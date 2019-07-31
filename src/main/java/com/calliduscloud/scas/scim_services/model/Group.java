package com.calliduscloud.scas.scim_services.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Database schema for {@link Group} object.
 */
@Entity
@Table(name = "SC_GROUP")
public class Group implements Serializable {

    private static final int GROUP_REF_LENGTH = 300;
    private static final int GROUP_NAME_LENGTH = 255;
    private static final String VALUE = "value";
    private static final String REF = "$ref";
    private static final int CREATED_BY_LENGTH = 255;
    private static final Logger LOG = LoggerFactory.getLogger(Group.class);

    @Transient
    private int len = 0;
    @Transient
    ArrayList<String> dis = new ArrayList();
    @Transient
    ArrayList<String> refer = new ArrayList();
    @EmbeddedId
    private GroupKey groupKey;

    @Column(length = GROUP_REF_LENGTH, name = "GROUP_REF")
    private String ref;

    @Column(length = GROUP_NAME_LENGTH, unique = true, name = "GROUP_NAME")
    private String value;

    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;

    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;


    @Column(name = "MODIFY_DATE")
    private Timestamp updatedAt;

    @Column(length = CREATED_BY_LENGTH, name = "MODIFIED_BY")
    private String updatedBy;


    //    @Transient
//    private Set<User> users;
//
//    public Set<User> getUsers() {
//        return users;
//    }
//
//    @ManyToMany(mappedBy = "groups")
//    public void setUsers(Set<User> users) {
//        this.users = users;
//    }

    public Group() {
    }

    public Group(GroupKey groupKey, String ref, String value, Timestamp created,
                 String createdBy, Timestamp updated, String updatedBy) {

        this.groupKey = groupKey;
        this.ref = ref;
        this.value = value;
        this.createdAt = new Timestamp(created.getTime());
        this.createdBy = createdBy;
        this.updatedAt = new Timestamp(updated.getTime());
        this.updatedBy = updatedBy;
    }

    public Group(Map<String, Object> resource) {

        this.update(resource);
    }

    public Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp currentTime = new Timestamp(time);
        return currentTime;
    }

    public Group(GroupKey groupKey, Map<String, Object> params, String createdBy,
                 Timestamp createdAt, String updatedBy, Timestamp updatedAt) {

        this.updateGroup(groupKey, params, createdBy, createdAt, updatedBy, updatedAt);
    }

    void update(Map<String, Object> resource) {

        try {
            this.groupKey = (GroupKey) resource.get("groupKey");
            this.value = resource.get(VALUE).toString();
            this.updatedAt = getTimeStamp();
            this.updatedBy = resource.get(VALUE).toString();

        } catch (Exception e) {
            LOG.error("error", e);
        }
    }


    /**
     * Updates {@link Group} object from JSON {@link Map}.
     *
     * @param params JSON {@link Map} of {@link Group}.
     */
    public Map<String, Object> updateGroup(GroupKey groupKey, Map<String, Object> params, String createdBy,
                                           Timestamp createdAt, String updatedBy, Timestamp updatedAt) {

        ArrayList names = (ArrayList) params.get("members");
        LOG.info("name ***" + names.size());
        len = names.size();

        for (int i = 0; i < names.size(); i++) {
            Map<String, Object> str = (Map<String, Object>) names.get(i);
            for (Map.Entry<String, Object> entry : str.entrySet()) {
                switch (entry.getKey()) {

                    case VALUE:
                        this.value = str.get(entry.getKey()).toString();
                        dis.add(this.value);
                        break;
                    case REF:
                        this.ref = str.get(entry.getKey()).toString();
                        refer.add(this.ref);
                        break;
                    default:
                        break;
                }
            }
        }

//        String[] arr = ref.split("/");
        this.groupKey = groupKey;
        this.createdAt = getTimeStamp();
        this.createdBy = this.value;
        this.updatedBy = this.value;
        this.updatedAt = getTimeStamp();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("createdDate", this.createdAt);
        hashMap.put("createdBy", this.createdBy);
        hashMap.put("updatedBy", this.updatedBy);
        hashMap.put("updatedDate", this.updatedAt);
        return hashMap;
    }



    //Getter And Setter
    public GroupKey getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(GroupKey groupKey) {
        this.groupKey = groupKey;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public ArrayList<String> getDis() {
        return dis;
    }

    public void setDis(ArrayList<String> dis) {
        this.dis = dis;
    }

    public ArrayList<String> getRefer() {
        return refer;
    }

    public void setRefer(ArrayList<String> refer) {
        this.refer = refer;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = getTimeStamp();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        Group group = (Group) o;
        return len == group.len
                && dis.equals(group.dis)
                && refer.equals(group.refer)
                && groupKey.equals(group.groupKey)
                && ref.equals(group.ref)
                && value.equals(group.value)
                && createdAt.equals(group.createdAt)
                && createdBy.equals(group.createdBy)
                && updatedAt.equals(group.updatedAt)
                && updatedBy.equals(group.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(len, dis, refer, groupKey, ref, value, createdAt, createdBy, updatedAt, updatedBy);
    }

    @Override
    public String toString() {
        return "Group{"
                + "len=" + len
                + ", dis=" + dis
                + ", refer=" + refer
                + ", groupKey='" + groupKey + '\''
                + ", ref='" + ref + '\''
                + ", value='" + value + '\''
                + ", createdAt=" + createdAt
                + ", createdBy='" + createdBy + '\''
                + ", updatedAt=" + updatedAt
                + ", updatedBy='" + updatedBy + '\''
                + '}';
    }
}