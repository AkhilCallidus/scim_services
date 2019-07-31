package com.calliduscloud.scas.scim_services.controller;

import com.calliduscloud.scas.scim_services.exception.InvalidClientDetailsException;
import com.calliduscloud.scas.scim_services.response.ErrorResponse;
import com.calliduscloud.scas.scim_services.response.JWTRequest;
import com.calliduscloud.scas.scim_services.response.JWTResponse;
import com.calliduscloud.scas.scim_services.services.JWTService;
import com.calliduscloud.scas.scim_services.util.JWTUtils;
import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Akhil Pathipaka
 *
 * URL route:  http://{{host_name}}:{{port_number}}.
 * <h2>
 * JWT Controller is responsible for accept and process any JWT related requests.
 * The main feature of this controller is to provide token services like
 * tokenCreation and tokenValidation for the end user.
 * It return the JWTTokenResponse and IPSTokenResponse
 * which includes access_token, expiry_in and token_type.
 * </h2>
 */
@RestController
public class JWTController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTController.class);
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String BASIC = "Basic ";
    private static final String REPLACEMENT = "";
    private static final String API_AUTH_REST_V_1_VALIDATE = "/api/auth/rest/v1/validate";
    private static final String API_AUTH_REST_V_1_TOKEN = "/api/auth/rest/v1/token";
    private static final String API_AUTH_IPS_TOKEN = "/api/auth/ips/token";
    private static final String API_OAUTH_REST_V_1_TOKEN = "/api/oauth/rest/v1/token";
    private static final String API_OAUTH_REST_V_1_VALIDATE = "/api/oauth/rest/v1/validate";
    private static final String APPLICATION_JSON = "application/json";
    private static final int UAE = 401;
    private static final String API_SCAI_REST_V_1_TOKEN = "/api/scai/rest/v1/token";
    private static final String API_SCAI_REST_V_1_VALIDATE = "/api/scai/rest/v1/validate";

    private JWTService jwtService;
    private JWTUtils jwtUtils;

    @Autowired
    private JWTController(JWTService jwtService, JWTUtils jwtUtils) {
        this.jwtService = jwtService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * <pre>
     *     This service is to provide the JWTToken to end user as per the request from SAC.
     *     It has the following responsibilities.
     *     Based on clientId, clientSecret, loginUserId and tenantId it will construct the token
     *     which is unique across all the users. This will sent to /validate for further verification.
     *
     * @param jwtRequest Jwt Request has loginUserId and tenantId along with
     *                   clientId and clientSecret that is coming from SAC.
     * @param httpServletRequest Http Servlet Request
     * @param response Http Servlet Response
     * @return jwtResponse JwtResponse has access_token, expiry_in and token_type.
     *                     access_token is used for validating the end user.
     *
     *</pre>
     * <pre>
      Token Request:
      curl -X POST \
        http://{{host_name}}:{{port_number}}/api/auth/rest/v1/token \
        -H 'Authorization: Basic VDAwMDAwMDpWZW5rYXQxMjM=' \
        -H 'Content-Type: application/json' \
        -H 'Postman-Token: 3e3d5fc2-2fcb-4b6a-94a2-4e329407f506' \
        -H 'cache-control: no-cache' \
        -d '{
       "loginUserId": "P000039",
       "tenantId": "CALLIDUS_DEV"
       }'

      Token Creation Response:
      {
          "access_token": "{{SAC_TOKEN}}",
          "expires_in": 10800,
          "token_type": "Bearer"
      }

      Access_Token Payload:
      HEADER: (ALGORITHM  TOKEN TYPE)
           {
             "typ": "JWT",
             "alg": "RS256"
           }

      PAYLOAD:
           {
             "loginUserName": "test_user2",
             "tenantId": 2,
             "loginUserId": "P000039",
             "iat": 1551303593,
             "jti": "ed3f5fbc-bdf1-43fc-97df-2f87a5863784",
             "exp": 1551303893,
             "nbf": 1551303593,
             "aud": "sac_backend",
             "iss": "sacservices.calliduscloud.com",
             "sub": "test_user2"
           }

      SIGNATURE:
            RSASHA256(
            base64UrlEncode(header) + "." +
            base64UrlEncode(payload),
            public_key)
    </pre>
     */

    @RequestMapping(value = API_AUTH_REST_V_1_TOKEN, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public Object token(@RequestBody JWTRequest jwtRequest,
                        HttpServletRequest httpServletRequest, HttpServletResponse response) {
        LOGGER.info("JWTController.jwtResponse : entry : SAC token");
        Object jwtResponse = null;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("sacRequest", "false");
            String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                jwtResponse = jwtService.createToken(jwtRequest, authorizationHeader, map);
                LOGGER.info("SAC Token Created.");
            } else {
                LOGGER.error("client id and client secret are not valid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jwtResponse = new ErrorResponse();
                ((ErrorResponse) jwtResponse).setError(ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                ((ErrorResponse) jwtResponse).setErrorDescription(ResponseCodeConstants.JWT_CLIENT_ERROR_DESCRIPTION);
                ((ErrorResponse) jwtResponse).setStatus(UAE);
            }
            LOGGER.info("Successfully created SAC Token");
        } catch (InvalidClientDetailsException ex) {
            LOGGER.error("Invalid Client Details ");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jwtResponse = new ErrorResponse();
            ((ErrorResponse) jwtResponse).setError(ex.getErrorCode());
            ((ErrorResponse) jwtResponse).setErrorDescription(ex.getErrorDescription());
            ((ErrorResponse) jwtResponse).setStatus(UAE);
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating a token   ", e);
        }
        return jwtResponse;
    }

    /**
     <pre>
     *  ipsJwtRequest uses ipsClientId and ipsClientSecret for generating
     *  token response.
     *  With access_token from ipsJwtResponse we are going to sync down
     *  users from IAS to SCIM system(Config DB).
     * @param httpServletRequest Http Servlet Request
     * @param response Http Servlet Response
     * @return ipsjwtResponse IpsJwtResponse has access_token, expiry_in
     * and token_type. access_token is used for validating the end user
     * and to do CRUD for User Services.
     *</pre>
     *<pre>
      Token Request:
            curl -X POST \
            http://{{host_name}}:{{port_number}}/api/auth/ips/token \
            -H 'Authorization: Basic VDAwMDAwMzpWZW5rYXQxMjM=' \
            -H 'Postman-Token: a723268a-a081-40c7-a6b2-e78b4e7fed9e' \
            -H 'cache-control: no-cache'

      Token Creation Response:
           {
           "access_token": "{{IPS_TOKEN}}",
           "expires_in": 10800,
           "token_type": "Bearer"
           }
      Access_Token Payload:
          HEADER: (ALGORITHM, TOKEN TYPE)
               {
               "typ": "JWT",
               "alg": "RS256"
               }

          PAYLOAD:
               {
               "clientId": "T000003",
               "tenantId": 3,
               "iat": 1555102867,
               "jti": "403d99b0-037b-4360-b766-97946f3ba374",
               "exp": 1555113667,
               "nbf": 1555102867,
               "aud": "sac_backend",
               "iss": "sacservices.calliduscloud.com",
               "sub": "Calliduscloud"
               }

          SIGNATURE:
               RSASHA256(
               base64UrlEncode(header) + "." +
               base64UrlEncode(payload),
               public_key)
    </pre>
     */
    @RequestMapping(value = API_AUTH_IPS_TOKEN, method = RequestMethod.POST)
    public Object ipsJwtResponse(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        LOGGER.info("JWTController.ipsJwtResponse : entry : IPS token");
        Object ipsJwtResponse = null;
        try {
            String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
            if (null != authHeader && !authHeader.isEmpty()) {
                String jwt = authHeader.replace(BASIC, REPLACEMENT);
                ipsJwtResponse = jwtService.createIpsToken(jwt);
                LOGGER.info("IPS Token Created.");
            } else {
                LOGGER.error("Client id and Client secret are not valid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ipsJwtResponse = new ErrorResponse();
                ((ErrorResponse) ipsJwtResponse).setError(ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                ((ErrorResponse) ipsJwtResponse).setErrorDescription(ResponseCodeConstants.
                        JWT_CLIENT_ERROR_DESCRIPTION);
                ((ErrorResponse) ipsJwtResponse).setStatus(UAE);
            }
            LOGGER.info("Successfully created IPS Token");
        } catch (InvalidClientDetailsException ex) {
            LOGGER.error(" InValid Client Details ", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ipsJwtResponse = new ErrorResponse();
            ((ErrorResponse) ipsJwtResponse).setError(ex.getErrorCode());
            ((ErrorResponse) ipsJwtResponse).setErrorDescription(ex.getErrorDescription());
            ((ErrorResponse) ipsJwtResponse).setStatus(UAE);
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating a IPS token ", e);
        }
        return ipsJwtResponse;
    }

    @RequestMapping(value = API_OAUTH_REST_V_1_TOKEN, method = RequestMethod.POST)
    public Object oauthToken(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("JWTController.oauthJwtResponse : entry : oauth token ");
        Object oauthJwtResponse = null;
        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            if (null != authHeader && !authHeader.isEmpty()) {
                String jwt = authHeader.replace(BASIC, REPLACEMENT);
//                oauthJwtResponse = jwtService.createOauthToken(jwt);
                LOGGER.info("OAuth Token Created.");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                oauthJwtResponse = new ErrorResponse();
                ((ErrorResponse) oauthJwtResponse).setError(ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                ((ErrorResponse) oauthJwtResponse).setErrorDescription(ResponseCodeConstants.
                        JWT_CLIENT_ERROR_DESCRIPTION);
                ((ErrorResponse) oauthJwtResponse).setStatus(UAE);
            }
            LOGGER.info("Successfully created OAuth Token");
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating a IPS token ", e);
        }
        return null;
    }

    /**
     * This method is used to validate the token coming from SAC browser using
     * public key and returns required params which is used for further
     * processing like calling other services to get the required data.
     *
     * @param httpServletRequest is used to pass token as bearer with
     *                          Authorization header for validation.
     * @param response Http Servlet Response
     * @return End user's userId, companyId, clientId and other public claims.
     *
     Token Verification Request:
      curl -X GET \
       http://{{host_name}}:{{port_number}}/api/auth/rest/v1/validate \
       -H 'Authorization: Bearer {{SAC_TOKEN}}'
           \
       -H 'Postman-Token: 8929f6cf-cb56-406d-962b-f980c11267bf' \
       -H 'cache-control: no-cache'

     Token Validation Response:
        {
        "expiresIn": 1555025359,
        "companyId": "CALLIDUS-TST",
        "clientId": "T000000",
        "issuedFor": "Callidus",
        "issuedAt": 1555014559,
        "userId": "bgillell"
        }
     */
    @RequestMapping(value = API_AUTH_REST_V_1_VALIDATE, method = RequestMethod.GET)
    public Object validateSacToken(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        LOGGER.info("JWTController  :: sac.validateToken : entry ");
        Object data = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                String jwt = authorizationHeader.replace(BEARER, REPLACEMENT);
                data = jwtUtils.validateSacToken(jwt);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                data = new ErrorResponse();
                ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
                ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION);
                ((ErrorResponse) data).setStatus(UAE);
            }
            LOGGER.info("Successfully validated SAC Token");
        } catch (SignatureException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_SIGNATURE_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_SIGNATURE_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_EXPIRED_JWT_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_EXPIRED_JWT_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (MalformedJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_MALFORMED_ERROR_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_MALFORMED_ERROR_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (UnsupportedJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_UNSUPPORTED_JWT_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_UNSUPPORTED_JWT_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_ILLEGAL_TOKEN_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_ILLEGAL_TOKEN_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (InvalidKeySpecException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_KEY);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_KEY_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (Exception e) {
            LOGGER.error("Error occurred while validating a SAC Token.", e);
        }
        return data;
    }

    @RequestMapping(value = API_OAUTH_REST_V_1_VALIDATE, method = RequestMethod.GET)
    public Object verify(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        LOGGER.info("JWTController :: oauth.validateToken : entry ");
        Object data = null;
        Map<String, Object> verifyRespMap = new HashMap<>();
        try {
            String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                String jwt = authorizationHeader.replace(BEARER, REPLACEMENT);
                JWTResponse jwtResponse = jwtUtils.validateOauthToken(jwt);
                verifyRespMap.put("isValid", jwtResponse.getSuccess());
                data = verifyRespMap;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                data = new ErrorResponse();
                ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
                ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION);
                ((ErrorResponse) data).setStatus(UAE);
                LOGGER.error("Authorization Header is empty ");
            }
            LOGGER.info("Successfully validated OAuth Token");
        } catch (SignatureException ex) {
            LOGGER.error("Invalid Signature");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_SIGNATURE_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_SIGNATURE_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_EXPIRED_JWT_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_EXPIRED_JWT_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (InvalidKeySpecException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_KEY);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_KEY_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (Exception ex) {
            LOGGER.error("Error occurred while validating a OAuth Token.", ex);
        }
        return data;
    }

    /**
     * This end-point is for creating a sample Idp token only.
     * This should be used for the purpose of unit-testing and QA
     * @param jwtRequest JWTRequest
     * @param response HttpServletResponse
     * @return JSON object
     * <pre>
     *  POST /api/scai/rest/v1/token
     *  Request Body
     *  {
     *  "loginUserId": "P000063",
     *  "tenantId": "CALLIDUS-TST
     *  }
     *  Response
     *  {
     *  "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOiJQMDAwMDYzIi
     *  wibG9naW5Vc2VyTmFtZSI6InRlc3RfdXNlcl8wMiIsImNvbXBhbnlJZCI6IkNBTExJRFVTLVRTVCIsI
     *  mVudmlyb25tZW50SWQiOjUsImxvZ2luX25hbWUiOiJ0ZXN0X3VzZXJfMDIiLCJjbGllbnRJZCI6IlQw
     *  MDAwMDkiLCJpc3N1ZWRGb3IiOiJDYWxsaWR1cyIsInRlbmFudElkIjoyLCJ1c2VySWQiOiJQMDAwMDY
     *  zIiwiaWF0IjoxNTYwMTY3NTM5LCJqdGkiOiI0ZTUyOTg0OC0zMGM4LTRiNGYtOGU0My03Mjc1YTZlMD
     *  g5NWQiLCJleHAiOjE4NjAxNjc1MzksIm5iZiI6MTU2MDE2NzUzOSwiYXVkIjoiVDAwMDAwOSIsImlzc
     *  yI6InNjLWRldi0wMDAyLmFjY291bnRzLm9uZGVtYW5kLmNvbSIsInN1YiI6InRlc3RfdXNlcl8wMiJ9.
     *  jhz2Y2mMpVDgYU7TdYA8wgfw-9Z3nArj06NqoBQDMzAakiY92NQ_LWaw7KmTz7R3_-uQCV22A4KynLzF
     *  2J0vyD33LGeXIz7gGzB2WrrBRGRV2SO5ILW4UeD2oZ6AdDAy7DjLA1KEW8h91wUthRaDQ2vR65GVLdT
     *  yRgKKc9sC9LraUh6xPCsZ1_NeJi_WhZu2ECFY9sSytiiZqnUP0_7r08JeBbuypHvmsjJq85Pd4xQC_4
     *  AcTjpmYHOtN9eDUu4BxMjGAgGSX5CBUE5m5aP7FtpxhVMmbtLOKUk_54bWOfrX3J4MRidLsHLCEwcKY
     *  HT_sbR_vgyXyLIqXaAZOpY5yA",
     *  "expires_in": 300000000,
     *  "token_type": "Bearer"
     *  }
     * </pre>
     */
    @RequestMapping(value = API_SCAI_REST_V_1_TOKEN, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public Object sampleIdpToken(@RequestBody JWTRequest jwtRequest, HttpServletResponse response) throws Exception {
        LOGGER.info("JWTController.scaiTokenResponse : entry ");
        Object jwtResponse;
        try {
//                jwtResponse = jwtService.createSampleIdpToken(jwtRequest);
        }  catch (Exception e) {
            LOGGER.error("Error occurred while creating a token   ", e);
            throw e;
        }
        return null;
    }

    /**
     * This end-point is for validation of the sample IDP token only.
     * @param httpServletRequest HttpServletRequest
     * @param response HttpServletResponse
     * @return JSON object
     * <pre>
     * GET /api/scai/rest/v1/validate HTTP/1.1
     * HTTP Headers
     * Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.e
     * yJ1aWQiOiJQMDAwMDYzIiwibG9naW5Vc2VyTmFtZSI6InRlc3RfdXNlcl8wM
     * iIsImNvbXBhbnlJZCI6IkNBTExJRFVTLVRTVCIsImVudmlyb25tZW50SWQiO
     * jUsImxvZ2luX25hbWUiOiJ0ZXN0X3VzZXJfMDIiLCJjbGllbnRJZCI6IlQwM
     * DAwMDkiLCJpc3N1ZWRGb3IiOiJDYWxsaWR1cyIsInRlbmFudElkIjoyLCJ1c
     * 2VySWQiOiJQMDAwMDYzIiwiaWF0IjoxNTYwMTY3NTM5LCJqdGkiOiI0ZTUyO
     * Tg0OC0zMGM4LTRiNGYtOGU0My03Mjc1YTZlMDg5NWQiLCJleHAiOjE4NjAxN
     * jc1MzksIm5iZiI6MTU2MDE2NzUzOSwiYXVkIjoiVDAwMDAwOSIsImlzcyI6I
     * nNjLWRldi0wMDAyLmFjY291bnRzLm9uZGVtYW5kLmNvbSIsInN1YiI6InRlc
     * 3RfdXNlcl8wMiJ9.jhz2Y2mMpVDgYU7TdYA8wgfw-9Z3nArj06NqoBQDMzAa
     * kiY92NQ_LWaw7KmTz7R3_-uQCV22A4KynLzF2J0vyD33LGeXIz7gGzB2WrrB
     * RGRV2SO5ILW4UeD2oZ6AdDAy7DjLA1KEW8h91wUthRaDQ2vR65GVLdTyRgKK
     * c9sC9LraUh6xPCsZ1_NeJi_WhZu2ECFY9sSytiiZqnUP0_7r08JeBbuypHvm
     * sjJq85Pd4xQC_4AcTjpmYHOtN9eDUu4BxMjGAgGSX5CBUE5m5aP7FtpxhVMm
     * btLOKUk_54bWOfrX3J4MRidLsHLCEwcKYHT_sbR_vgyXyLIqXaAZOpY5yA
     * Response - on a successful validation
     * { "expiresIn": 1860167539,
     * "companyId": "CALLIDUS-TST",
     * "clientId": "T000009",
     * "issuedFor": "sc-dev-0002.accounts.ondemand.com",
     * "userId": "test_user_02"
     * }
     * </pre>
     */
    @RequestMapping(value = API_SCAI_REST_V_1_VALIDATE, method = RequestMethod.GET)
    public Object validateIdpToken(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        LOGGER.info("JWTController :: scai.validateToken : entry ");
        Object data = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                String jwt = authorizationHeader.replace(BEARER, REPLACEMENT);
                data = jwtUtils.validateIdpToken(jwt);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                data = new ErrorResponse();
                ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
                ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION);
                ((ErrorResponse) data).setStatus(UAE);
            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid Signature", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_SIGNATURE_CODE);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_SIGNATURE_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (InvalidKeySpecException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = new ErrorResponse();
            ((ErrorResponse) data).setError(ResponseCodeConstants.JWT_INVALID_KEY);
            ((ErrorResponse) data).setErrorDescription(ResponseCodeConstants.JWT_INVALID_KEY_DESCRIPTION);
            ((ErrorResponse) data).setStatus(UAE);
        } catch (Exception ex) {
            LOGGER.error("Error occurred while validating a token.", ex);
        }
        return data;
    }
}
