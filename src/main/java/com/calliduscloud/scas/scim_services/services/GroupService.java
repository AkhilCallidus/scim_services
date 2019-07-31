package com.calliduscloud.scas.scim_services.services;

import com.calliduscloud.scas.scim_services.exception.GroupAlreadyExistException;
import com.calliduscloud.scas.scim_services.exception.GroupNameAlreadyExistException;
import com.calliduscloud.scas.scim_services.exception.InvalidGroupIdException;
import com.calliduscloud.scas.scim_services.exception.InvalidGroupNameException;
import com.calliduscloud.scas.scim_services.exception.ItemNotFoundException;
import com.calliduscloud.scas.scim_services.exception.UserNotFoundException;
import com.calliduscloud.scas.scim_services.model.Group;
import com.calliduscloud.scas.scim_services.model.GroupKey;
import com.calliduscloud.scas.scim_services.model.User;
import com.calliduscloud.scas.scim_services.model.UserGroup;
import com.calliduscloud.scas.scim_services.model.UserGroupKey;
import com.calliduscloud.scas.scim_services.model.UserKey;
import com.calliduscloud.scas.scim_services.dao.GroupDao;
import com.calliduscloud.scas.scim_services.dao.UserDao;
import com.calliduscloud.scas.scim_services.dao.UserGroupDao;
import com.calliduscloud.scas.scim_services.response.GroupListResponse;
import com.calliduscloud.scas.scim_services.util.CommonUtils;
import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GroupService will handle all Group related operations
 * such as createGroup, updateGroup and deleteGroup and find Group.
 */
@Service
public class GroupService {
    private static final int COUNT = 10;
    private static final int START_INDEX = 1;
    private static final Integer ISE = 500;
    private static final Logger LOG = LoggerFactory.getLogger(GroupService.class);
    public static final String STRING = "://";
    public static final String STRING1 = ":";


    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserDao userDao;

    @Value("${issuer.name}")
    private String issuerName;
    @Value("${issuer.protocol}")
    private String issuerProtocol;
    @Value("${issuer.port}")
    private String issuerPort;
    @Value("${issuer.context.path}")
    private String issuerContextPath;

    /**
     * this method is used to find the Group by groupId.
     *
     * @param groupId .
     * @return Group.
     */
  /*  public Group getGroup(BigInteger tenantId, String groupId) {
        Group group = groupDao.findByGroupKey(new GroupKey(tenantId, groupId));
        return group;
    }*/
    public Map<String, Object> getGroup(BigInteger tenantId, String groupId) throws ItemNotFoundException {
        Map<String, Object> groupMap = new HashMap<>();
        Group group = groupDao.findByGroupKey(new GroupKey(tenantId, groupId));
        if (null == group) {
            throw new ItemNotFoundException("Group Not Found");
        } else {
            groupMap.put("group", group);

            List<UserGroup> groupUsers = userGroupDao.getUsersByGroupKey(group.getGroupKey().getGroupId(),
                    group.getGroupKey().getTenantId());
            Set<User> users = new HashSet<>();

            for (UserGroup userGroup : groupUsers) {
                User user = userDao.findByUserKey(new UserKey(tenantId,
                        userGroup.getUserGroupKey().getUserKey().getUserId()));
                users.add(user);

            }
            groupMap.put("users", users);
       /* User user = groupDao.findByUserKey(new UserKey(tenantId, userId));
        if (user == null) {
            throw new UsernameNotFoundException("getUser :: User not Found ");
        } else {
            userMap.put("user", user);
            userMap.put("groups", getGroupsforUser(user));
        }*/
        }
        return groupMap;
    }

