package com.calliduscloud.scas.scim_services.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

//import javax.persistence.JoinColumn;

/*
 * Database schema for {@link SC_ENV_SCAN_CONFIG} object
 */
@Entity
@Table(name = "SC_ENV_SCAN_CONFIG")
public class TenantConfig implements Serializable {
    private static final int APP_TYPE_LENGTH = 20;
    private static final int DB_HOST_NAME_LENGTH = 255;
    private static final int DB_USER_NAME_LENGTH = 100;
    private static final int DB_PASSWORD_LENGTH = 100;
    private static final int CREATED_BY_LENGTH = 255;
    @Column(name = "ENVIRONMENT_ID")
    @Id
    private BigInteger environmentId;
    @OneToOne(mappedBy = "scanEnvironment")
    Environment environment;
    @Column(length = APP_TYPE_LENGTH, name = "APP_TYPE")
    private String appType;
    @Column(length = DB_HOST_NAME_LENGTH, name = "DB_HOST_NAME")
    private String hostName;
    @Column(length = DB_USER_NAME_LENGTH, name = "SQL_PORT")
    private String sqlPort;
    @Column(length = DB_USER_NAME_LENGTH, name = "DB_USER_NAME")
    private String dbUsername;
    @Column(length = DB_PASSWORD_LENGTH, name = "DB_PASSWORD")
    private String dbPassword;
    @Column(length = CREATED_BY_LENGTH, name = "DB_CONN_URL")
    private String dbconnectionUrl;
    @Column(length = DB_USER_NAME_LENGTH, name = "DB_POOL_ID")
    private String dbPoolId;
    @Column(length = DB_USER_NAME_LENGTH, name = "DB_HTTP_PORT")
    private String httpPort;
    @Column(length = DB_USER_NAME_LENGTH, name = "DB_HTTPS_PORT")
    private String httpsPort;
    @Column(length = CREATED_BY_LENGTH, name = "HTTP_CONN_URL")
    private String httpConnectionUrl;
    @Column(length = DB_USER_NAME_LENGTH, name = "SCHEMA_NAME")
    private String schemaMap;
    @Column (length = CREATED_BY_LENGTH, name = "REPO_USER_NAME")
    private String repoUsername;
    @Column(length = DB_USER_NAME_LENGTH, name = "REPO_PASSWORD")
    private String repoPassword;
    @Column(name = "CREATE_DATE")
    private Timestamp createdAt;
    @Column(length = CREATED_BY_LENGTH, name = "CREATED_BY")
    private String createdBy;
    @Column(name = "MODIFY_DATE")
    private Timestamp updatedAt;
    @Column(length = CREATED_BY_LENGTH, name = "MODIFIED_BY")
    private String updatedBy;

    /**
     * default constructor.
     */
    public TenantConfig() {
    }

    /**
     * This method is used to construct tenant config obj.
     * @param environmentId
     * @param appType
     * @param hostName
     * @param sqlPort
     * @param dbUsername
     * @param dbPassword
     * @param dbconnectionUrl
     * @param dbPoolId
     * @param httpPort
     * @param httpsPort
     * @param httpConnectionUrl
     * @param schemaMap
     * @param repoUsername
     * @param repoPassword
     * @param createdAt
     * @param createdBy
     * @param updatedAt
     * @param updatedBy
     */
     public TenantConfig(BigInteger environmentId, String appType, String hostName, String sqlPort, String dbUsername,
                         String dbPassword, String dbconnectionUrl, String dbPoolId, String httpPort, String httpsPort,
                         String httpConnectionUrl, String schemaMap, String repoUsername, String repoPassword, Timestamp createdAt,
                         String createdBy, Timestamp updatedAt, String updatedBy) {
        this.environmentId = environmentId;
        this.appType = appType;
        this.hostName = hostName;
        this.sqlPort = sqlPort;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbconnectionUrl = dbconnectionUrl;
        this.dbPoolId = dbPoolId;
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
        this.httpConnectionUrl = httpConnectionUrl;
        this.schemaMap = schemaMap;
        this.repoUsername = repoUsername;
        this.repoPassword = repoPassword;
        this.createdAt = new Timestamp(createdAt.getTime());
        this.createdBy = createdBy;
        this.updatedAt = new Timestamp(updatedAt.getTime());
        this.updatedBy = updatedBy;
     }

