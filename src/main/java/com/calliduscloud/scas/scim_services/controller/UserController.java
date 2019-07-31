package com.calliduscloud.scas.scim_services.controller;

import com.calliduscloud.scas.scim_services.exception.JwtTokenEmptyException;
import com.calliduscloud.scas.scim_services.exception.UserNameAlreadyExistException;
import com.calliduscloud.scas.scim_services.extrautils.WebApplicationErrorHandler;
import com.calliduscloud.scas.scim_services.model.UserKey;
import com.calliduscloud.scas.scim_services.services.JWTService;
import com.calliduscloud.scas.scim_services.services.UserService;
import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
import com.calliduscloud.scas.scim_services.util.SCIMResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * URL route http://{{host_name}}:{{port_number}}/scim/v2/Users.
 * <h2>
 * UserService will handle all User related operations
 * such as createUser, updateUser, deleteUser and find User.
 * </h2>
 */
@Controller
@ComponentScan("com.calliduscloud.scas")
@RequestMapping("/scim/v2/Users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static final String APPLICATION_SCIM_JSON = "application/scim+json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String USER_404_001 = "USER_404_001";
    private static final String USER_500_002 = "USER_500_002";
    private static final String USER_500_001 = "USER_500_001";

    private UserService userService;
    private JWTService jwtService;
    private SCIMResponseUtil scimResponseUtil;

    @Autowired
    private UserController(UserService userService, JWTService jwtService, SCIMResponseUtil scimResponseUtil) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.scimResponseUtil = scimResponseUtil;
    }

    /**
     * <pre>
     * This is the method which Retrieve a single User by @param userKey.
     * @param userId userId
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @return respMap It gives json SCIMResponse
     * </pre>
     * <pre>
     * Request Payload :
     * GET /scim/v2/Users/{{id}}
     * Host: {{host_name}}:{{port_number}}
     * Content-Type: application/scim+json
     * Authorization: Bearer {{ips_token}}
     * </pre>
     * <pre>
     * Response Payload :
     * HTTP/1.1 200 OK
     * Content-Type: application/scim+json
     * {
     *     "meta": {
     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095",
     *         "lastModified": "2019-07-12T01:53:52.192+0000",
     *         "resourceType": "User"
     *     },
     *     "schemas": [
     *         "urn:ietf:params:scim:schemas:core:2.0:User"
     *     ],
     *     "groups": [
     *         {
     *             "ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c74904708ba3425d48ea7",
     *             "value": "5c74904708ba3425d48ea784",
     *             "display": "ADMINISTRATOR_COMM-SCAI"
     *         },
     *         {
     *             "ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c7490354fa1bc25fcb916",
     *             "value": "5c7490354fa1bc25fcb9162c",
     *             "display": "ADMINISTRATOR_CPQ-SCAI"
     *         },
     *         {
     *             "ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c748fe0ac44762660a267",
     *             "value": "5c748fe0ac44762660a26763",
     *             "display": "ADMINISTRATOR_COMM-SCAN"
     *         }
     *     ],
     *     "externalId": "P000095",
     *     "id": "P000095",
     *     "userName": "april.zhu"
     * }
     * </pre>
     * <pre>
     * <b>Error Codes: </b>
     * HTTP 400:
     * Error Code: CODE_BAD_REQUEST = 400;
     * Error Description: DESC_USER_BAD_ID_REQUEST = "missing parameter externalId."
     *
     * HTTP 401:
     * Error Code: CODE_UNAUTHORIZED = 401;
     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
     *             + "missing."
     *
     * HTTP 404:
     * Error Code: CODE_RESOURCE_NOT_FOUND = 404
     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
     * Error Code: USER_404_001= User Data Not Found at the User Service.
     *
     * HTTP 500:
     * Error Code: USER_500_001 = Internal server error at the User Service.
     * Error Code: USER_500_002 = User Service error.
     * </pre>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_SCIM_JSON)
    public @ResponseBody
    Object getUser(@PathVariable(value = "id") String userId, HttpServletRequest request,
                   HttpServletResponse response) {
        LOG.info("UserController:getUser " + userId);
        BigInteger tenantId;
        Map respMap = null;
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                tenantId = jwtService.getTenantFromJWT(authorizationHeader);
                UserKey userKey = new UserKey(tenantId, userId);
                if (null == userKey.getUserId() || userKey.getUserId().isEmpty()) {
                    LOG.error("getUser :: externalId is missing...");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_ID_REQUEST,
                            Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
                }
                Map<String, Object> userMap = userService.getUser(tenantId, userId);
                respMap = scimResponseUtil.toSCIMResource(userMap);
            } else {
                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return userService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
            }
            LOG.info("getTenantIdFromJWT ::   " + tenantId);
            LOG.info("User : " + userId);
        } catch (JwtTokenEmptyException ex) {
            LOG.error("JWT signature does not match locally computed signature", ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        } catch (UsernameNotFoundException ex) {
            LOG.error("ErrorCode : USER_404_001 : ", ex);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return userService.scimError("Resource " + userId + " not found.",
                    Optional.of(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND));
        } catch (HttpServerErrorException.InternalServerError internalServerError) {
            LOG.error(internalServerError + "ErrorCode : USER_500_001");
            WebApplicationErrorHandler.raiseError(USER_500_001);
        } catch (Exception e) {
            LOG.error("ErrorCode : USER_500_002", e);
            WebApplicationErrorHandler.raiseError(USER_500_002);
        }
        return respMap;
    }

    /**
     * <pre>
     * This is the method which Retrieve all the Users specific to tenantId.
     * @param params request user payload
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @return respMap It gives json SCIMResponse
     * </pre>
     * <pre>
     * Request Payload :
           GET /scim/v2/Users/ HTTP/1.1
           Host: {{host_name}}:{{port_number}}
           Authorization: Bearer {{ips_token}}
      </pre>
     * <pre>
     * Response Payload :
     * HTTP/1.1 200 OK
     * Content-Type: application/scim+json
     *
     * {
     *     "totalResults": 321,
     *     "startIndex": 1,
     *     "itemsPerPage": 10,
     *     "schemas": [
     *         "urn:ietf:params:scim:api:messages:2.0:ListResponse"
     *     ],
     *     "Resources": [
     *         {
     *             "meta": {
     *                 "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095",
     *                 "lastModified": "2019-07-12T01:53:52.192+0000",
     *                 "resourceType": "User"
     *             },
     *             "schemas": [
     *                 "urn:ietf:params:scim:schemas:core:2.0:User"
     *             ],
     *             "groups": [
     *                 {
     *                     "ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c749047082212",
     *                     "value": "5c74904708ba3425d48ea784",
     *                     "display": "ADMINISTRATOR_COMM-SCAI"
     *                 },
     *       ...............
     *       ...............
     * </pre>
     * <pre>
     * <b>Error Codes: </b>
     * HTTP 401:
     * Error Code: CODE_UNAUTHORIZED = 401;
     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
     *             + "missing."
     * HTTP 404:
     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
     * Error Code: USER_404_001= User Data Not Found at the User Service.
     *
     * HTTP 500:
     * Error Code: USER_500_001 = Internal server error at the User Service.
     * Error Code: USER_500_002 = User Service error.
     * </pre>
     */
    @RequestMapping(method = RequestMethod.GET, produces = APPLICATION_SCIM_JSON)
    public @ResponseBody
    Map getAllUsers(@RequestParam Map<String, String> params, HttpServletRequest request,
                    HttpServletResponse response) {
        LOG.info("UserController : getAllUsers : entry");
        Map<String, Object> respMap = new HashMap<>();
        BigInteger tenantId;
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                tenantId = jwtService.getTenantFromJWT(authorizationHeader);
                LOG.info("getTenantIdFromJWT ::   " + tenantId);
                respMap = userService.getAllUsers(params, tenantId);
            } else {
                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return userService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
            }
        } catch (JwtTokenEmptyException ex) {
            LOG.error("JWT signature does not match locally computed signature", ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        } catch (HttpClientErrorException.NotFound userNotFound) {
            LOG.error(userNotFound + "ErrorCode : " + USER_404_001);
            WebApplicationErrorHandler.raiseError(USER_404_001);
        } catch (HttpServerErrorException.InternalServerError internalServerError) {
            LOG.error(internalServerError + "ErrorCode : " + USER_500_001);
            WebApplicationErrorHandler.raiseError(USER_500_001);
        } catch (Exception e) {
            LOG.error("Exception while getting all users ", e);
            WebApplicationErrorHandler.raiseError(USER_500_002);
        }
        return scimResponseUtil.toScimUsersResources(respMap);
    }

    /**
     * <pre>
     * This is the method which Create User.
     * @param params request user payload
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @return respMap It gives json SCIMResponse
     * </pre>
     * <pre>
     * Request Payload :
     * POST /scim/v2/Users/ HTTP/1.1
     * Host: {{host_name}}:{{port_number}}
     * Authorization: Bearer {{ips_token}}
     * Content-Type: application/scim+json
     *{
     *   "userName":"akhil+10@calliduscloud.com",
     *   "schemas": [
     *   "urn:ietf:params:scim:schemas:core:2.0:User"
     *   ],
     *   "externalId":"A000010"
     *}
     * </pre>
     * <pre>
     * Response Payload :
     * HTTP/1.1 201 Created
     * Content-Type: application/scim+json
     *{
     *     "meta": {
     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/A000010",
     *         "lastModified": "2019-07-17T20:25:07.160+0000",
     *         "resourceType": "User"
     *     },
     *     "schemas": [
     *         "urn:ietf:params:scim:schemas:core:2.0:User"
     *     ],
     *     "externalId": "A000010",
     *     "groups": [],
     *     "id": "A000010",
     *     "userName": "akhil+10@calliduscloud.com"
     * }
     * </pre>
     * <pre>
     * <b>Error Codes: </b>
     * HTTP 400:
     * Error Code: CODE_BAD_REQUEST = 400;
     * Error Description: DESC_USER_BAD_ID_REQUEST = "missing parameter externalId."
     * Error Description: DESC_USER_BAD_NAME_REQUEST = "missing parameter userName."
     * USER_400_003 = Bad data received. User not created.
     *
     * HTTP 401:
     * Error Code: CODE_UNAUTHORIZED = 401;
     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
     *             + "missing."
     *
     * HTTP 404:
     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
     *
     * HTTP 409:
     * Error Code: CODE_CONFLICT = 409;
     * Error Description: USER_409_001 = User Conflict at the User Service.
     * Error Description: USER_409_002 = User Already exists.
     * Error Description: DESC_CONFLICT = "User Already existed in the system."
     *
     * HTTP 500:
     * Error Code: USER_500_001 = Internal server error at the User Service.
     * Error Code: USER_500_002 = User Service error.
     * </pre>
     */
    @PostMapping(produces = APPLICATION_SCIM_JSON)
    public @ResponseBody
    Object create(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("UserController:create :params" + params);
        LOG.info("params : " + params);
        BigInteger tenantId;
        Map respMap = new HashMap<>();

        if ((null == params.get("externalId") || ((String) params.get("externalId")).isEmpty())) {
            LOG.error("create :: id is missing...");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_ID_REQUEST,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        }
        if ((null == params.get("userName") || ((String) params.get("userName")).isEmpty())) {
            LOG.error("create :: username is missing..");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_NAME_REQUEST,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        } else {
            try {
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                    tenantId = jwtService.getTenantFromJWT(authorizationHeader);
                    LOG.info("getTenantIdFromJWT ::   " + tenantId);
                    Map<String, Object> userMap = userService.addUser(params, tenantId);
                    LOG.info("User " + params.get("externalId") + " is Created ");
                    respMap = scimResponseUtil.toSCIMResource(userMap);
                } else {
                    LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return userService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
                            Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
                }
            } catch (JwtTokenEmptyException | com.calliduscloud.scas.scim_services.response.exception.UserAlreadyExistException | com.calliduscloud.scas.scim_services.response.exception.UserNameAlreadyExistException | com.calliduscloud.scas.scim_services.response.exception.InvalidUserIdException | com.calliduscloud.scas.scim_services.response.exception.InvalidUserNameFoundException ex) {
                LOG.error("JWT signature does not match locally computed signature", ex);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return userService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
            } catch (HttpServerErrorException.InternalServerError internalServerError) {
                LOG.error(internalServerError + "ErrorCode : USER_500_001");
                WebApplicationErrorHandler.raiseError(USER_500_001);
            }
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        return respMap;
    }

    /**
     * <pre>
     * This is the method which Update User by @param userKey.
     * @param userId externalId
     * @param params request user payload
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @return respMap It gives json SCIMResponse
     * </pre>
     * <pre>
     * Request Payload :
     * PUT /scim/v2/Users/{{id}}
     * Host: {{host_name}}:{{port_number}}
     * Content-Type: application/scim+json
     * Authorization: Bearer {{ips_token}}
     * {
     *   "userName": "akhil+11@calliduscloud.com",
     *   "schemas": [
     *     "urn:ietf:params:scim:schemas:core:2.0:User"
     *   ]
     * }
     * </pre>
     * <pre>
     * Response Payload :
     * HTTP/1.1 200 OK
     * Content-Type: application/scim+json
     * {
     *     "meta": {
     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/A000010",
     *         "lastModified": "2019-07-17T21:05:23.318+0000",
     *         "resourceType": "User"
     *     },
     *     "schemas": [
     *         "urn:ietf:params:scim:schemas:core:2.0:User"
     *     ],
     *     "externalId": "A000010",
     *     "groups": [],
     *     "id": "A000010",
     *     "userName": "akhil+11@calliduscloud.com"
     * }
     * </pre>
     * <pre>
     * <b>Error Codes: </b>
     * HTTP 400:
     * Error Code: CODE_BAD_REQUEST = 400;
     * Error Description: DESC_USER_BAD_ID_REQUEST = "missing parameter externalId."
     * Error Description: DESC_USER_BAD_NAME_REQUEST = "missing parameter userName."
     * USER_400_003 = Bad data received. User not created.
     *
     * HTTP 401:
     * Error Code: CODE_UNAUTHORIZED = 401;
     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
     *             + "missing."
     *
     * HTTP 404:
     * Error Code: CODE_RESOURCE_NOT_FOUND = 404;
     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
     *
     * HTTP 500:
     * Error Code: USER_500_001 = Internal server error at the User Service.
     * Error Code: USER_500_002 = User Service error.
     * </pre>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = APPLICATION_SCIM_JSON)
    public @ResponseBody
    Object update(@PathVariable(value = "id") String userId,
                  @RequestBody Map<String, Object> params, HttpServletRequest request,
                  HttpServletResponse response) throws UserNameAlreadyExistException {
        LOG.info("UserController: update: params" + params);
        Map respMap = new HashMap<>();
        UserKey userKey = null;
        BigInteger tenantId;
//        if ((null == params.get("id") || ((String) params.get("id")).isEmpty())
//                || (null == params.get("userName") || ((String) params.get("userName")).isEmpty())
//                || !(params.get("id").equals(userId))) {
        if ((null == params.get("userName") || ((String) params.get("userName")).isEmpty())) {
            LOG.error("create :: username is missing..");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_NAME_REQUEST,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        } else {
            try {
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                    tenantId = jwtService.getTenantFromJWT(authorizationHeader);
                    LOG.info("getTenantIdFromJWT ::   " + tenantId);
                    userKey = new UserKey(tenantId, userId);
                    if (null == userKey.getUserId() || userKey.getUserId().isEmpty()) {
                        LOG.error("update :: externalId is missing...");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_ID_REQUEST,
                                Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
                    }
                    Map<String, Object> userMap = userService.updateUser(userKey.getTenantId(),
                            userKey.getUserId(), params);
                    respMap = scimResponseUtil.toSCIMResource(userMap);
                    LOG.info("User " + userId + " is Updated ");
                } else {
                    LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return userService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
                            Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
                }
            } catch (JwtTokenEmptyException ex) {
                LOG.error("JWT signature does not match locally computed signature", ex);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return userService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
            } catch (HttpClientErrorException.NotFound userNotUpdated) {
                LOG.error(userNotUpdated + "ErrorCode : USER_400_404");
                WebApplicationErrorHandler.raiseError("USER_400_004");
            } catch (HttpServerErrorException.InternalServerError internalServerError) {
                LOG.error(internalServerError + "ErrorCode : USER_500_001");
                WebApplicationErrorHandler.raiseError(USER_500_001);
            } catch (Exception e) {
                LOG.error("ErrorCode : USER_500_002", e);
                WebApplicationErrorHandler.raiseError(USER_500_002);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
        return respMap;
    }

    /**
     * <pre>
     * This is the method which Delete User by @param userKey.
     * @param userId userId
     * @param request httpServletRequest
     * @param response httpServletResponse
     * @return empty response
     * </pre>
     * <pre>
     * Request Payload :
     * DELETE /scim/v2/Users/{{id}}
     * Host: {{host_name}}:{{port_number}}
     * Authorization: Bearer {{ips_token}}
     * </pre>
     * <pre>
     * Response Payload :
     * HTTP/1.1 204 No Content
     * </pre>
     * <pre>
     * <b>Error Codes: </b>
     * HTTP 400:
     * Error Code: CODE_BAD_REQUEST = 400;
     * Error Description: DESC_USER_BAD_ID_REQUEST = "missing parameter externalId."
     *
     * HTTP 401:
     * Error Code: CODE_UNAUTHORIZED = 401;
     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
     *             + "missing."
     *
     * HTTP 404:
     * Error Code: CODE_RESOURCE_NOT_FOUND = 404;
     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
     *
     * HTTP 500:
     * Error Code: USER_500_001 = Internal server error at the User Service.
     * Error Code: USER_500_002 = User Service error.
     * </pre>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    Object delete(@PathVariable(value = "id") String userId, HttpServletRequest request,
                  HttpServletResponse response) {
        LOG.info("UserController:delete: userId" + userId);
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
                BigInteger tenantId = jwtService.getTenantFromJWT(authorizationHeader);
                LOG.info("getTenantIdFromJWT ::   " + tenantId);
                UserKey userKey = new UserKey(tenantId, userId);
                if (userKey.getUserId() == null || userKey.getUserId().isEmpty()) {
                    LOG.error("externalId is missing... ");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return userService.scimError(ResponseCodeConstants.DESC_USER_BAD_ID_REQUEST,
                            Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
                }
                userService.deleteUser(userKey.getTenantId(), userKey.getUserId());
                LOG.info("User " + userId + " is Deleted ");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                Map<String, Object> returnValue = new HashMap<>();
                return returnValue;
            } else {
                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return userService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
            }
        } catch (JwtTokenEmptyException ex) {
            LOG.error("JWT signature does not match locally computed signature", ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return userService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
        } catch (HttpServerErrorException.InternalServerError internalServerError) {
            LOG.error(internalServerError + "ErrorCode : USER_500_001");
            WebApplicationErrorHandler.raiseError(USER_500_001);
        } catch (Exception e) {
            LOG.error("ErrorCode : USER_500_002", e);
            WebApplicationErrorHandler.raiseError(USER_500_002);
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        Map<String, Object> returnValue = new HashMap<>();
        return returnValue;
    }
}