    public Map<String, Object> getAllGroups(Map<String, String> params, BigInteger tenantId) {

        Map<String, Object> respMap = new HashMap<>();

        ArrayList<Group> groupList;
        ArrayList<Group> groupList1 = new ArrayList<>();
        long totalResults = 0L;

        // If not given count, default to 10
        int count = (params.get("count") != null) ? Integer.parseInt(params.get("count")) : COUNT;

        // If not given startIndex, default to 1
        int startIndex = (params.get("startIndex") != null)
                ? Integer.parseInt(params.get("startIndex")) : START_INDEX;

        if (startIndex < 1) {
            startIndex = 1;
        }
        LOG.info("startIndex  :: " + startIndex);
        int index = startIndex;
        String filter = params.get("filter");
        if (filter != null && filter.contains("eq")) {
            String regex = "(\\w+) eq \"([^\"]*)\"";            // ?filter=groupName eq "akhil"
            Pattern response = Pattern.compile(regex);

            Matcher match = response.matcher(filter);
            Boolean found = match.find();
            if (found) {
//                String searchKey = match.group(1);
                String searchValue = match.group(2);
                // Defaults to username lookup
                groupList = groupDao.findByValue(searchValue, tenantId);
            } else {
                groupList = groupDao.findAll(tenantId);
            }
        } else {
            groupList = groupDao.findAll(tenantId);
        }

        if (groupList != null && groupList.size() > 0) {

            totalResults = groupList.size();
            long temp = 0L;

            if (totalResults == startIndex) {
                temp = 1;
            } else if (totalResults == count + startIndex) {
                temp = count + startIndex;
            } else if ((count + startIndex) > totalResults) {

                temp = totalResults - startIndex > 0 ? totalResults - startIndex + 1 : 0;

            }

            long records = (count + startIndex) < totalResults ? (count) : temp;
            startIndex -= 1;

            for (int i = 0; i < records; i++) {

                groupList1.add(groupList.get(startIndex));
                startIndex += 1;
            }

        }

        LOG.info("count  :: " + count);
        LOG.info("totalResults  :: " + totalResults);
        LOG.info("totalResults for page  :: " + groupList1.size());

        List<Map> pGroups = new ArrayList<>();
        Map<String, Object> userMap;
        for (Group group : groupList1) {
            userMap = new HashMap<>();
            userMap.put("group", group);
            userMap.put("users", getUsersByGroup(group, tenantId));
            pGroups.add(userMap);
        }

        // Convert optional values into Optionals for ListResponse Constructor
        GroupListResponse groupListResponse = new GroupListResponse(pGroups, Optional.of(startIndex),
                Optional.of(count), Optional.of(totalResults));
        respMap.put("groupListResponse", groupListResponse);
        respMap.put("itemsPerPage", groupList1.size());
        respMap.put("count", count);
        respMap.put("startIndex", index);
        respMap.put("totalResults", totalResults);

        return respMap;
    }

    private Set<User> getUsersByGroup(Group group, BigInteger tenantId) {

        List<UserGroup> userGroup = userGroupDao.getUsersByGroupKey(group.getGroupKey().getGroupId(), tenantId);
        Set<User> users = new HashSet<>();
        for (UserGroup uGroup : userGroup) {
            User user = userDao.findByUserKey(uGroup.getUserGroupKey().getUserKey());
            users.add(user);

        }
        LOG.info("There are " + users.size() + " users found for group id " + group.getGroupKey().getGroupId());
        return users;
    }

    public List<Group> getAllTenantGroups(BigInteger tenantId, List<String> groups) {
        return groupDao.findAllByTenantId(tenantId, groups);
    }

