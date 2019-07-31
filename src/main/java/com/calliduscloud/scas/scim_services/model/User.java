package com.calliduscloud.scas.scim_services.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Database Schema for {@link User} object.
 */
@Entity
@Table(name = "SC_USER")
public class User implements Serializable {

    private static final int USER_NAME_LENGTH = 255;
    private static final int CREATED_BY_LENGTH = 255;
    private static final String USER_NAME = "userName";
    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    @EmbeddedId
    private UserKey userKey;

    @Column(unique = true, length = USER_NAME_LENGTH, name = "USER_NAME")
    private String userName;

    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;

    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFY_DATE")
    private Timestamp updatedAt;

    @Column(length = CREATED_BY_LENGTH, name = "MODIFIED_BY")
    private String updatedBy;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "SC_USER_GROUP",
            joinColumns = {@JoinColumn(name = "TENANT_ID"),
                    @JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TENANT_ID", insertable = false, updatable = false),
                    @JoinColumn(name = "GROUP_ID")}
    )

    @Transient
    private Set<Group> groups = new HashSet<>();


    public User() {
    }

    public User(UserKey userKey, String userName, Timestamp createdAt, String createdBy, Timestamp updatedAt,
                String updatedBy, Set<Group> groups) {
        this.userKey = userKey;
        this.userName = userName;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
        this.updatedAt = new Timestamp(updatedAt.getTime());
        this.updatedBy = updatedBy;
        this.groups = groups;
    }

    public User(UserKey userKey, String userName, Timestamp createdAt, String createdBy, Timestamp updatedAt,
                String updatedBy) {
        this.userKey = userKey;
        this.userName = userName;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
        this.updatedAt = new Timestamp(updatedAt.getTime());
        this.updatedBy = updatedBy;
    }

    public UserKey getUserKey() {
        return userKey;
    }

    public void setUserKey(UserKey userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Timestamp getUpdatedAt() {
        return new Timestamp(updatedAt.getTime());
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = new Timestamp(updatedAt.getTime());
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    // For Embbeded keys
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return userKey.equals(user.userKey)
                && userName.equals(user.userName)
                && createdAt.equals(user.createdAt)
                && createdBy.equals(user.createdBy)
                && updatedAt.equals(user.updatedAt)
                && updatedBy.equals(user.updatedBy)
                && groups.equals(user.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userKey, userName, createdAt, createdBy, updatedAt, updatedBy, groups);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("userKey=").append(userKey);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", createdBy='").append(createdBy).append('\'');
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", updatedBy='").append(updatedBy).append('\'');
        sb.append(", groups=").append(groups);
        sb.append('}');
        return sb.toString();
    }
}
