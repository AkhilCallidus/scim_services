package com.calliduscloud.scas.scim_services.dao;

import com.calliduscloud.scas.scim_services.model.User;
//import com.calliduscloud.scas.scim_services.model.UserJson;
import com.calliduscloud.scas.scim_services.model.UserKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, UserKey> {

    // Query by userName
    @Query("SELECT u FROM User as u WHERE u.userName = :name")
    Page<User> findByUserName(@Param("name") String name, Pageable pagable);

    @Query("SELECT u FROM User as u WHERE u.userKey.tenantId = :tenantId")
    Page<User> findAll(@Param("tenantId") BigInteger tenantId, Pageable pagable);

//    @Query("SELECT u FROM User as u WHERE u.userName = :name and u.userKey.tenantId = :tenantId")
//    Page<User> findByUserName(@Param("name") String name, @Param("tenantId") BigInteger tenantId, Pageable pagable);

    @Query("SELECT u FROM User as u WHERE u.userName = :name and u.userKey.tenantId = :tenantId")
    ArrayList<User> findByUserName(@Param("name") String name, @Param("tenantId") BigInteger tenantId);

    @Query("SELECT u FROM User as u WHERE u.userKey.userId = :userId and u.userKey.tenantId = :tenantId")
    Page<User> findByUserId(@Param("userId") String userId, @Param("tenantId") BigInteger tenantId, Pageable pagable);

    // Query by userKey
    User findByUserKey(UserKey userKey);

    @Query("SELECT u FROM User as u WHERE u.userKey.tenantId = :tenantId")
    List<User> getAllUsersByTenantId(@Param("tenantId") BigInteger tenantId);

    @Query("select u, ug.userGroupKey.groupId as groupId, grp.value as groupName"
            + " from User as u, UserGroup ug, Group grp"
            + " where u.userKey.tenantId = :tenantId and u.userKey.userId = ug.userGroupKey.userKey.userId"
            + " and u.userKey.tenantId = ug.userGroupKey.userKey.tenantId and "
            + " ug.userGroupKey.groupId = grp.groupKey.groupId "
            + "and ug.userGroupKey.userKey.tenantId = grp.groupKey.tenantId and grp.value like '%SCAN' "
            + "and grp.value not like 'APP%'")
    List getUsersByTenantId(@Param("tenantId") BigInteger tenantId);

//    @Query("select new com.calliduscloud.scas.model.UserJson(u.userKey.userId, u.userName, "
//            + "ug.userGroupKey.groupId, grp.value) "
//            + "from User as u, UserGroup ug, Group grp "
//            + "where u.userKey.tenantId = :tenantId and u.userKey.userId = ug.userGroupKey.userKey.userId "
//            + "and u.userKey.tenantId = ug.userGroupKey.userKey.tenantId and "
//            + "ug.userGroupKey.groupId = grp.groupKey.groupId "
//            + "and ug.userGroupKey.userKey.tenantId = grp.groupKey.tenantId and grp.value like '%SCAN' "
//            + "and grp.value not like 'APP%'")
//    List<UserJson> getTenantUsersList(@Param("tenantId") BigInteger tenantId);

    @Query("SELECT u FROM User as u WHERE u.userName = :name and u.userKey.tenantId = :tenantId")
    User getUserName(@Param("name") String name, @Param("tenantId") BigInteger tenantId);

    @Query("SELECT u FROM User as u WHERE u.userKey.tenantId = :tenantId")
    ArrayList<User> findAll(@Param("tenantId") BigInteger tenantId);
}