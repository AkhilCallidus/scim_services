package com.calliduscloud.scas.scim_services.services;

import com.calliduscloud.scas.scim_services.response.exception.InvalidUserIdException;
import com.calliduscloud.scas.scim_services.response.exception.InvalidUserNameFoundException;
import com.calliduscloud.scas.scim_services.response.exception.ItemNotFoundException;
import com.calliduscloud.scas.scim_services.response.exception.UserAlreadyExistException;
import com.calliduscloud.scas.scim_services.response.exception.UserNameAlreadyExistException;
import com.calliduscloud.scas.scim_services.model.Group;
import com.calliduscloud.scas.scim_services.model.GroupKey;
import com.calliduscloud.scas.scim_services.model.User;
import com.calliduscloud.scas.scim_services.model.UserGroup;
//import com.calliduscloud.scas.scim_services.model.UserJson;
import com.calliduscloud.scas.scim_services.model.UserKey;
import com.calliduscloud.scas.scim_services.dao.GroupDao;
import com.calliduscloud.scas.scim_services.dao.TenantDao;
import com.calliduscloud.scas.scim_services.dao.UserDao;
import com.calliduscloud.scas.scim_services.dao.UserGroupDao;
import com.calliduscloud.scas.scim_services.response.GroupDTO;
import com.calliduscloud.scas.scim_services.response.UserListResponse;
import com.calliduscloud.scas.scim_services.util.CommonUtils;
import com.calliduscloud.scas.scim_services.util.ResponseCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.util.Iterator;

