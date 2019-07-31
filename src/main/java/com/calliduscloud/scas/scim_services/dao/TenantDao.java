package com.calliduscloud.scas.scim_services.dao;

import com.calliduscloud.scas.scim_services.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TenantDao extends JpaRepository<Tenant, BigInteger> {

    @Query("SELECT t FROM Tenant as t  WHERE t.tenantId = :id")
    Tenant findByTenantId(@Param("id") BigInteger tenantId);

    List<Tenant> findByIpsClientIdAndIpsClientSecret(String ipsClientId, String ipsClientSecret);
    List<Tenant> findByOauthClientIdAndOauthClientSecret(String oauthClientId, String oauthClientSecret);
}