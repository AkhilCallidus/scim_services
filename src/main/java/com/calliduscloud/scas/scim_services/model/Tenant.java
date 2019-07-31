package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "SC_TENANT")
public class Tenant implements Serializable {
    private static final int TENANT_NAME_LENGTH = 500;
    private static final int IDP_HOST_NAME_LENGTH = 50;
    private static final int IDP_CERTIFICATE_LENGTH = 500;
    private static final int OAUTH_SERVER_URL_LENGTH = 500;
    private static final int OAUTH_CERTIFICATE_LENGTH = 500;
    private static final int SCAI_CLIENT_ID_LENGTH = 100;
    private static final int SCAI_CLIENT_SECRET_LENGTH = 100;
    private static final int IPS_CLIENT_ID_LENGTH = 100;
    private static final int IPS_CLIENT_SECRET_LENGTH = 100;
    private static final int CREATED_BY_LENGTH = 255;
    private static final int OAUTH_CLIENT_ID_LENGTH = 100;
    private static final int OAUTH_CLIENT_SECRET_LENGTH = 100;
    private static final int SAC_CLIENT_ID_LENGTH = 100;
    private static final int SAC_CLIENT_SECRET_LENGTH = 100;

    @Column(name = "TENANT_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger tenantId;

    @Column(length = TENANT_NAME_LENGTH, name = "TENANT_NAME")
    private String tenantName;

    @Column(length = IDP_HOST_NAME_LENGTH, name = "IDP_HOST_NAME")
    private String idpHostName;

    @Column(length = IDP_CERTIFICATE_LENGTH, name = "IDP_CERTIFICATE")
    private String idpCertificate;

    @Column(length = OAUTH_SERVER_URL_LENGTH, name = "OAUTH_SERVER_URL")
    private String oauthServerUrl;

    @Column(length = OAUTH_CERTIFICATE_LENGTH, name = "OAUTH_CERTIFICATE")
    private String oauthCertificate;

    @Column(length = SCAI_CLIENT_ID_LENGTH, name = "SCAI_CLIENT_ID")
    private String scaiClientId;

    @Column(length = SCAI_CLIENT_SECRET_LENGTH, name = "SCAI_CLIENT_SECRET")
    private String scaiClientSecret;

    @Column(length = SAC_CLIENT_ID_LENGTH, name = "SAC_CLIENT_ID")
    private String sacClientId;

    @Column(length = SAC_CLIENT_SECRET_LENGTH, name = "SAC_CLIENT_SECRET")
    private String sacClientSecret;

    @Column(length = IPS_CLIENT_ID_LENGTH, name = "IPS_CLIENT_ID")
    private String ipsClientId;

    @Column(length = IPS_CLIENT_SECRET_LENGTH, name = "IPS_CLIENT_SECRET")
    private String ipsClientSecret;

    @Column(length = OAUTH_CLIENT_ID_LENGTH, name = "OAUTH_CLIENT_ID")
    private String oauthClientId;

    @Column(length = OAUTH_CLIENT_SECRET_LENGTH, name = "OAUTH_CLIENT_SECRET")
    private String oauthClientSecret;

    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;

    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFY_DATE")
    private Timestamp updatedAt;

    @Column(length = CREATED_BY_LENGTH, name = "MODIFIED_BY")
    private String updatedBy;

    public Tenant() {
    }

    public Tenant(BigInteger tenantId, String tenantName, String idpHostName,
                  String idpCertificate, String oauthServerUrl,
                  String oauthCertificate, String sacClientId, String sacClientSecret,
                  String scaiClientId, String scaiClientSecret, String ipsClientId,
                  String ipsClientSecret, String oauthClientId, String oauthClientSecret,
                  Timestamp createdAt, String createdBy, Timestamp updatedAt,
                  String updatedBy) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.idpHostName = idpHostName;
        this.idpCertificate = idpCertificate;
        this.oauthServerUrl = oauthServerUrl;
        this.oauthCertificate = oauthCertificate;
        this.sacClientId = sacClientId;
        this.sacClientSecret = sacClientSecret;
        this.scaiClientId = scaiClientId;
        this.scaiClientSecret = scaiClientSecret;
        this.oauthClientId = oauthClientId;
        this.oauthClientSecret = oauthClientSecret;
        this.ipsClientId = ipsClientId;
        this.ipsClientSecret = ipsClientSecret;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
        this.updatedAt = new Timestamp(updatedAt.getTime());
        this.updatedBy = updatedBy;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getIdpHostName() {
        return idpHostName;
    }

    public void setIdpHostName(String idpHostName) {
        this.idpHostName = idpHostName;
    }

    public String getIdpCertificate() {
        return idpCertificate;
    }

    public void setIdpCertificate(String idpCertificate) {
        this.idpCertificate = idpCertificate;
    }

    public String getOauthServerUrl() {
        return oauthServerUrl;
    }

    public void setOauthServerUrl(String oauthServerUrl) {
        this.oauthServerUrl = oauthServerUrl;
    }

    public String getSacClientId() {
        return sacClientId;
    }

    public void setSacClientId(String sacClientId) {
        this.sacClientId = sacClientId;
    }

    public String getSacClientSecret() {
        return sacClientSecret;
    }

    public void setSacClientSecret(String sacClientSecret) {
        this.sacClientSecret = sacClientSecret;
    }

    public String getOauthCertificate() {
        return oauthCertificate;
    }

    public void setOauthCertificate(String oauthCertificate) {
        this.oauthCertificate = oauthCertificate;
    }

    public String getScaiClientId() {
        return scaiClientId;
    }

    public void setScaiClientId(String scaiClientId) {
        this.scaiClientId = scaiClientId;
    }

    public String getScaiClientSecret() {
        return scaiClientSecret;
    }

    public void setScaiClientSecret(String scaiClientSecret) {
        this.scaiClientSecret = scaiClientSecret;
    }

    public String getIpsClientId() {
        return ipsClientId;
    }

    public void setIpsClientId(String ipsClientId) {
        this.ipsClientId = ipsClientId;
    }

    public String getIpsClientSecret() {
        return ipsClientSecret;
    }

    public void setIpsClientSecret(String ipsClientSecret) {
        this.ipsClientSecret = ipsClientSecret;
    }

    public String getOauthClientId() {
        return oauthClientId;
    }

    public void setOauthClientId(String oauthClientId) {
        this.oauthClientId = oauthClientId;
    }

    public String getOauthClientSecret() {
        return oauthClientSecret;
    }

    public void setOauthClientSecret(String oauthClientSecret) {
        this.oauthClientSecret = oauthClientSecret;
    }

    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Timestamp(createdAt.getTime());
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
}