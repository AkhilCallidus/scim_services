package com.calliduscloud.scas.scim_services.util;

import com.calliduscloud.scas.scim_services.model.Group;
import com.calliduscloud.scas.scim_services.model.User;
import com.calliduscloud.scas.scim_services.response.GroupDTO;
import com.calliduscloud.scas.scim_services.response.GroupListResponse;
import com.calliduscloud.scas.scim_services.response.UserListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SCIMResponseUtil {

    private static final String STRING = "://";
    private static final String STRING1 = ":";
    @Value("${issuer.name}")
    private String issuerName;
    @Value("${issuer.protocol}")
    private String issuerProtocol;
    @Value("${issuer.port}")
    private String issuerPort;
    @Value("${issuer.context.path}")
    private String issuerContextPath;

    private static final Logger LOG = LoggerFactory.getLogger(SCIMResponseUtil.class);

    private List<Map> list;
    private int count;
    private int totalResults;
    private int startIndex;

    public Map toSCIMResource(Map<String, Object> userMap) {
        Map<String, Object> returnValue = new HashMap<>();
        User user = (User) userMap.get("user");
        List<String> schemas = new ArrayList<>();
        schemas.add(ResponseCodeConstants.USER_RESPONSE_SCHEMA_URI);
        returnValue.put("schemas", schemas);
        returnValue.put("id", user.getUserKey().getUserId());
        returnValue.put("externalId", user.getUserKey().getUserId());
        returnValue.put("userName", user.getUserName());

        List<GroupDTO> groups = (ArrayList<GroupDTO>) userMap.get("groups");
        List<Map<String, Object>> groupList = new ArrayList<>();
        for (GroupDTO g : groups) {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("$ref", g.getref());
            groupMap.put("value", g.getValue());
            groupMap.put("display", g.getDisplay());
            groupList.add(groupMap);
        }

        returnValue.put("groups", groupList);

        // Meta information
        Map<String, Object> meta = new HashMap<>();
        meta.put("resourceType", "User");
        meta.put("location", (issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                + "/scim/v2/Users/" + user.getUserKey().getUserId()));
        meta.put("lastModified", user.getUpdatedAt());
        returnValue.put("meta", meta);

        return returnValue;
    }


    /**
     * Formats JSON {@link Map} response with {@link User} attributes.
     *
     * @return JSON {@link Map} of {@link User}
     */
//    public Map toScimResource(Map userMap) {
//        Map value = commonMethodForToScimResource(userMap);
//        User user = (User) userMap.get("user");
//        // Meta information
//        Map<String, Object> meta = new HashMap<>();
//        meta.put("resourceType", "User");
//        meta.put("location", ("https://" + issuer + "/scim_services/scim/v2/Users/"
//                + user.getUserKey().getUserId()));
//        meta.put("lastModified", user.getUpdatedAt());
//        value.put("meta", meta);
//
//        return value;
//    }
//
//    private Map commonMethodForToScimResource(Map userMap) {
//        Map<String, Object> returnValue = new HashMap<>();
//        User user = (User) userMap.get("user");
//        // Group Information
//        List groups = (ArrayList) userMap.get("groups");
//        List<String> schemas = new ArrayList<>();
//        schemas.add(ResponseCodeConstants.USER_RESPONSE_SCHEMA_URI);
//        returnValue.put("schemas", schemas);
//        returnValue.put("groups", groups);
//        returnValue.put("id", user.getUserKey().getUserId());
//        returnValue.put("externalId", user.getUserKey().getUserId());
//        returnValue.put("userName", user.getUserName());
//       // returnValue.put("groups", getGroupList(user.getGroups()));
//      //  returnValue.put("groups", );
//
//        return returnValue;
//    }
//
//    private List<GroupDTO> getGroupList(Set<Group> groups) {
//        List<GroupDTO> groupList = new ArrayList<>();
//        if (null != groups && !groups.isEmpty()) {
//            Iterator<Group> itr = groups.iterator();
//            while (itr.hasNext()) {
//                Group group = itr.next();
//             String ref = "https://" + issuer + "/scim_services/scim/v2/Groups/" + group.getGroupKey().getGroupId();
//                groupList.add(new GroupDTO(ref, group.getGroupKey().getGroupId(), group.getValue()));
//            }
//        }
//        return groupList;
//    }


    /**
     * Formats JSON {@link Map} response with {@link Group} attributes.
     *
     * @return JSON {@link Map} of {@link Group}
     */
    public Map toScimGroupResource(Map<String, Object> groupMap) {

        Map<String, Object> map = new HashMap<>();
        Group group = (Group) groupMap.get("group");

        List<String> schemas = new ArrayList<>();
        schemas.add("urn:ietf:params:scim:schemas:core:2.0:Group");

        map.put("schemas", schemas);
        map.put("id", group.getGroupKey().getGroupId());
        map.put("externalId", group.getGroupKey().getGroupId());
        map.put("displayName", group.getValue());

        // Members
        ArrayList<Map<String, Object>> members = new ArrayList<>();
        Set<User> userList = (Set<User>) groupMap.get("users");
        for (User user : userList) {
            Map<String, Object> obj = new HashMap<>();
            String ref = issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                    + "/scim/v2/Users/" + user.getUserKey().getUserId();
            obj.put("value", user.getUserKey().getUserId());
            obj.put("$ref", ref);   //User $ref
            obj.put("display", user.getUserName());
            obj.put("type", "User");
            members.add(obj);
        }
        map.put("members", members);

        // Meta information
        Map<String, Object> meta = new HashMap<>();
        meta.put("resourceType", "Group");
        meta.put("location", (issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                + "/scim/v2/Groups/" + group.getGroupKey().getGroupId()));
        meta.put("lastModified", group.getUpdatedAt());
        map.put("meta", meta);

        return map;
    }

    /**
     * @return JSON {@link Map} of {@link GroupListResponse} object
     */
    private Map<String, Object> toScimResource() {
        Map<String, Object> returnValue = new HashMap<>();
        List<String> schemas = new ArrayList<>();
        schemas.add("urn:ietf:params:scim:api:messages:2.0:ListResponse");
        returnValue.put("schemas", schemas);
        returnValue.put("totalResults", this.totalResults);
        returnValue.put("startIndex", this.startIndex);

//        List<Map> resources = this.list.stream().map(x -> toScimGroupResource(x))
//                .collect(Collectors.toList());

        if (this.count != 0) {
            returnValue.put("itemsPerPage", this.count);
        }
//        returnValue.put("Resources", resources);

        return returnValue;
    }

    public Map<String, Object> toScimGroupsResources(Map<String, Object> reqMap) {
        GroupListResponse groupListResponse = (GroupListResponse) reqMap.get("groupListResponse");
        this.totalResults = ((Long) reqMap.get("totalResults")).intValue();
        this.count = (Integer) reqMap.get("count");
        this.startIndex = (Integer) reqMap.get("startIndex");
        this.list = groupListResponse.getList();

        return toScimResource();

    }

    /**
     * @return JSON {@link Map} of {@link UserListResponse} object
     */
    private Map<String, Object> toSCIMAllResources() {
        Map<String, Object> returnValue = new HashMap<>();

        List<String> schemas = new ArrayList<>();
        schemas.add(ResponseCodeConstants.USER_LIST_RESPONSE_SCHEMA_URI);
        returnValue.put("schemas", schemas);
        returnValue.put("totalResults", this.totalResults);
        returnValue.put("startIndex", this.startIndex);
        LOG.info("startIndex  :: " + returnValue.get("startIndex"));

//        List<Map> resources = this.list.stream().map(x -> toSCIMResource(x))
//                .collect(Collectors.toList());

        if (this.count != 0) {
            returnValue.put("itemsPerPage", this.count);
        }
//        returnValue.put("Resources", resources);

        return returnValue;
    }


    public Map<String, Object> toScimUsersResources(Map<String, Object> reqMap) {
        UserListResponse userListResponse = (UserListResponse) reqMap.get("userListResponse");
        this.totalResults = ((Long) reqMap.get("totalResults")).intValue();
        this.count = (Integer) reqMap.get("count");
        this.startIndex = (Integer) reqMap.get("startIndex");
        this.list = userListResponse.getList();

        return toSCIMAllResources();

    }


    public List<Map> getList() {
        return list;
    }

    public void setList(List<Map> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalResults() {
        return Math.toIntExact(totalResults);
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}
