package com.calliduscloud.scas.scim_services.dao;

import com.calliduscloud.scas.scim_services.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface EnvironmentDao extends JpaRepository<Environment, BigInteger> {
    Environment findByEnvironmentName(String environmentName);

    @Query("SELECT env from Environment AS env where env.clientEnvironmentId = :clientEnvironmentId")
    Environment findByClientEnvironmentId(@Param("clientEnvironmentId") String clientEnvironmentId);

    Environment findByEnvironmentHostName(String environmentHostName);

    List<Environment> findEnvironmentsByTenantId(@Param("tenantId") BigInteger tenantId);

    Environment findEnvironmentByTenantId(@Param("tenantId") BigInteger tenantId);

    @Query("SELECT env.environmentHostName from Environment AS env where env.tenantId = :tenantId")
    List<String> findEnvironMentHostNamesByTenantId(@Param("tenantId") BigInteger tenantId);
}
