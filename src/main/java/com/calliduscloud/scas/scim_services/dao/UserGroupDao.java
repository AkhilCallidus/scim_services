package com.calliduscloud.scas.scim_services.dao;

import com.calliduscloud.scas.scim_services.model.UserGroup;
import com.calliduscloud.scas.scim_services.model.UserGroupKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserGroupDao extends JpaRepository<UserGroup, UserGroupKey> {
   // UserGroup findByGroupId(String groupId);

    @Query("SELECT grp.value FROM Group AS grp INNER JOIN UserGroup usrGrp ON"
            + " grp.groupKey.groupId = usrGrp.userGroupKey.groupId"
            + " WHERE usrGrp.userGroupKey.userKey.userId = :userId AND "
            + "usrGrp.userGroupKey.userKey.tenantId = :tenantId")
    List<String> getUserGroupNames(@Param("userId") String userId,
                                   @Param("tenantId") BigInteger tenantId);

    @Query("SELECT usrGrp from UserGroup AS usrGrp "
         + "where usrGrp.userGroupKey.userKey.userId = :userId AND "
         + "usrGrp.userGroupKey.userKey.tenantId = :tenantId")
    List<UserGroup> getUserGroups(@Param("userId") String userId,
                                  @Param("tenantId") BigInteger tenantId);

    @Modifying
    @Query("DELETE from UserGroup usrGrp "
            + "where usrGrp.userGroupKey.userKey.userId = :userId AND "
            + "usrGrp.userGroupKey.userKey.tenantId = :tenantId")
    void deleteGroupsByUserKey(@Param("userId") String userId,
                               @Param("tenantId") BigInteger tenantId);

    @Transactional
    @Modifying
    @Query("update UserGroup grp set grp.userGroupKey.groupId = :newGroupId where grp.userGroupKey.userKey.userId = "
            + ":userId and grp.userGroupKey.userKey.tenantId = :tenantId and grp.userGroupKey.groupId = :oldGroupId")
    int updateUserGroups(@Param("userId") String userId,
                         @Param("tenantId") BigInteger tenantId,
                         @Param("oldGroupId") String oldGroupId,
                         @Param("newGroupId") String newGroupId);

    @Query("SELECT usrGrp from UserGroup AS usrGrp "
            + "where usrGrp.userGroupKey.groupId = :groupId AND "
            + "usrGrp.userGroupKey.userKey.tenantId = :tenantId")
    List<UserGroup> getUsersByGroupKey(@Param("groupId") String groupId,
                                       @Param("tenantId") BigInteger tenantId);

    @Modifying
    @Transactional
    @Query("DELETE from UserGroup usrGrp "
            + "where usrGrp.userGroupKey.groupId = :groupId AND "
            + "usrGrp.userGroupKey.userKey.tenantId = :tenantId")
    void deleteUsersByGroupKey(@Param("groupId") String groupId,
                               @Param("tenantId") BigInteger tenantId);
}
