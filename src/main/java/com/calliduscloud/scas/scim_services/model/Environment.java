package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Database schema for {@link Environment} object.
 */
@Entity
@Table(name = "SC_ENVIRONMENT")
public class Environment implements Serializable {
    private static final int ENVIRONMENT_HOST_NAME_LENGTH = 500;
    private static final int ENVIRONMENT_TYPE_LENGTH = 10;
    private static final int ENVIRONMENT_NAME_LENGTH = 20;
    private static final int ENVIRONMENT_CERTIFICATE_LENGTH = 1000;
    private static final int CREATED_BY_LENGTH = 255;
    private static final int CLIENT_ENVIRONMENT_ID_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENVIRONMENT_ID")
    private BigInteger environmentId;

    @OneToOne
    @JoinColumn(name = "ENVIRONMENT_ID")
    TenantConfig  scanEnvironment;

    @Column(length = ENVIRONMENT_HOST_NAME_LENGTH, name = "ENVIRONMENT_HOST_NAME")
    private String environmentHostName;

    @Column(length = ENVIRONMENT_TYPE_LENGTH, name = "ENVIRONMENT_TYPE")
    private String environmentType;

    @Column(length = ENVIRONMENT_NAME_LENGTH, name = "ENVIRONMENT_NAME")
    String environmentName;

    @Column(name = "TENANT_ID")
    private BigInteger tenantId;

    @Column(length = CLIENT_ENVIRONMENT_ID_LENGTH, name = "CLIENT_ENVIRONMENT_ID")
    private String clientEnvironmentId;

    @Column(length = ENVIRONMENT_CERTIFICATE_LENGTH, name = "ENVIRONMENT_CERTIFICATE")
    private String environmentCertificate;

    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;

    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFY_DATE")
    private Timestamp modifiedAt;

    @Column(length = CREATED_BY_LENGTH, name = "MODIFIED_BY")
    private String modifiedBy;

    /**
     *  default constructor.
     */
    public Environment() {
    }

    /**
     *  This is a overloaded constructor used to construct data for the obj.
     * @param environmentId environmentId
     * @param environmentHostName
     * @param environmentType
     * @param clientEnvironmentId
     * @param environmentName
     * @param tenantId
     * @param environmentCertificate
     * @param createdAt
     * @param createdBy
     * @param modifiedAt
     * @param modifiedBy
     */
    public Environment(BigInteger environmentId, String environmentHostName, String environmentType,
                       String environmentName, BigInteger tenantId, String clientEnvironmentId,
                       String environmentCertificate, Timestamp createdAt, String createdBy,
                       Timestamp modifiedAt, String modifiedBy) {
        this.environmentId = environmentId;
        this.environmentHostName = environmentHostName;
        this.environmentType = environmentType;
        this.environmentName = environmentName;
        this.tenantId = tenantId;
        this.clientEnvironmentId = clientEnvironmentId;
        this.environmentCertificate = environmentCertificate;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
        this.modifiedAt = new Timestamp(modifiedAt.getTime());
        this.modifiedBy = modifiedBy;
    }

    /**
     *  This method returns environmentId.
     * @return environmentId
     */
    public BigInteger getEnvironmentId() {
        return environmentId;
    }
    /**
     * This method sets the environmentId.
     * @param environmentId
     */
    public void setEnvironmentId(BigInteger environmentId) {
        this.environmentId = environmentId;
    }
    /**
     * This method returns environmentHostName.
     * @return environmentHostName
     */
    public String getEnvironmentHostName() {
        return environmentHostName;
    }
    /**
     *  This method sets environmentHostName.
     * @param environmentHostName
     */
    public void setEnvironmentHostName(String environmentHostName) {
        this.environmentHostName = environmentHostName;
    }
    /**
     *  This method returns environmentType.
     * @return environmentType
     */
    public String getEnvironmentType() {
        return environmentType;
    }
    /**
     *  This method sets environmentType.
     * @param environmentType
     */
    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }
    /**
     *  This method returns environmentName.
     * @return environmentName
     */
    public String getEnvironmentName() {
        return environmentName;
    }
    /**
     *  This method sets environmentName.
     * @param environmentName
     */
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
    /**
     * This method returns tenantId.
     * @return tenantId
     */
    public BigInteger getTenantId() {
        return tenantId;
    }
    /**
     * This method sets tenantId.
     * @param tenantId
     */
    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientEnvironmentId() {
        return clientEnvironmentId;
    }

    public void setClientEnvironmentId(String clientEnvironmentId) {
        this.clientEnvironmentId = clientEnvironmentId;
    }

    /**
     * This method returns environmentCertificate.
     * @return environmentCertificate
     */
    public String getEnvironmentCertificate() {
        return environmentCertificate;
    }
    /**
     * This method sets environmentCertificate.
     * @param environmentCertificate
     */
    public void setEnvironmentCertificate(String environmentCertificate) {
        this.environmentCertificate = environmentCertificate;
    }
    /**
     * This method returns createdAt.
     * @return createdAt
     */
    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }
    /**
     * This method sets createdAt.
     * @param createdAt
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Timestamp(createdAt.getTime());
    }
    /**
     * This method returns createdBy.
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }
    /**
     * This method sets createdBy.
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /**
     * This method returns updatedAt.
     * @return updatedAt
     */
    public Timestamp getModifiedAt() {
        return new Timestamp(modifiedAt.getTime());
    }
    /**
     * This method sets updatedAt.
     * @param updatedAt
     */
    public void setModifiedAt(Timestamp updatedAt) {
        this.modifiedAt = new Timestamp(updatedAt.getTime());
    }
    /**
     * This method returns updatedBy.
     * @return updatedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }
    /**
     * This method sets updatedBy.
     * @param updatedBy
     */
    public void setModifiedBy(String updatedBy) {
        this.modifiedBy = updatedBy;
    }
}
