package com.calliduscloud.scas.scim_services.util;

public class ResponseCodeConstants {

    private ResponseCodeConstants() {
// To stop instantiation of this constant class
    }

    public static final String ERROR_RESPONSE_SCHEMA_URI = "urn:ietf:params:scim:api:messages:2.0:Error";
    public static final String USER_LIST_RESPONSE_SCHEMA_URI =
            "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    public static final String USER_RESPONSE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0:User";


    public static final String JWT_CLIENT_ERROR_CODE = "Invalid credentials";
    public static final String JWT_CLIENT_ERROR_DESCRIPTION = "client id and client secret are not valid";
    public static final String JWT_CLIENT_INPUT_ERROR_CODE = "invalid body details";
    public static final String JWT_CLIENT_INPUT_ERROR_DESCRIPTION = "user id is not valid";
    public static final String JWT_CLIENT_IN_ERROR_DESCRIPTION = "environment name is not valid";
    public static final String JWT_INVALID_SIGNATURE_CODE = "Invalid signature";
    public static final String JWT_INVALID_SIGNATURE_DESCRIPTION = "Signature is not valid";
    public static final String JWT_MALFORMED_ERROR_CODE = "Invalid JWT token";
    public static final String JWT_MALFORMED_ERROR_DESCRIPTION = "JWT was not properly constructed";
    public static final String JWT_EXPIRED_JWT_CODE = "Expired JWT token";
    public static final String JWT_EXPIRED_JWT_DESCRIPTION = "JWT Token is Expired";
    public static final String JWT_UNSUPPORTED_JWT_CODE = "Unsupported JWT token";
    public static final String JWT_UNSUPPORTED_JWT_DESCRIPTION = "You are parsing an unsigned plaintext "
            + "JWT when the application requires a cryptographically signed Claims JWS instead.";
    public static final String JWT_ILLEGAL_TOKEN_CODE = "Invalid JWT token";
    public static final String JWT_ILLEGAL_TOKEN_DESCRIPTION = "Illegal or inappropriate argument";
    public static final String JWT_INVALID_KEY = "Invalid key found";
    public static final String JWT_INVALID_KEY_DESCRIPTION = "Invalid key specifications";
    public static final String JWT_EMPTY_TOKEN_CODE = "Invalid JWT token";
    public static final String JWT_EMPTY_TOKEN_DESCRIPTION = "JWT is not found in header as a part of Bearer";
    public static final String JWT_SIGNATURE_TOKEN_DESCRIPTION = "JWT signature does not match locally computed"
            + " signature. JWT validity cannot be asserted and should not be trusted";
    public static final String USER_ENVIRONMENT_CODE = "Environment is not Found";
    public static final String USER_ENVIRONMENT_DESCRIPTION = "Error while fetching environment for tenant id : ";

    public static final String SYSTEM = "SYSTEM";


    public static final int CODE_BAD_REQUEST = 400;
    public static final String DESC_USER_BAD_ID_REQUEST = "Missing Parameter ExternalId.";
    public static final String DESC_GROUP_BAD_ID_REQUEST = "Missing Parameter ExternalId.";
    public static final String DESC_USER_BAD_NAME_REQUEST = "Missing Parameter UserName.";
    public static final String DESC_INVALID_USER_ID_REQUEST = "Invalid ExternalId Found.";
    public static final String DESC_INVALID_USER_NAME_REQUEST = "Invalid UserName Found.";
    public static final String DESC_INVALID_GROUP_ID_REQUEST = "Invalid ExternalId Found.";
    public static final String DESC_GROUP_BAD_NAME_REQUEST = "Missing Parameter DisplayName.";
    public static final String DESC_GROUP_INVALID_NAME_REQUEST = "Invalid DisplayName Found.";
    public static final String DESC_GROUP_NAME_REQUEST = "You can't use this DisplayName it's already Assigned"
            + " to different GroupId";

    public static final String DESC_GROUP_BAD_REQUEST = "Missing Parameter ExternalId.";
// DESC Description
    public static final int CODE_UNAUTHORIZED = 401;
    public static final String DESC_UNAUTHORIZED = "Authorization failure. The authorization header is invalid or "
            + "missing.";

    public static final int CODE_NO_CONTENT = 204;
    public static final String DESC_NO_CONTENT = "Transferred a partial file";

    public static final int CODE_CONFLICT = 409;
    public static final String DESC_CONFLICT = "ExternalId Already existed in the system.";
    public static final String DESC_USER_NAME_CONFLICT = "You can't use this UserName it's already Assigned"
            + " to different UserId";
    public static final String DESC_JWT_SIGNATURE = "JWT signature does not match locally computed signature.";
    public static final String DESC_GROUP_CONFLICT = "ExternalId Already existed in the system.";

    public static final int CODE_RESOURCE_NOT_FOUND = 404;
    public static final String DESC_RESOURCE_NOT_FOUND = "members are not present";

    public static final int CODE_INTERNAL_ERROR = 500;
    public static final String DESC_INTERNAL_ERROR = "An internal error.";

}
