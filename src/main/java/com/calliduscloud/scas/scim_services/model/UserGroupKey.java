package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserGroupKey implements Serializable {
    private static final int GROUP_ID_LENGTH = 255;

   private UserKey userKey;

   @Column(length = GROUP_ID_LENGTH, name = "GROUP_ID")
   private String groupId;

   public UserGroupKey(UserKey userKey, String groupId) {
        this.userKey = userKey;
        this.groupId = groupId;
   }

    public UserGroupKey() {
    }

    public UserKey getUserKey() {
        return userKey;
    }

    public void setUserKey(UserKey userKey) {
        this.userKey = userKey;
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
        if (!(o instanceof UserGroupKey)) {
            return false;
        }
        UserGroupKey that = (UserGroupKey) o;
        return userKey.equals(that.userKey)
                && groupId.equals(that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userKey, groupId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserGroupKey{");
        sb.append("userKey=").append(userKey);
        sb.append(", groupId='").append(groupId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}