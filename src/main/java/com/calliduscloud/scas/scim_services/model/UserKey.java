package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Composite primary key for {@link User} object.
 */
@Embeddable
public class UserKey implements Serializable {
    private static final int USER_ID_LENGTH = 255;

    @Column(name = "TENANT_ID")
    public BigInteger tenantId;

    @Column(length = USER_ID_LENGTH, name = "USER_ID")
    public String userId;

    public UserKey(BigInteger tenantId, String userId) {
        this.tenantId = tenantId;
        this.userId = userId;
    }

    public UserKey() {
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserKey)) {
            return false;
        }
        UserKey userKey = (UserKey) o;
        return tenantId == userKey.tenantId
                && userId.equals(userKey.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, userId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserKey{");
        sb.append("tenantId=").append(tenantId);
        sb.append(", userId=\'").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}