    /**
     * this method is used to create the Group for given params values.
     *
     * @param params parameters to create Group.
     * @return create the Group with given params.
     */
    public Map<String, Object> createGroup(Map<String, Object> params, BigInteger tenantId)
            throws GroupAlreadyExistException, InvalidGroupNameException,
            GroupNameAlreadyExistException, InvalidGroupIdException {
        LOG.info("createGroup:: params : " + params);
        String groupName = params.get("displayName").toString();

        GroupKey groupKey = new GroupKey();
        Map<String, Object> groupMap = new HashMap<>();
        groupKey.setGroupId(params.get("externalId").toString());
        groupKey.setTenantId(tenantId);
        LOG.info("groupKey   : " + groupKey);

        if (groupKey.getGroupId().contains("%")) {
            LOG.error("Invalid groupId found");
            throw new InvalidGroupIdException("Invalid groupId found");
        }
        Group groupNameCheck = groupDao.findByGroupName(groupName, tenantId);
        if (null != groupNameCheck) {
            LOG.error(groupNameCheck.getValue() + " assigned to different groupId");
            throw new GroupNameAlreadyExistException(groupNameCheck.getValue() + " assigned to different groupId");
        }
        Group groupCheck = groupDao.findByGroupKey(groupKey);
        if (null != groupCheck) {
            throw new GroupAlreadyExistException("Group Already Exists");
        } else {
            Group group;
            if (null != groupName && !groupName.contains("+") && (groupName.endsWith("-SCAN")
                    || groupName.endsWith("-SCAI") || groupName.endsWith("_SCAN") || groupName.endsWith("_SCAI"))) {
                Set<User> userSet = getUsersFromRequest(tenantId, params);
//            groupMap.put("users", userSet);

                Set<User> userList = new HashSet<>();
//                for (User u : userSet) {
//                    userList.add(u);
//                    User dbReadUser = null;
//                    try {
//                        dbReadUser = userDao.findByUserKey(u.getUserKey());
//                    } catch (Exception e) {
//                        LOG.error("Error occurred while doing dbRead for User", e);
//                        e.printStackTrace();
//                    }
//                    if (dbReadUser == null) {
//                        userDao.save(u);
//                        LOG.info("User" + u.getUserKey() + " is created");
//                    }
//                }
                groupMap.put("users", userList);
                String name = "";
                for (User u : userSet) {
                    name = u.getUserName();
                    break;
                }
                String ref = issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                        + "/scim/v2/Groups/" + groupKey.getGroupId();
                group = new Group(groupKey, ref, groupName, CommonUtils.getTimeStamp(),
                        name, CommonUtils.getTimeStamp(), name);
                LOG.info("user::  " + group);
                LOG.info("userKey " + group.getGroupKey());

                group = groupDao.save(group);
                groupMap.put("group", group);
                UserGroup userGroup = new UserGroup();
                UserGroupKey key = new UserGroupKey();
                key.setGroupId(group.getGroupKey().getGroupId());
                Iterator<User> itr = userSet.iterator();
                while (itr.hasNext()) {
                    User grp11 = itr.next();
                    key.setUserKey(grp11.getUserKey());
                    userGroup.setUserGroupKey(key);
                    LOG.info("UserGroupKey::  " + key);
                    userGroup.setCreatedAt(group.getCreatedAt());
                    userGroup.setCreatedBy(group.getCreatedBy());

                    LOG.info("userGroup::  " + userGroup);
// TODO 2019-07-27 handle exception if members are not exists
                    userGroupDao.save(userGroup);
                    LOG.info("addUser :: adding user_group for group id ::  "
                            + userGroup.getUserGroupKey().getGroupId());
                }
                Group group1 = groupDao.findByGroupKey(new GroupKey(tenantId, groupKey.getGroupId()));
                groupMap.put("group", group1);

                List<UserGroup> groupUsers = userGroupDao.getUsersByGroupKey(group.getGroupKey().getGroupId(),
                        group.getGroupKey().getTenantId());
                Set<User> users = new HashSet<>();

                for (UserGroup userGroup1 : groupUsers) {
                    User user = userDao.findByUserKey(new UserKey(tenantId,
                            userGroup1.getUserGroupKey().getUserKey().getUserId()));
                    users.add(user);

                }
                groupMap.put("users", users);
            } else {
                LOG.error("Invalid GroupName Found");
                throw new InvalidGroupNameException("Invalid GroupName Found.");
            }
        }
        return groupMap;
    }

