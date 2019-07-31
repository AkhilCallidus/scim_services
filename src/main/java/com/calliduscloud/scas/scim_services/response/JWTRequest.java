package com.calliduscloud.scas.scim_services.response;

public class JWTRequest {
    /*
     * SAC request parameters
     *
     * */
    private String loginUserId;
    private String tenantId;
    private String sacTenantId;
    private String sacTenantUrl;


    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSacTenantId() {
        return sacTenantId;
    }

    public void setSacTenantId(String sacTenantId) {
        this.sacTenantId = sacTenantId;
    }

    public String getSacTenantUrl() {
        return sacTenantUrl;
    }

    public void setSacTenantUrl(String sacTenantUrl) {
        this.sacTenantUrl = sacTenantUrl;
    }
}