    /**
     * This method returns environmentId.
     * @return environmentId
     */
    public BigInteger getEnvironmentId() {
       return environmentId;
    }

    /**
     * This method sets environmentId.
     * @param environmentId
     */
    public void setEnvironmentId(BigInteger environmentId) {
        this.environmentId = environmentId;
    }

    /**
     * This method returns appType.
     * @return appType
     */
    public String getAppType() {
        return appType;
    }

    /**
     * This method sets appType.
     * @param appType
     */
    public void setAppType(String appType) {
        this.appType = appType;
    }

    /**
     * This method returns HostName.
     * @return dbHostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * This method sets HostName.
     * @param hostName
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * This method returns dbPortName.
     * @return dbPortName
     */
    public String getSqlPort() {
        return sqlPort;
    }

    /**
     * This method sets dbPortName.
     * @param sqlPort
     */
    public void setSqlPort(String sqlPort) {
        this.sqlPort = sqlPort;
    }

    /**
     * This method returns dbUsername.
     * @return dbUserName
     */
    public String getDbUsername() {
        return dbUsername;
    }

    /**
     * This method sets dbUserName.
     * @param dbUsername
     */
    public void setDbUserName(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    /**
     * This method returns dbPassword.
     * @return dbPassword
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * This method sets dbPassword.
     * @param dbPassword
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /**
     * This method returns dbHttpPort.
     * @return dbHttpPort
     */
    public String getHttpPort() {
        return httpPort;
    }

    /**
     * This method sets dbHttpPort.
     * @param httpPort
     */
    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * This method returns dbHttpsPort.
     * @return dbHttpsPort
     */
    public String getHttpsPort() {
        return httpsPort;
    }

    /**
     * This method sets dbHttpsPort.
     * @param httpsPort
     */
    public void setHttpsPort(String httpsPort) {
        this.httpsPort = httpsPort;
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
    public Timestamp getUpdatedAt() {
        return new Timestamp(updatedAt.getTime());
    }

    /**
     * This method returns updatedAt.
     * @param updatedAt
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = new Timestamp(updatedAt.getTime());
    }

    /**
     * This method returns updatedBy.
     * @return updatedBy
     */
    public String getUpdatedBy() {
       return updatedBy;
    }

    /**
     * This method sets updatedBy.
     * @param updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
       this.updatedBy = updatedBy;
    }


    public String getDbPoolId() {
       return dbPoolId;
    }

    public void setDbPoolId(String dbPoolId) {
       this.dbPoolId = dbPoolId;
    }

    public String getSchemaMap() {
       return schemaMap;
    }

    public void setSchema(String schemaMap) {
       this.schemaMap = schemaMap;
    }

    public String getRepoUsername() {
       return repoUsername;
    }

    public void setRepoUsername(String repoUsername) {
       this.repoUsername = repoUsername;
    }

    public String getRepoPassword() {
       return repoPassword;
    }

    public void setRepoPassword(String repoPassword) {
       this.repoPassword = repoPassword;
    }

    public String getDbconnectionUrl() {
       return dbconnectionUrl;
    }

    public void setDbconnectionUrl(String dbconnectionUrl) {
       this.dbconnectionUrl = dbconnectionUrl;
    }

    public String getHttpConnectionUrl() {
       return httpConnectionUrl;
    }

    public void setHttpConnectionUrl(String httpConnectionUrl) {
       this.httpConnectionUrl = httpConnectionUrl;
    }
}