    /**
     * this method is used to update the Group.
     *
     * @param groupKey GroupKey
     * @param params   parameters to update Group.
     * @return Group with updated values.
     */
    /*public Map updateGroup(GroupKey groupKey, Map<String, Object> params) {
        Group group = groupDao.findByGroupKey(groupKey);
        try {
            if (params.get(REF) != null) {
                group.setRef(params.get(REF).toString());
            }

            if (params.get(VALUE) != null) {
                group.setValue(params.get(VALUE).toString());
            }

            group.setUpdatedAt(CommonUtils.getTimeStamp());
            group.setUpdatedBy(params.get(VALUE).toString());
            groupDao.save(group);
        } catch (Exception e) {
            LOG.error("error", e);
        }
        LOG.info("Updating Group..");
//        return group;
        return null;
    }*/
    public Map<String, Object> updateGroup(GroupKey groupKey, Map<String, Object> params)
            throws ItemNotFoundException, InvalidGroupNameException, GroupNameAlreadyExistException,
            UserNotFoundException{
        LOG.info("params : " + params);
        Map<String, Object> groupMap = new HashMap<>();
        String groupName = params.get("displayName").toString();
        Group group = groupDao.findByGroupKey(groupKey);
        if (group == null) {
            LOG.info("Group not found for groupId " + groupKey.getGroupId() + ".");
//                userMap = addUser(params, tenantId);
            throw new ItemNotFoundException("groupId " + groupKey.getGroupId() + " Not Found ");
        }
        Group groupNameCheck = groupDao.findByGroupName(groupName, groupKey.getTenantId());
        if (group.getValue().equals(groupName) || null == groupNameCheck) {
//                String location = ((Map<String, Object>) params.get("meta")).get("location").toString();
            String ref = issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                    + "/scim/v2/Groups/" + groupKey.getGroupId();
            if (null != groupName && (groupName.contains("-SCAN") || groupName.contains("-SCAI")
                    || groupName.contains("_SCAN") || groupName.contains("_SCAI"))) {
                Set<User> userSet = updateUsers(groupKey.getTenantId(), params);
                group.setGroupKey(groupKey);
                group.setRef(ref);
                group.setValue(groupName);
                group.setUpdatedAt(CommonUtils.getTimeStamp());
                group.setUpdatedBy(ResponseCodeConstants.SYSTEM);

                LOG.info("Updating Group..");
                group = groupDao.save(group);
                groupMap.put("group", group);
                groupMap.put("users", userSet);
                UserGroup userGroup = null;
                UserGroupKey key = new UserGroupKey();
                key.setGroupId(group.getGroupKey().getGroupId());

                userGroupDao.deleteUsersByGroupKey(key.getGroupId(), groupKey.getTenantId());
                LOG.info("deleteUsersByGroupKey :: entry :: " + key.getGroupId());
                Iterator<User> itr = userSet.iterator();
                LOG.info("inserting new records  :: entry :: " + key.getGroupId());
                while (itr.hasNext()) {
//                        key.setGroupId(itr.next().getGroupKey().getGroupId());
                    key.setUserKey(itr.next().getUserKey());
                    userGroup = new UserGroup();
                    userGroup.setUserGroupKey(key);
                    userGroup.setCreatedAt(group.getCreatedAt());
                    userGroup.setCreatedBy(group.getCreatedBy());
                    userGroup = userGroupDao.save(userGroup);
                    LOG.info("Updated user group  :: for group id ::  "
                            + userGroup.getUserGroupKey().getGroupId());
                }
            } else {
                LOG.error("Invalid GroupName Found");
                throw new InvalidGroupNameException("Invalid GroupName Found.");
            }
        } else {
            LOG.error(groupNameCheck.getValue() + " assigned to different groupId");
            throw new GroupNameAlreadyExistException(groupNameCheck.getValue() + " assigned to different groupId");
        }
        return groupMap;
    }

