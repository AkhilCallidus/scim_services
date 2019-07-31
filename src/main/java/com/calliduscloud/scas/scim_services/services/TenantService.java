package com.calliduscloud.scas.scim_services.services;

//import com.calliduscloud.commons.encoder.Encoder;
import com.calliduscloud.scas.scim_services.model.Environment;
import com.calliduscloud.scas.scim_services.model.Tenant;
import com.calliduscloud.scas.scim_services.dao.EnvironmentDao;
import com.calliduscloud.scas.scim_services.dao.TenantDao;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.persistence.PersistenceException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TenantService will handle all tenant related operations i,e create,
 * update and delete and find the Tenant.
 */
@Service
public class TenantService {

    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String REQ_PARAM_DELIMITOR = "&";
    public static final String EQUALS = "=";
    public static final String CLIENT_ID = "client_id";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String OPENID = "openid";
    public static final String STATE = "state";
    public static final String MYRELAYSTATE = "myrelaystate";
    public static final String CODE = "code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic ";

    private static final String REDIRECT_URL = "http://abc01.sac.sap.com:8080/thunderbridge_ai/login";

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private EnvironmentDao environmentDao;

    private Tenant tenant;

    private static final Logger LOG =
            LoggerFactory.getLogger(TenantService.class);

    /**
     * this method is used to find the Tenant for given tenantId.
     *
     * @param tenantId Tenant Id.
     * @return Tenant for given tenantId.
     */
    public Tenant find(BigInteger tenantId) {
        LOG.info("tenantId: " + tenantId);
        return tenantDao.findByTenantId(tenantId);
    }

    /**
     * this method is used to get all the Tenants.
     *
     * @return Tenant for given tenantId.
     */
    public List<Tenant> findAll() {
        return tenantDao.findAll();
    }

    /**
     * this method is used to create the tenant for given parameter values.
     *
     * @param params tenant parameters.
     * @return creates the tenant with given params.
     */


    /**
     * this method is used to update the tenant.
     *
     * @param tenantId tenant Id.
     * @param params   tenant parameters which needs to update.
     * @return updated Tenant with params.
     */


    /**
     * this method is used to delete the Tenant.
     *
     * @param tenantId Tenant Id.
     * @return success message.
     */
    public String delete(BigInteger tenantId) throws NotFoundException {
        LOG.info("tenantId: " + tenantId);
        Tenant t = tenantDao.findByTenantId(tenantId);
        if (null != t) {
            tenantDao.delete(t);
            return "Tenant Successfully Deleted";
        } else {
            LOG.info("Tenant not found for the tenantId: " + tenantId);
            throw new NotFoundException("Tenant not found");
        }
    }



    public String getAuthURL(String envHostName) {
        Environment environment = environmentDao.findByEnvironmentHostName(envHostName);
        LOG.info("environment.getTenant() : " + environment.getTenantId());
        if (tenant == null) {
            Optional<Tenant> tenantOptional = tenantDao.findById(environment.getTenantId());
            tenant = tenantOptional.get();
        }
        String baseGsUrl = "https://" + tenant.getIdpHostName() + "/oauth2/authorize";
        StringBuffer url = new StringBuffer(baseGsUrl);
        url.append(REQ_PARAM_DELIMITOR);
        url.append(CLIENT_ID);
        url.append(EQUALS);
        url.append(tenant.getScaiClientId());
        url.append(REQ_PARAM_DELIMITOR);
        url.append(REDIRECT_URI);
        url.append(EQUALS);
        url.append(REDIRECT_URL);
        url.append(REQ_PARAM_DELIMITOR);
        url.append(SCOPE);
        url.append(EQUALS);
        url.append(OPENID);
        url.append(REQ_PARAM_DELIMITOR);
        url.append(STATE);
        url.append(EQUALS);
        url.append(MYRELAYSTATE);
        return url.toString();
    }






    public TenantDao getTenantDao() {
        return tenantDao;
    }

    public void setTenantDao(TenantDao tenantDao) {
        this.tenantDao = tenantDao;
    }
}
