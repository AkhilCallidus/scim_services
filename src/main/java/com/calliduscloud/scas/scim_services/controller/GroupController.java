//package com.calliduscloud.scas.scim_services.controller;
//
//import com.calliduscloud.scas.scim_services.exception.GroupAlreadyExistException;
//import com.calliduscloud.scas.scim_services.exception.GroupNameAlreadyExistException;
//import com.calliduscloud.scas.scim_services.exception.InvalidGroupIdException;
//import com.calliduscloud.scas.scim_services.exception.InvalidGroupNameException;
//import com.calliduscloud.scas.scim_services.exception.ItemNotFoundException;
//import com.calliduscloud.scas.scim_services.exception.JwtTokenEmptyException;
//import com.calliduscloud.scas.scim_services.exception.UserNotFoundException;
//import com.calliduscloud.scas.scim_services.extrautils.WebApplicationErrorHandler;
//import com.calliduscloud.scas.scim_services.model.GroupKey;
//import com.calliduscloud.scas.scim_services.services.GroupService;
////import com.calliduscloud.scas.scim_services.services.JWTService;
//import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
//import com.calliduscloud.scas.scim_services.util.SCIMResponseUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.math.BigInteger;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * URL route http://{{host_name}}:{{port_number}}/scim/v2/Groups.
// * <h2>
// * GroupService will handle all Group related operations
// * such as createGroup, updateGroup and deleteGroup and find Group.
// * </h2>
// */
//@Controller
//@RequestMapping("/scim/v2/Groups")
//public class GroupController {
//    private static final Logger LOG = LoggerFactory.getLogger(GroupController.class);
//    private static final String APPLICATION_SCIM_JSON = "application/scim+json";
//    private static final String AUTHORIZATION = "Authorization";
//    private GroupService groupService;
//    private JWTService jwtService;
//    private SCIMResponseUtil scimResponseUtil;
//
//    @Autowired
//    private GroupController(GroupService groupService, JWTService jwtService, SCIMResponseUtil scimResponseUtil) {
//        this.groupService = groupService;
//        this.jwtService = jwtService;
//        this.scimResponseUtil = scimResponseUtil;
//    }
//
//
//    /**
//     * This is the method which Retrieve a Single Group by @param groupId.
//     * @param groupId groupId
//     * @param request request
//     * @param response response
//     * @return a Single Group by @param groupId
//     * <pre>
//     * Request Payload :
//     * GET /scim/v2/Groups/5c74904708ba3425d48ea784
//     * Host: {{host_name}}:{{port_number}}
//     * Content-Type: application/scim+json
//     * </pre>
//     * <pre>
//     * <b>Response Payload :</b>
//     * HTTP/1.1 200 OK
//     * Content-Type: application/scim+json
//     * Location: http://{{host_name}}:{{port_number}}/scim/v2/Groups/5c74904708ba3425d48ea784
//     * {
//     *     "displayName": "ADMINISTRATOR_COMM-SCAI",
//     *     "meta": {
//     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c74904708ba3425d4a784",
//     *         "lastModified": "2019-07-12T02:14:54.034+0000",
//     *         "resourceType": "Group"
//     *     },
//     *     "schemas": [
//     *         "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *     ],
//     *     "members": [
//     *         {
//     *             "display": "april.zhu",
//     *             "type": "User",
//     *             "value": "P000095",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095"
//     *         },
//     *         {
//     *             "display": "vmantirr",
//     *             "type": "User",
//     *             "value": "P000011",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000011"
//     *         }
//     *     ],
//     *     "externalId": "5c74904708ba3425d48ea784",
//     *     "id": "5c74904708ba3425d48ea784"
//     * }
//     * </pre>
//     * <pre>
//     * <b>Error Response :</b>
//     * HTTP/1.1 404 Not Found
//     * Content-Type: application/scim+json
//     * {
//     *     "schemas": [
//     *         "urn:ietf:params:scim:api:messages:2.0:Error"
//     *     ],
//     *     "message": "Resource 5c748fe0ac44762660A0000082222weq not found.",
//     *     "status": 404
//     * }
//     * </pre>
//     * <pre>
//     * <b>Error Codes :</b>
//     * HTTP 400:
//     * Error Code: CODE_BAD_REQUEST = 400;
//     * Error Code: GROUP_400_001= Bad data received. Mandatory Group ID is missing.
//     * Error Description: DESC_USER_BAD_ID_REQUEST = "missing parameter externalId."
//     *
//     * HTTP 401:
//     * Error Code: CODE_UNAUTHORIZED = 401;
//     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
//     *             + "missing."
//     * HTTP 404:
//     * Error Code: GROUP_404_001= Group Data Not Found.
//     * Error Code: CODE_RESOURCE_NOT_FOUND = 404
//     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
//     *
//     * HTTP 500:
//     * Error Code: GROUP_500_001= Internal server error.
//     * Error Code: GROUP_500_002=Group Service error.
//     * </pre>
//     */
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_SCIM_JSON)
//    public @ResponseBody
//    Map getGroup(@PathVariable(value = "id") String groupId, HttpServletRequest request,
//                 HttpServletResponse response) {
//        Map<String, Object> groupMap = new HashMap<>();
//        BigInteger tenantId;
//        try {
//            String authorizationHeader = request.getHeader(AUTHORIZATION);
//            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
//                tenantId = jwtService.getTenantFromJWT(authorizationHeader);
//                GroupKey groupKey = new GroupKey(tenantId, groupId);
//                if (groupKey.getGroupId() == null || groupKey.getGroupId().isEmpty()) {
//                    LOG.info("externalId is missing..");
//                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                    return groupService.scimError(ResponseCodeConstants.DESC_USER_BAD_ID_REQUEST,
//                            Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//                }
//                groupMap = groupService.getGroup(tenantId, groupId);
//                LOG.info("GroupMap : " + groupMap);
//            } else {
//                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return groupService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
//                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
//            }
//        } catch (JwtTokenEmptyException ex) {
//            LOG.error("JWT signature does not match locally computed signature", ex);
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        } catch (HttpClientErrorException.NotFound groupNotFound) {
//            LOG.error(String.valueOf(groupNotFound) + "ErrorCode : GROUP_404_001");
//            WebApplicationErrorHandler.raiseError("GROUP_404_001");
//        } catch (ItemNotFoundException ex) {
//            LOG.error("groupId " + groupId + " Not Found", ex);
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return groupService.scimError("Resource " + groupId + " not found.",
//                    Optional.of(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND));
//        } catch (HttpServerErrorException.InternalServerError internalServerError) {
//            LOG.error(String.valueOf(internalServerError) + "ErrorCode : GROUP_500_001");
//            WebApplicationErrorHandler.raiseError("GROUP_500_001");
//        } catch (Exception e) {
//            LOG.error("ErrorCode : GROUP_500_002", e);
//            WebApplicationErrorHandler.raiseError("GROUP_500_002");
//        }
//
//        return scimResponseUtil.toScimGroupResource(groupMap);
//    }
//
//    /**
//     * <pre>
//     * This is the method which Retrieve all the Groups.
//     * @param params params
//     * @param request request
//     * @param response response
//     * @return all the Groups.
//     * </pre>
//     * <pre>
//     * Request Payload :
//     * GET /scim/v2/Groups
//     * Host: {{host_name}}:{{port_number}}
//     * Content-Type: application/scim+json
//     * </pre>
//     * <pre>
//     * Response Payload :
//     * HTTP/1.1 200 OK
//     * Content-Type: application/scim+json
//     * Location: https://{{host_name}}:{{port_number}}/scim/v2/Groups
//     * {
//     *     "totalResults": 42,
//     *     "startIndex": 1,
//     *     "itemsPerPage": 10,
//     *     "schemas": [
//     *         "urn:ietf:params:scim:api:messages:2.0:ListResponse"
//     *     ],
//     *     "Resources": [
//     *         {
//     *             "displayName": "ADMINISTRATOR_COMM-SCAI",
//     *             "meta": {
//     *                 "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c74904708ba2",
//     *                 "lastModified": "2019-07-12T02:14:54.034+0000",
//     *                 "resourceType": "Group"
//     *             },
//     *             "schemas": [
//     *                 "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *             ],
//     *             "members": [
//     *                 {
//     *                     "display": "april.zhu",
//     *                     "type": "User",
//     *                     "value": "P000095",
//     *                     "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095"
//     *                 },
//     *                 {
//     *                     "display": "vmantirr",
//     *                     "type": "User"
//     *
//     *            ....................................
//     *            ....................................
//     * </pre>
//     * <pre>
//     * <b>Error Codes :</b>
//     * HTTP 401:
//     * Error Code: CODE_UNAUTHORIZED = 401;
//     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
//     *             + "missing."
//     * HTTP 404:
//     * Error Code: GROUP_404_001= Group Data Not Found at the Group Service.
//     * HTTP 500:
//     * Error Code: GROUP_500_001= Internal server error at the Group Service.
//     * Error Code: GROUP_500_002=Group Service error.
//     * </pre>
//     */
//    @RequestMapping(method = RequestMethod.GET, produces = APPLICATION_SCIM_JSON)
//    public @ResponseBody
//    Map getAllGroups(@RequestParam Map<String, String> params, HttpServletRequest request,
//                     HttpServletResponse response) {
//        Map<String, Object> respMap = new HashMap<>();
//        BigInteger tenantId;
//        try {
//            String authorizationHeader = request.getHeader(AUTHORIZATION);
//            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
//                tenantId = jwtService.getTenantFromJWT(authorizationHeader);
//                respMap = groupService.getAllGroups(params, tenantId);
//            } else {
//                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return groupService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
//                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
//            }
//        } catch (JwtTokenEmptyException ex) {
//            LOG.error("JWT signature does not match locally computed signature", ex);
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        } catch (HttpClientErrorException.NotFound groupNotFound) {
//            LOG.error(String.valueOf(groupNotFound) + "ErrorCode : GROUP_404_001");
//            WebApplicationErrorHandler.raiseError("GROUP_404_001");
//        } catch (HttpServerErrorException.InternalServerError internalServerError) {
//            LOG.error(String.valueOf(internalServerError) + "ErrorCode : GROUP_500_001");
//            WebApplicationErrorHandler.raiseError("GROUP_500_001");
//        } catch (Exception e) {
//            LOG.error("ErrorCode : GROUP_500_002", e);
//            WebApplicationErrorHandler.raiseError("GROUP_500_002");
//        }
//        return scimResponseUtil.toScimGroupsResources(respMap);
//    }
//
//    /**
//     * <pre>
//     * This is the method which Create Group by @param groupId.
//     * @param params params
//     * @param request request
//     * @param response response
//     * @return groupMap It gives json SCIMResponse.
//     * </pre>
//     * <pre>
//     * Request Payload :
//     * POST /scim/v2/Groups
//     * Host: {{host_name}}:{{port_number}}
//     * Content-Type: application/scim+json
//     * {
//     * "externalId": "5c748fe0ac44222weq",
//     *   "displayName": "A0000018weq_COMM-SCAN",
//     *   "schemas": [
//     *     "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *   ],
//     *   "members": [
//     *     {
//     *       "value": "P000095"
//     *     },
//     *     {
//     *       "value": "P000011"
//     *     }
//     *   ]
//     * }
//     * </pre>
//     * <pre>
//     * Response Payload :
//     * HTTP/1.1 201 Created
//     * Content-Type: application/scim+json
//     * Location: https://{{host_name}}:{{port_number}}/scim/v2/Groups/5bbd23a427e5b72659baaeb3
//     * {
//     *     "displayName": "A0000018weq_COMM-SCAN",
//     *     "meta": {
//     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c748fe0ac44222weq",
//     *         "lastModified": "2019-07-18T19:20:58.575+0000",
//     *         "resourceType": "Group"
//     *     },
//     *     "schemas": [
//     *         "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *     ],
//     *     "members": [
//     *         {
//     *             "display": "april.zhu",
//     *             "type": "User",
//     *             "value": "P000095",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095"
//     *         },
//     *         {
//     *             "display": "vmantirr",
//     *             "type": "User",
//     *             "value": "P000011",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000011"
//     *         }
//     *     ],
//     *     "externalId": "5c748fe0ac44222weq",
//     *     "id": "5c748fe0ac44222weq"
//     * }
//     * </pre>
//     * <pre>
//     * <b>Error Codes :</b>
//     * HTTP 400:
//     * Error Code: CODE_BAD_REQUEST = 400;
//     * Error Description: DESC_GROUP_BAD_ID_REQUEST = "missing parameter externalId."
//     * Error Description: DESC_GROUP_BAD_NAME_REQUEST = "missing parameter displayName."
//     * Error Description: DESC_GROUP_INVALID_NAME_REQUEST = "Invalid displayName Found.";
//     * USER_400_002 = Bad data received. Invalid Group Name Found.
//     * GROUP_400_003 = Bad data received. Group Already exist.
//     *
//     * HTTP 401:
//     * Error Code: CODE_UNAUTHORIZED = 401;
//     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
//     *             + "missing."
//     *
//     * HTTP 409:
//     * Error Code: CODE_CONFLICT = 409;
//     * Error Code: GROUP_409_002= Group Conflict at the Group Service.
//     * Error Description: USER_409_001 = User Conflict at the User Service.
//     * Error Description: USER_409_002 = User Already exists.
//     * Error Description: DESC_GROUP_CONFLICT = "Group Already exists in the system."
//     *
//     * HTTP 500:
//     * Error Code: USER_500_001 = Internal server error at the User Service.
//     * Error Code: USER_500_002 = User Service error.
//     * </pre>
//     */
//    @RequestMapping(method = RequestMethod.POST, produces = APPLICATION_SCIM_JSON)
//    public @ResponseBody
//    Object create(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
//        LOG.info("GroupController:create : Entry" + params);
//
//        Map<String, Object> groupMap = new HashMap<>();
//        BigInteger tenantId;
//        if ((null == params.get("externalId") || ((String) params.get("externalId")).isEmpty())) {
//            LOG.error("create :: id is missing...");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_GROUP_BAD_ID_REQUEST,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        }
//        if ((null == params.get("displayName") || ((String) params.get("displayName")).isEmpty())) {
//            LOG.error("Group :: create :: missing displayName..");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_GROUP_BAD_NAME_REQUEST,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        } else {
//            try {
//                String authorizationHeader = request.getHeader(AUTHORIZATION);
//                if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
//                    tenantId = jwtService.getTenantFromJWT(authorizationHeader);
//                    LOG.info("getTenantIdFromJWT ::   " + tenantId);
//                    groupMap = groupService.createGroup(params, tenantId);
//                    LOG.info("Group " + params.get("displayName") + " is Created.");
//                } else {
//                    LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    return groupService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
//                            Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
//                }
//            } catch (HttpClientErrorException.NotFound ex) {
//                LOG.error("ErrorCode : GROUP_400_004", ex);
//                WebApplicationErrorHandler.raiseError("GROUP_400_004");
//            } catch (JwtTokenEmptyException ex) {
//                LOG.error("JWT signature does not match locally computed signature", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (GroupAlreadyExistException ex) {
//                LOG.error("group already exists " + "ErrorCode : GROUP_409_003", ex);
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                return groupService.scimError(ResponseCodeConstants.DESC_GROUP_CONFLICT,
//                        Optional.of(ResponseCodeConstants.CODE_CONFLICT));
//            } catch (InvalidGroupIdException ex) {
//                LOG.error("Invalid groupId found " + "ErrorCode : GROUP_400_003", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_INVALID_GROUP_ID_REQUEST,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (InvalidGroupNameException ex) {
//                LOG.error("invalid group_name found " + "ErrorCode : GROUP_400_002", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_GROUP_INVALID_NAME_REQUEST,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (GroupNameAlreadyExistException ex) {
//                LOG.error("group_name already exists " + "ErrorCode : GROUP_409_002", ex);
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                return groupService.scimError(ResponseCodeConstants.DESC_GROUP_NAME_REQUEST,
//                        Optional.of(ResponseCodeConstants.CODE_CONFLICT));
//            } catch (HttpServerErrorException.InternalServerError internalServerError) {
//                LOG.error(String.valueOf(internalServerError) + "ErrorCode : GROUP_500_001");
//                WebApplicationErrorHandler.raiseError("GROUP_500_001");
//            } catch (Exception e) {
//                LOG.error("ErrorCode : GROUP_500_002", e);
//                WebApplicationErrorHandler.raiseError("GROUP_500_002");
//            }
//            response.setStatus(HttpServletResponse.SC_CREATED);
//        }
//        return scimResponseUtil.toScimGroupResource(groupMap);
//    }
//
//    /**
//     * <pre>
//     * This is the method which Update Group by @param groupId.
//     *
//     * @param groupId groupId
//     * @param params params
//     * @param request request
//     * @param response response
//     * @return groupMap It updates the groups.
//     * </pre>
//     * <pre>
//     * Request Payload :
//     * PUT /scim/v2/Groups/5c748fe0ac44222weq
//     * Host: {{host_name}}:{{port_number}}
//     * Content-Type: application/scim+json
//     *{
//     * "externalId": "5c748fe0ac44222weq",
//     *   "displayName": "A00000_COMM-SCAN",
//     *   "schemas": [
//     *     "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *   ],
//     *   "members": [
//     *     {
//     *       "value": "P000095"
//     *     },
//     *     {
//     *       "value": "P000011"
//     *     }
//     *   ]
//     * }
//     * </pre>
//     * <pre>
//     * Response Payload :
//     * HTTP/1.1 200 OK
//     * Content-Type: application/scim+json
//     * Location: https://{{host_name}}:{{port_number}}/scim/v2/Groups/5c748fe0ac44222weq
//     * {
//     *     "displayName": "A00000_COMM-SCAN",
//     *     "meta": {
//     *         "location": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Groups/5c748fe0ac44222weq",
//     *         "lastModified": "2019-07-18T19:41:02.078+0000",
//     *         "resourceType": "Group"
//     *     },
//     *     "schemas": [
//     *         "urn:ietf:params:scim:schemas:core:2.0:Group"
//     *     ],
//     *     "members": [
//     *         {
//     *             "display": "april.zhu",
//     *             "type": "User",
//     *             "value": "P000095",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000095"
//     *         },
//     *         {
//     *             "display": "vmantirr",
//     *             "type": "User",
//     *             "value": "P000011",
//     *             "$ref": "https://sacservices.calliduscloud.com/scim_services/scim/v2/Users/P000011"
//     *         }
//     *     ],
//     *     "externalId": "5c748fe0ac44222weq",
//     *     "id": "5c748fe0ac44222weq"
//     * }
//     * </pre>
//     * <pre>
//     * <b>Error Codes :</b>
//     * HTTP 400:
//     * Error Code: CODE_BAD_REQUEST = 400;
//     * Error Description: DESC_GROUP_BAD_ID_REQUEST = "missing parameter externalId."
//     * Error Description: DESC_GROUP_BAD_NAME_REQUEST = "missing parameter displayName."
//     * Error Description: DESC_GROUP_INVALID_NAME_REQUEST = "Invalid displayName Found.";
//     * USER_400_002 = Bad data received. Invalid Group Name Found.
//     * GROUP_400_003 = Bad data received. Group Already exist.
//     *
//     * HTTP 401:
//     * Error Code: CODE_UNAUTHORIZED = 401;
//     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
//     *             + "missing."
//     *
//     * HTTP 404:
//     * Error Code: CODE_RESOURCE_NOT_FOUND = 404;
//     *
//     * HTTP 500:
//     * Error Code: GROUP_500_001= Internal server error at the Group Service.
//     * Error Code: GROUP_500_002=Group Service error.
//     * </pre>
//     */
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = APPLICATION_SCIM_JSON)
//    public @ResponseBody
//    Map update(@PathVariable(value = "id") String groupId,
//               @RequestBody Map<String, Object> params, HttpServletRequest request,
//               HttpServletResponse response) {
//        LOG.info("params : " + params);
//        Map<String, Object> groupMap = new HashMap<>();
//        GroupKey groupKey;
//        if ((null == params.get("displayName") || ((String) params.get("displayName")).isEmpty())) {
//            LOG.error("Group :: create :: missing displayName..");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_GROUP_BAD_NAME_REQUEST,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        } else {
//            try {
//                String authorizationHeader = request.getHeader(AUTHORIZATION);
//                if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
//                    BigInteger tenantId = jwtService.getTenantFromJWT(authorizationHeader);
//                    LOG.info("getTenantIdFromJWT ::   " + tenantId);
//                    groupKey = new GroupKey(tenantId, groupId);
//                    if (groupKey.getGroupId() == null || groupKey.getGroupId().isEmpty()) {
//                        LOG.error("externalId is missing... ");
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        return groupService.scimError(ResponseCodeConstants.DESC_GROUP_BAD_REQUEST,
//                                Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//                    }
//                    groupMap = groupService.updateGroup(groupKey, params);
//                    LOG.info("Group " + params.get("displayName") + " is Updated.");
//                } else {
//                    LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    return groupService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
//                            Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
//                }
//            } catch (JwtTokenEmptyException ex) {
//                LOG.error("JWT signature does not match locally computed signature", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (ItemNotFoundException ex) {
//                LOG.error("groupId " + groupId + " Not Found", ex);
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                return groupService.scimError("Resource " + groupId + " not found.",
//                        Optional.of(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND));
//            } catch (GroupNameAlreadyExistException ex) {
//                LOG.error("group_name already exists " + "ErrorCode : GROUP_409_002", ex);
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                return groupService.scimError(ResponseCodeConstants.DESC_GROUP_NAME_REQUEST,
//                        Optional.of(ResponseCodeConstants.CODE_CONFLICT));
//            } catch (UserNotFoundException ex) {
//                LOG.error("members not found " + "ErrorCode : GROUP_400_003", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_RESOURCE_NOT_FOUND,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (HttpClientErrorException.NotFound groupNotUpdated) {
//                LOG.error(String.valueOf(groupNotUpdated) + "ErrorCode : GROUP_400_005");
//                WebApplicationErrorHandler.raiseError("GROUP_400_005");
//            } catch (InvalidGroupNameException ex) {
//                LOG.error("invalid group_name found " + "ErrorCode : USER_400_002", ex);
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return groupService.scimError(ResponseCodeConstants.DESC_GROUP_INVALID_NAME_REQUEST,
//                        Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//            } catch (HttpServerErrorException.InternalServerError internalServerError) {
//                LOG.error(String.valueOf(internalServerError) + "ErrorCode : GROUP_500_001");
//                WebApplicationErrorHandler.raiseError("GROUP_500_001");
//            } catch (Exception e) {
//                LOG.error("ErrorCode : GROUP_500_002", e);
//                WebApplicationErrorHandler.raiseError("GROUP_500_002");
//            }
//            response.setStatus(HttpServletResponse.SC_OK);
//        }
//        return scimResponseUtil.toScimGroupResource(groupMap);
//    }
//
//    /**
//     * <pre>
//     * This is the method which Delete Group by @param groupId.
//     * @param groupId groupId
//     * @param request request
//     * @param response response
//     * @return delete response
//     * </pre>
//     * <pre>
//     * Request Payload :
//     * DELETE /scim/v2/Groups/5bbd23a427e5b72659baaeb3
//     * Host: {{host_name}}:{{port_number}}
//     * </pre>
//     * <pre>
//     * Response Payload :
//     * HTTP/1.1 204 No Content
//     * </pre>
//     * <pre>
//     * Error Response :
//     * {
//     *     "schemas": [
//     *         "urn:ietf:params:scim:api:messages:2.0:Error"
//     *     ],
//     *     "message": "Resource 5c748fe0ac4400082222weq not found.",
//     *     "status": 404
//     * }
//     *
//     * </pre>
//     * <pre>
//     * <b>Error Codes :</b>
//     * HTTP 400:
//     * Error Code: CODE_BAD_REQUEST = 400;
//     * Error Description: DESC_GROUP_BAD_REQUEST = "missing parameter externalId."
//     *
//     * HTTP 401:
//     * Error Code: CODE_UNAUTHORIZED = 401;
//     * Error Description: DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
//     *             + "missing."
//     *
//     * HTTP 404:
//     * Error Code: CODE_RESOURCE_NOT_FOUND = 404;
//     * Error Code: JWT_TOKEN_404_001 = Jwt Token is Empty.
//     *
//     * HTTP 500:
//     * Error Code: GROUP_500_001= Internal server error at the Group Service.
//     * Error Code: GROUP_500_002=Group Service error.
//     * </pre>
//     */
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    public @ResponseBody
//    Object delete(@PathVariable(value = "id") String groupId, HttpServletRequest request,
//                  HttpServletResponse response) {
//        LOG.info("GroupController:delete: groupId " + groupId);
//        try {
//            String authorizationHeader = request.getHeader(AUTHORIZATION);
//            if (null != authorizationHeader && !authorizationHeader.isEmpty()) {
//                BigInteger tenantId = jwtService.getTenantFromJWT(authorizationHeader);
//                GroupKey groupKey = new GroupKey(tenantId, groupId);
//                LOG.info("getTenantIdFromJWT ::  " + groupKey.tenantId);
//                if (groupKey.getGroupId() == null || groupKey.getGroupId().isEmpty()) {
//                    LOG.error("externalId is missing... ");
//                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                    return groupService.scimError(ResponseCodeConstants.DESC_GROUP_BAD_REQUEST,
//                            Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//                }
//                groupService.deleteGroup(tenantId, groupId);
//                LOG.info("Group " + groupId + " is Deleted ");
//            } else {
//                LOG.error(ResponseCodeConstants.DESC_UNAUTHORIZED);
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return groupService.scimError(ResponseCodeConstants.DESC_UNAUTHORIZED,
//                        Optional.of(ResponseCodeConstants.CODE_UNAUTHORIZED));
//            }
//        } catch (ItemNotFoundException ex) {
//            LOG.error("groupId " + groupId + " Not Found", ex);
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return groupService.scimError("Resource " + groupId + " not found.",
//                    Optional.of(ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND));
//        } catch (JwtTokenEmptyException ex) {
//            LOG.error("JWT signature does not match locally computed signature", ex);
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return groupService.scimError(ResponseCodeConstants.DESC_JWT_SIGNATURE,
//                    Optional.of(ResponseCodeConstants.CODE_BAD_REQUEST));
//        } catch (HttpServerErrorException.InternalServerError internalServerError) {
//            LOG.error(internalServerError + "ErrorCode : USER_500_001");
//            WebApplicationErrorHandler.raiseError("USER_500_001");
//        } catch (Exception e) {
//            LOG.error("ErrorCode : USER_500_002", e);
//            WebApplicationErrorHandler.raiseError("USER_500_002");
//        }
//        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//        return "";
//    }
//}