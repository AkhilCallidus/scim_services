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
public class GroupKey implements Serializable {
    private static final int GROUP_ID_LENGTH = 255;

    @Column(name = "TENANT_ID")
    public BigInteger tenantId;

    @Column(length = GROUP_ID_LENGTH, name = "GROUP_ID")
    public String groupId;

    public GroupKey(BigInteger tenantId, String groupId) {
        this.tenantId = tenantId;
        this.groupId = groupId;
    }

    public GroupKey() {
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupKey)) {
            return false;
        }
        GroupKey groupKey = (GroupKey) o;
        return tenantId == groupKey.tenantId
                && groupId.equals(groupKey.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, groupId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GroupKey{");
        sb.append("tenantId=").append(tenantId);
        sb.append(", groupId=\'").append(groupId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}