    private Set<User> updateUsers(BigInteger tenantId, Map<String, Object> params) throws UserNotFoundException{
        ArrayList userArr = (ArrayList) params.get("members");


        LOG.info("updateUsers : users array :: " + userArr);
        Set<User> userSet = new HashSet<>();


        //
        try {
            for (Object o : userArr) {

                Map<String, Object> userMap = (Map<String, Object>) o;

                String userId = userMap.get("value").toString();
                UserKey userKey = new UserKey(tenantId, userId);
                // TODO 2019-07-27 handle exception if members are not exists
                User users = userDao.findByUserKey(userKey);
                if (users == null) {
                    LOG.error("Users not existed in the system.");
                    throw new UserNotFoundException("Users not existed in the system.");
                }
                String userName = users.getUserName();


                User user = new User(userKey, userName, CommonUtils.getTimeStamp(),
                        userName, CommonUtils.getTimeStamp(), userName);
                User user1 = userDao.findByUserKey(user.getUserKey());
                if (user1 != null) {

                    user1.setUserName(userName);
                    user1.setUpdatedBy(userName);
                    user1.setUpdatedAt(CommonUtils.getTimeStamp());
//                    user1 = userDao.save(user1);
                    userSet.add(user1);
                } else {

//                    user = userDao.save(user);
                    userSet.add(user);
                }
            }
        } catch (Exception ex) {
            LOG.info("Exception while Updating groups ", ex);
        }
        LOG.info("updated users   :: " + userSet + " for group " + params.get("displayName"));
        return userSet;
    }

    /**
     * this method is used to delete the Group.
     *
     * @param groupId .
     * @return success message.
     */
    public Object deleteGroup(BigInteger tenantId, String groupId) throws ItemNotFoundException {
        LOG.info("GroupId   : " + groupId);
        GroupKey groupKey = new GroupKey(tenantId, groupId);
        Group group = groupDao.findByGroupKey(groupKey);
        if (group == null) {
            throw new ItemNotFoundException("groupId " + groupId + " Not Found ");
        }
        LOG.info("Deleting User..");
        List<UserGroup> userGroups = userGroupDao.getUsersByGroupKey(groupId, tenantId);
        LOG.info(" User groups ::: " + userGroups);
        for (UserGroup ug : userGroups) {
            userGroupDao.delete(ug);
        }
        groupDao.delete(group);
        return "Data Successfully Deleted!";

    }

    /*public GroupListResponse getAllGroupsByTenantId(BigInteger tenantId) {
        List<Group> groups = groupDao.getAllGroupsByTenantId(tenantId);
        GroupListResponse response = new GroupListResponse();
        response.setList(groups);
        response.setTotalResults(groups.size());
        response.setCount(groups.size());
        return response;
    }*/

    private Set<User> getUsersFromRequest(BigInteger tenantId, Map params) {
        ArrayList<Map<String, Object>> userArr = (ArrayList<Map<String, Object>>) params.get("members");
        LOG.info("getUsers : users array :: " + userArr);
        Set<User> userSet = new HashSet<>();
        User user;
        try {
            for (Map<String, Object> map : userArr) {     // java.lang.NullPointerException: null
                user = new User();
                user.setUserName(ResponseCodeConstants.SYSTEM);
                user.setUserKey(new UserKey(tenantId, map.get("value").toString()));
                user.setCreatedAt(CommonUtils.getTimeStamp());
                user.setCreatedBy(user.getUserName());
                user.setUpdatedAt(CommonUtils.getTimeStamp());
                user.setUpdatedBy(user.getUserName());
                userSet.add(user);
            }
        } catch (Exception ex) {
            LOG.info("exception ", ex);
        }
        return userSet;
    }

    public Map scimError(String message, Optional<Integer> statusCode) {

        Map<String, Object> returnValue = new HashMap<>();
        List<String> schemas = new ArrayList<>();
        schemas.add("urn:ietf:params:scim:api:messages:2.0:Error");
        returnValue.put("schemas", schemas);
        returnValue.put("message", message);

        // Set default to 500
        returnValue.put("status", statusCode.orElse(ISE));
        return returnValue;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
}