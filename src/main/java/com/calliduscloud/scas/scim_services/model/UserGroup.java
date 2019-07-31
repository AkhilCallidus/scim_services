package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "SC_USER_GROUP")
public class UserGroup implements Serializable {
    private static final int CREATED_BY_LENGTH = 255;

    @EmbeddedId
    private UserGroupKey userGroupKey;

    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;

    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;

    public UserGroup(UserGroupKey userGroupKey, Timestamp createdAt, String createdBy) {
        this.userGroupKey = userGroupKey;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
    }

    public UserGroup() {

    }

    public UserGroupKey getUserGroupKey() {
        return userGroupKey;
    }

    public void setUserGroupKey(UserGroupKey userGroupKey) {
        this.userGroupKey = userGroupKey;
    }

    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Timestamp(createdAt.getTime());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGroup)) {
            return false;
        }
        UserGroup userGroup = (UserGroup) o;
        return userGroupKey.equals(userGroup.userGroupKey)
                && createdAt.equals(userGroup.createdAt)
                && createdBy.equals(userGroup.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userGroupKey, createdAt, createdBy);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserGroup{");
        sb.append("userGroupKey=").append(userGroupKey);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", createdBy='").append(createdBy).append('\'');
        sb.append('}');
        return sb.toString();
    }
}