/**
 * UserService will handle all User related operations
 * such as createUser, updateUser and deleteUser and find User.
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final int COUNT = 10;
    public static final String STRING = "://";
    public static final String STRING1 = ":";

    private UserDao userDao;
    private TenantDao tenantDao;
    private GroupDao groupDao;
    private UserGroupDao userGroupDao;

    @Value("${issuer.name}")
    private String issuerName;
    @Value("${issuer.protocol}")
    private String issuerProtocol;
    @Value("${issuer.port}")
    private String issuerPort;
    @Value("${issuer.context.path}")
    private String issuerContextPath;

    @Autowired
    private UserService(UserGroupDao userGroupDao, GroupDao groupDao, TenantDao tenantDao, UserDao userDao) {
        this.userGroupDao = userGroupDao;
        this.groupDao = groupDao;
        this.tenantDao = tenantDao;
        this.userDao = userDao;
    }

    public UserService() {
    }

    /**
     * this method is used to find the User by userKey.
     *
     * @param tenantId
     * @param userId
     * @return list of users and groups by using tenantId and userId.
     */
    public Map<String, Object> getUser(BigInteger tenantId, String userId) {
        Map<String, Object> userMap = new HashMap<>();
        UserKey userKey = new UserKey(tenantId, userId);
        User user = userDao.findByUserKey(userKey);
        if (user == null) {
            throw new UsernameNotFoundException("getUser :: User not Found ");
        } else {
            userMap.put("user", user);
            userMap.put("groups", getGroupsforUser(user));
        }
        return userMap;
    }

    private List<GroupDTO> getGroupsforUser(User user) {
        List<GroupDTO> groups = new ArrayList<>();
        Group group;
        Set<Group> groupSet = new HashSet<>();
        List<UserGroup> userGroups = userGroupDao.getUserGroups(user.getUserKey().getUserId(),
                user.getUserKey().getTenantId());
        for (UserGroup userGroup : userGroups) {
            group = groupDao.findByGroupKey(new GroupKey(userGroup.getUserGroupKey().
                    getUserKey().getTenantId(), userGroup.getUserGroupKey().getGroupId()));
            String ref = issuerProtocol + STRING + issuerName + STRING1 + issuerPort + issuerContextPath
                    + "/scim/v2/Groups/" + group.getGroupKey().getGroupId();
            groups.add(new GroupDTO(ref, group.getGroupKey().getGroupId(), group.getValue()));
            groupSet.add(group);
        }
        user.setGroups(groupSet);
        return groups;
    }

    public Map<String, Object> getAllUsers(Map<String, String> params, BigInteger tenantId) {

        Map<String, Object> respMap = new HashMap<>();
        ArrayList<User> userList;
        ArrayList<User> userList1 = new ArrayList<>();
        long totalResults = 0L;

        // If not given count, default to 10
        int count = (params.get("count") != null) ? Integer.parseInt(params.get("count")) : COUNT;

        // If not given startIndex, default to 1
        int startIndex = (params.get("startIndex") != null) ? Integer.parseInt(params.get("startIndex")) : 1;

        if (startIndex < 1) {
            startIndex = 1;
        }

        LOG.info("startIndex  :: " + startIndex);
        int index = startIndex;
        String filter = params.get("filter");
        if (filter != null && filter.contains("eq")) {
            String regex = "(\\w+) eq \"([^\"]*)\"";            // ?filter=userName eq "akhil"
            Pattern response = Pattern.compile(regex);

            Matcher match = response.matcher(filter);
            Boolean found = match.find();
            if (found) {
//                String userId = match.group(1);
                String searchValue = match.group(2);
                // Defaults to username lookup
                userList = userDao.findByUserName(searchValue, tenantId);
            } else {
                userList = userDao.findAll(tenantId);
            }
        } else {
            userList = userDao.findAll(tenantId);
        }

        if (userList != null && userList.size() > 0) {

            totalResults = userList.size();
            long temp = 0L;

            if (totalResults == startIndex) {
                temp = 1;
                // userList1.add(userList.get(startIndex-1));

            } else if (totalResults == count + startIndex) {
                temp = count + startIndex;
            } else if ((count + startIndex) > totalResults) {

                temp = totalResults - startIndex > 0 ? totalResults - startIndex + 1 : 0;

            }

            long records = (count + startIndex) < totalResults ? (count) : temp;
            startIndex -= 1;

            for (int i = 0; i < records; i++) {

                userList1.add(userList.get(startIndex));
                startIndex += 1;
            }

        }

        LOG.info("count  :: " + count);
        LOG.info("totalResults  :: " + totalResults);
        LOG.info("totalResults for page  :: " + userList1.size());
        List<Map> pUsers = new ArrayList<>();
        Map<String, Object> userMap;
        for (User user : userList1) {
            userMap = new HashMap<>();
            userMap.put("user", user);
            userMap.put("groups", getGroupsforUser(user));
            pUsers.add(userMap);
        }

        // Convert optional values into Optionals for ListResponse Constructor
        UserListResponse userListResponse = new UserListResponse(pUsers, Optional.of(startIndex),
                Optional.of(count), Optional.of(totalResults));

        respMap.put("userListResponse", userListResponse);
        respMap.put("itemsPerPage", userList1.size());
        respMap.put("count", count);
        respMap.put("startIndex", index);
        respMap.put("totalResults", totalResults);

        return respMap;
    }

    public Map<String, Object> addUser(Map<String, Object> params, BigInteger tenantId)
            throws UserAlreadyExistException, UserNameAlreadyExistException,
            InvalidUserIdException, InvalidUserNameFoundException {

        LOG.info("addUser :: params : " + params);
        String userName = (String) params.get("userName");
        Map<String, Object> userMap = new HashMap<>();
        UserKey userKey = new UserKey();
        userKey.setUserId(params.get("externalId").toString());
        userKey.setTenantId(tenantId);
        LOG.info("userKey   : " + userKey);
        if (userName.contains("+")) {
            LOG.error("Invalid UserName Found");
            throw new InvalidUserNameFoundException("Invalid UserName Found");
        }
        User userNameCheck = userDao.getUserName(userName, tenantId);
        if (null != userNameCheck) {
            LOG.error(userNameCheck.getUserName() + " assigned to different userId");
            throw new UserNameAlreadyExistException(userNameCheck.getUserName() + " assigned to different userId");
        }
        User userCheck = userDao.findByUserKey(userKey);
        if (null != userCheck) {
            LOG.error("User Already Exists");
            throw new UserAlreadyExistException("User Already Exists");
        } else {
            User user;
            if (null != userKey.getUserId() && !(userKey.getUserId().contains("%"))) {
                user = new User(userKey, params.get("userName").toString(),
                        CommonUtils.getTimeStamp(), params.get("userName").toString(),
                        CommonUtils.getTimeStamp(), params.get("userName").toString());
                LOG.info("user::  " + user);
                LOG.info("userKey " + user.getUserKey());

                user = userDao.save(user);
                userMap.put("user", user);
                userMap.put("groups", getGroupsforUser(user));
            } else {
                LOG.error("Invalid userId found ");
                throw new InvalidUserIdException("Invalid userId found");
            }
        }
        return userMap;
    }

    private Set<Group> getGroupsFromRequest(BigInteger tenantId, Map params) {
        ArrayList groupArr = (ArrayList) params.get("groups");
        String userName = params.get("userName").toString();

        LOG.info("getGroups : groups array :: " + groupArr);
        Set<Group> groupSet = new HashSet<>();
        Group group = null;
        try {
            for (Object o : groupArr) {     // java.lang.NullPointerException: null

                Map<String, Object> groupMap = (Map<String, Object>) o;
                LOG.info("getGroups :: groupMAp :: " + groupMap);
                String ref = groupMap.get("$ref").toString();
                String[] arr = ref.split("/");
                String groupId = arr[arr.length - 1];
                GroupKey groupKey = new GroupKey(tenantId, groupId);
                LOG.info("getGroups :: ref " + ref);
                LOG.info("groupKey  :: " + groupKey);

                String groupName = groupMap.get("value").toString();

                if (null != groupName && (groupName.contains("-SCAN") || groupName.contains("-SCAI")
                        || groupName.contains("_SCAN") || groupName.contains("_SCAI"))) {
                    group = new Group();
                    group.setRef(ref);
                    group.setGroupKey(groupKey);
                    group.setValue(groupName);
                    group.setCreatedAt(CommonUtils.getTimeStamp());
                    group.setUpdatedAt(CommonUtils.getTimeStamp());
                    group.setUpdatedBy(userName);
                    group.setCreatedBy(userName);

                    groupSet.add(group);
                }
            }
        } catch (Exception ex) {
            LOG.info("exception ", ex);
        }
        return groupSet;
    }

    /**
     * this method is used to update the User.
     *
     * @param userId .
     * @param params parameters to update User.
     * @return User with updated values.
     */
    public Map<String, Object> updateUser(BigInteger tenantId, String userId, Map<String, Object> params)
            throws ItemNotFoundException, UserNameAlreadyExistException {
        UserKey userKey = new UserKey(tenantId, userId);
        LOG.info("UserKey : " + userKey);
        LOG.info("params : " + params);
        Map<String, Object> userMap = new HashMap<>();
        String userName = params.get("userName").toString();
        User user = userDao.findByUserKey(userKey);
        if (user == null) {
            LOG.info("User not found for userId " + userKey.getUserId() + ".");
            throw new ItemNotFoundException("userID " + userId + " Not Found ");
        }
        User userNameCheck = userDao.getUserName(userName, tenantId);
        if (user.getUserName().equals(userName) || null == userNameCheck) {
            try {
                //                Set<Group> groupSet = updateGroups(tenantId, params);
                user.setUpdatedAt(CommonUtils.getTimeStamp());
                user.setUpdatedBy(userName);
                //                user.setGroups(groupSet);
                user.setUserName(userName);

                LOG.info("Updating User..");
                user = userDao.save(user);
                userMap.put("user", user);
                userMap.put("groups", getGroupsforUser(user));
            } catch (Exception ex) {
                LOG.error("Error while updating user :: error message :: ", ex);
            }
            return userMap;
        } else {
            LOG.error(userNameCheck.getUserName() + " assigned to different userId");
            throw new UserNameAlreadyExistException(userNameCheck.getUserName() + " assigned to different userId");
        }
    }

    private Set<Group> updateGroups(BigInteger tenantId, Map<String, Object> params) {
        ArrayList groupArr = (ArrayList) params.get("groups");
        String userName = params.get("userName").toString();

        LOG.info("getGroups : groups array :: " + groupArr);
        Set<Group> groupSet = new HashSet<>();
        Group group = null;
        try {
            for (Object o : groupArr) {

                Map<String, Object> groupMap = (Map<String, Object>) o;
                LOG.info("getGroups :: groupMAp :: " + groupMap);
                String ref = groupMap.get("$ref").toString();
                String[] arr = ref.split("/");
                String groupId = arr[arr.length - 1];
                GroupKey groupKey = new GroupKey(tenantId, groupId);
                LOG.info("getGroups :: ref " + ref);
                String groupName = groupMap.get("value").toString();
                if (null != groupName && (groupName.contains("-SCAN") || groupName.contains("-SCAI")
                        || groupName.contains("_SCAN") || groupName.contains("_SCAI"))) {
                    group = groupDao.findByGroupKey(groupKey);
                    if (group != null) {
                        group.setRef(ref);
                        group.setGroupKey(groupKey);
                        group.setValue(groupName);
                        group.setUpdatedAt(CommonUtils.getTimeStamp());
                        group.setUpdatedBy(userName);
                    } else {
                        group = new Group();
                        group.setRef(ref);
                        group.setGroupKey(groupKey);
                        group.setValue(groupName);
                        group.setCreatedAt(CommonUtils.getTimeStamp());
                        group.setUpdatedAt(CommonUtils.getTimeStamp());
                        group.setUpdatedBy(userName);
                        group.setCreatedBy(userName);
                    }
                    group = groupDao.save(group);
                    groupSet.add(group);
                }

            }
        } catch (Exception ex) {
            LOG.info("Exception while Updating groups " + ex.getMessage());
        }
        return groupSet;
    }

    /**
     * this method is used to delete the User.
     *
     * @param tenantId .
     * @param userId   .
     * @return success message.
     */
    @Transactional
    public Object deleteUser(BigInteger tenantId, String userId) throws ItemNotFoundException {
        UserKey userKey = new UserKey(tenantId, userId);
        LOG.info("UserId : " + userId);
        User user = userDao.findByUserKey(userKey);

        if (user == null) {
            throw new ItemNotFoundException("userID " + userId + " Not Found");
        }
        LOG.info("Deleting User..");
//        List<UserGroup> userGroups = userGroupDao.getUserGroups(userId, tenantId);
//        LOG.info(" User groups ::: " + userGroups);
//        Iterator<UserGroup> itr = userGroups.iterator();
//        while (itr.hasNext()) {
//            UserGroup ug = itr.next();
//            userGroupDao.delete(ug);
//        for (UserGroup ug : userGroups) {
//            userGroupDao.delete(ug);
//        }
        userGroupDao.deleteGroupsByUserKey(userKey.getUserId(), userKey.getTenantId());
        userDao.delete(user);
        return "Data Successfully Deleted!";

    }

    public UserListResponse getAllUsersByTenantId(BigInteger tenantId) {
        List<User> users = userDao.getAllUsersByTenantId(tenantId);
        List<Map> gUsers = new ArrayList<>();
        UserListResponse response = new UserListResponse();
        Map<String, Object> userMap = null;
        for (User user : users) {
            userMap = new HashMap<>();
            userMap.put("user", user);
            userMap.put("groups", getGroupsforUser(user));
            gUsers.add(userMap);
        }
        response.setList(gUsers);
        response.setTotalResults(users.size());
        response.setCount(users.size());

        return response;

    }

//    public List<UserJson> getTenantUsersList(BigInteger tenantId) {
//        List<UserJson> users = userDao.getTenantUsersList(tenantId);
//        return users;
//    }

    public int updateUserGroups(BigInteger tenantId, String userId, String oldGroupId, String newGroupId) {
        return userGroupDao.updateUserGroups(userId, tenantId, oldGroupId, newGroupId);
    }

    /**
     * Output custom error message with response code.
     *
     * @param message    Scim error message
     * @param statusCode Response status code
     * @return JSON {@link Map} of {@link User}
     */
    public Map scimError(String message, Optional<Integer> statusCode) {

        Map<String, Object> returnValue = new HashMap<>();
        List<String> schemas = new ArrayList<>();
        schemas.add(ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI);
        returnValue.put("schemas", schemas);
        returnValue.put("message", message);

        // Set default to 500
        returnValue.put("status", statusCode.orElse(ResponseCodeConstants.CODE_INTERNAL_ERROR));
        return returnValue;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public TenantDao getTenantDao() {
        return tenantDao;
    }

    public void setTenantDao(TenantDao tenantDao) {
        this.tenantDao = tenantDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

}
