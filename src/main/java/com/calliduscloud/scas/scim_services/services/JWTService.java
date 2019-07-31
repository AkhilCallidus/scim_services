package com.calliduscloud.scas.scim_services.services;

//import com.calliduscloud.commons.encoder.Encoder;
import com.calliduscloud.scas.scim_services.exception.InvalidClientDetailsException;
import com.calliduscloud.scas.scim_services.exception.JwtTokenEmptyException;
import com.calliduscloud.scas.scim_services.model.Environment;
import com.calliduscloud.scas.scim_services.model.Tenant;
import com.calliduscloud.scas.scim_services.model.User;
import com.calliduscloud.scas.scim_services.model.UserKey;
import com.calliduscloud.scas.scim_services.dao.EnvironmentDao;
import com.calliduscloud.scas.scim_services.dao.TenantDao;
import com.calliduscloud.scas.scim_services.dao.UserDao;
import com.calliduscloud.scas.scim_services.response.JWTRequest;
import com.calliduscloud.scas.scim_services.response.JWTResponse;
import com.calliduscloud.scas.scim_services.util.JWTUtils;
import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class JWTService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);
    private static final String TOKEN_TYPE = "Bearer";
    private static final String TENANT_ID = "tenantId";
    private static final String ENVIRONMENT_ID = "environmentId";
    private static final String USER_ID = "userId";
    private static final String LOGIN_USER_NAME = "loginUserName";
    private static final String BASIC = "Basic ";
    private static final String REGEX = ":";
    private static final String REPLACEMENT = "";
    private static final int INT = 2;
    private static final String COMPANY_ID = "companyId";
    private static final String CLIENT_ID = "clientId";
    private static final String ISSUED_FOR = "issuedFor";
    private static final String CALLIDUS = "Callidus";
    private static final int TIME_MILLIS = 1000;

    private UserDao userDao;
    private TenantDao tenantDao;
    private EnvironmentDao environmentDao;
    private JWTUtils jwtUtils;

    @Autowired
    private JWTService(UserDao userDao, TenantDao tenantDao, EnvironmentDao environmentDao, JWTUtils jwtUtils) {
        this.userDao = userDao;
        this.tenantDao = tenantDao;
        this.environmentDao = environmentDao;
        this.jwtUtils = jwtUtils;
    }

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * @param jwtRequest request has loginUserId and tenantId that is coming from SAC.
     * @param authorizationToken BASE64 Encoded Client Id and Client Secret.
     * @param map
     * @return jwtResponse It has access_token, expiry_in and token_type in it.
     * @throws InvalidClientDetailsException Invalid User Details, Authentication Failed.
     */
    public JWTResponse createToken(JWTRequest jwtRequest, String authorizationToken, Map<String, String> map)
            throws InvalidClientDetailsException {
        LOGGER.debug("Passed in jwtRequest:" + jwtRequest.toString());
//        String environmentName = jwtRequest.getTenantId();
        String clientEnvironmentId = jwtRequest.getSacTenantId();
        Environment environment = environmentDao.findByClientEnvironmentId(clientEnvironmentId);
        if (environment == null) {
            throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_IN_ERROR_DESCRIPTION,
                    ResponseCodeConstants.JWT_CLIENT_INPUT_ERROR_CODE);
        }

        UserKey userKey = new UserKey(environment.getTenantId(), jwtRequest.getLoginUserId());
        User loginUser = userDao.findByUserKey(userKey);

        if (!isValidUser(authorizationToken, loginUser, jwtRequest, map)) {
            throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_INPUT_ERROR_DESCRIPTION,
                    ResponseCodeConstants.JWT_CLIENT_INPUT_ERROR_CODE);
        }

        Date expirationDate = jwtUtils.generateExpirationDate();
        Tenant tenant = tenantDao.findByTenantId(environment.getTenantId());
        JWTResponse jwtResponse = new JWTResponse();
        Map<String, Object> claims = new HashMap<>();
        claims.put(TENANT_ID, loginUser.getUserKey().getTenantId());
        claims.put(ENVIRONMENT_ID, environment.getEnvironmentId());
        claims.put(USER_ID, loginUser.getUserKey().getUserId());
        claims.put(LOGIN_USER_NAME, loginUser.getUserName());
        claims.put(COMPANY_ID, environment.getEnvironmentName());
        claims.put(CLIENT_ID, tenant.getSacClientId());
        claims.put(ISSUED_FOR, CALLIDUS);
        claims.put("environmentHostName", environment.getEnvironmentHostName());
        claims.put("sacTenantId", environment.getClientEnvironmentId());
        claims.put("sacTenantUrl", jwtRequest.getSacTenantUrl());
        claims.put("appType", jwtRequest.getTenantId());
        jwtResponse.setAccessToken(jwtUtils.generateToken(claims, expirationDate, loginUser.getUserName()));

        jwtResponse.setExpiresIn(Math.toIntExact(expiration / TIME_MILLIS));  // token expiration time in seconds
        jwtResponse.setTokenType(TOKEN_TYPE);

        return jwtResponse;
    }

    /**
     * @param authorizationToken
     * @param loginUser
     * @param jwtRequest
     * @return
     * @throws Exception
     */
    private boolean isValidUser(String authorizationToken, User loginUser, JWTRequest jwtRequest,
                                Map<String, String> map) throws InvalidClientDetailsException {
        String reqClientId = "";
        String reqSecret = "";
        try {
           if (null != map && map.get("sacRequest").equalsIgnoreCase("true")) {
               reqClientId = map.get("userName");
               reqSecret = map.get("password");
           } else {
               String authString = authorizationToken.replace(BASIC, REPLACEMENT);
               byte[] decode = Base64.getDecoder().decode(authString);
               String str = new String(decode, StandardCharsets.UTF_8);
               String[] split = str.split(REGEX);
               if (split.length == INT) {
                   reqClientId = split[0];
                   reqSecret = split[1];
               }
           }
            if (null == loginUser) {
                return false;
            }
            BigInteger jwtRequestTenantId = environmentDao.
                    findByClientEnvironmentId(jwtRequest.getSacTenantId()).getTenantId();
            if (null != loginUser.getUserKey().getUserId()
                    && (loginUser.getUserKey().getTenantId()
                    .compareTo(jwtRequestTenantId) != 0)) {
                return false;
            }
            Tenant tenant = tenantDao.findByTenantId(jwtRequestTenantId);
            if (!tenant.getSacClientSecret().equals(reqSecret)
                    || !tenant.getSacClientId().equals(reqClientId)) {
                throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_ERROR_DESCRIPTION,
                        ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception occurs while validating user ");
            throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_ERROR_DESCRIPTION,
                    ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
        }
        return true;
    }

    public JWTResponse createIpsToken(String authHeader) throws InvalidClientDetailsException {

        JWTResponse ipsJwtResponse = null;
        boolean flag = false;
        try {
            Map<String, String> idMap = getReqClientIdAndReqSecret(authHeader);
            if (idMap != null && !idMap.isEmpty()) {
                List<Tenant> tenantList = tenantDao.findByIpsClientIdAndIpsClientSecret(idMap.get("reqClientId"),
                        idMap.get("reqSecret"));
                if (tenantList != null && !tenantList.isEmpty()) {
                    Tenant tenant = tenantList.get(0);
                    Date expirationDate = jwtUtils.generateExpirationDate();
                    ipsJwtResponse = new JWTResponse();
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("tenantId", tenant.getTenantId());
                    claims.put("clientId", tenant.getIpsClientId());
                    ipsJwtResponse.setAccessToken(jwtUtils.generateToken(claims, expirationDate,
                            tenant.getTenantName()));
                    ipsJwtResponse.setExpiresIn(Math.toIntExact(expiration / TIME_MILLIS));
                    ipsJwtResponse.setTokenType(TOKEN_TYPE);
                } else {
                    LOGGER.error("Could not find tenantList for clientId " + idMap.get("reqClientId"));
                    flag = true;
                }
            } else {
                flag = true;
                LOGGER.error("createIpsToken :: could not get clientId and "
                        + "clientSecret from getReqClientIdAndReqSecret() ");
            }
            if (flag) {
                throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_ERROR_DESCRIPTION,
                        ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
            }
        } catch (Exception ex) {
            LOGGER.error(" Invalid clientId and clientSecret ");
            throw new InvalidClientDetailsException(ResponseCodeConstants.JWT_CLIENT_ERROR_DESCRIPTION,
                    ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
        }
        return ipsJwtResponse;
    }


    private Map<String, String> getReqClientIdAndReqSecret(String authHeader) throws Exception {
        Map<String, String> idMap = new HashMap<>();
        try {
            String authString = authHeader.replace(BASIC, REPLACEMENT);
            byte[] decode = Base64.getDecoder().decode(authString);
            String str = new String(decode, StandardCharsets.UTF_8);
            String[] split = str.split(REGEX);
            if (split.length == INT) {
                idMap.put("reqClientId", split[0]);
                idMap.put("reqSecret", split[1]);
            }
        } catch (Exception e) {
            LOGGER.error("Error while getting client id and client secret from header ");
            throw new Exception();
        }
        return idMap;
    }

    public BigInteger getTenantFromJWT(String jwt) throws JwtTokenEmptyException {
        LOGGER.info("getTenantId :: access_token : " + jwt);
        return jwtUtils.getTenantIdFromJWT(jwt);
    }


}
