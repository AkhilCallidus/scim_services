package com.calliduscloud.scas.scim_services.dao;

import com.calliduscloud.scas.scim_services.model.Group;
import com.calliduscloud.scas.scim_services.model.GroupKey;
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
public interface GroupDao extends JpaRepository<Group, GroupKey> {

    // Query by value
    @Query("SELECT g FROM Group as g WHERE g.value = :name and g.groupKey.tenantId = :tenantId")
    Page<Group> findByValue(@Param("name") String name, @Param("tenantId") BigInteger tenantId, Pageable pagable);

    @Query("SELECT g FROM Group as g WHERE g.groupKey.groupId = :groupId and g.groupKey.tenantId = :tenantId")
    Page<Group> findByGroupId(@Param("groupId") String groupId,
                              @Param("tenantId") BigInteger tenantId, Pageable pagable);

    @Query("SELECT g FROM Group as g WHERE g.groupKey.tenantId = :tenantId")
    Page<Group> findAll(@Param("tenantId") BigInteger tenantId, Pageable pagable);

    // Query by userKey
    Group findByGroupKey(GroupKey groupKey);

    @Query("SELECT g FROM Group g WHERE g.groupKey.tenantId = :tenantId")
    List<Group> getAllGroupsByTenantId(@Param("tenantId") BigInteger tenantId);
    // Query by groupid
//    Group findByGroupId(String groupId);

    @Query("SELECT u FROM Group u WHERE u.groupKey.tenantId = :tenantId and u.value in "
            + "(:groups)")
    List<Group> findAllByTenantId(@Param("tenantId") BigInteger tenantId, @Param("groups") List<String> groups);

    @Query("SELECT g FROM Group as g WHERE g.value = :name and g.groupKey.tenantId = :tenantId")
    Group findByGroupName(@Param("name") String name, @Param("tenantId") BigInteger tenantId);

    @Query("SELECT g FROM Group as g WHERE g.value = :name and g.groupKey.tenantId = :tenantId")
    ArrayList<Group> findByValue(@Param("name") String name, @Param("tenantId") BigInteger tenantId);

    @Query("SELECT g FROM Group as g WHERE g.groupKey.tenantId = :tenantId")
    ArrayList<Group> findAll(@Param("tenantId") BigInteger tenantId);
}