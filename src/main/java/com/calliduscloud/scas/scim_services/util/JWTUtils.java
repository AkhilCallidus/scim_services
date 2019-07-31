package com.calliduscloud.scas.scim_services.util;

import com.calliduscloud.scas.scim_services.exception.ClaimNotFoundException;
import com.calliduscloud.scas.scim_services.exception.JwtTokenEmptyException;
import com.calliduscloud.scas.scim_services.model.Tenant;
import com.calliduscloud.scas.scim_services.response.JWTResponse;
import com.calliduscloud.scas.scim_services.services.TenantService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtils {

    private static final String TYPE = "typ";
    private static final String JWT = "JWT";
    private static final String SAC_BACKEND = "sac_backend";
//    private static final String ISSUER = "sacservices.calliduscloud.com";
    private static final String ALGORITHM = "RSA";
    private static final String PROVIDER = "BC";
    private static final boolean SUCCESS = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtils.class);
    private static final long MAX_SCORE = 1000L;
    @Autowired
    private TenantService tenantService;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Value("${jwt.public.key}")
    private String publicKeyFilePath;

    @Value("${jwt.private.key}")
    private String privateKeyFilePath;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${issuer.name}")
    private String issuer;

    public String generateToken(Map<String, Object> claims, Date expiryTime, String userName) {
        String jwtBuilder = null;

        try {
            jwtBuilder = Jwts.builder()
                    .setClaims(claims)
                    .setHeaderParam(TYPE, JWT)
                    .setIssuedAt(new Date())
                    .setId(UUID.randomUUID().toString())
                    .setExpiration(expiryTime)
                    .setNotBefore(new Date())
                    .setAudience(SAC_BACKEND)
                    .setIssuer(issuer)
                    .signWith(generatePrivateKey())
                    .setSubject(userName)
                    .compact();
        } catch (Exception e) {
            LOGGER.error("Unable to create JWT token", e);
        }
        return jwtBuilder;
    }

    public String generateSampleIdpToken(Map<String, Object> claims, Date expiryTime,
                                         String userName, String clientId, String idpHost) {
        String jwtBuilder = null;

        try {
            jwtBuilder = Jwts.builder()
                    .setClaims(claims)
                    .setHeaderParam(TYPE, "IDP")
                    .setIssuedAt(new Date())
                    .setId(UUID.randomUUID().toString())
                    .setExpiration(expiryTime)
                    .setNotBefore(new Date())
                    .setAudience(clientId)
                    .setIssuer(idpHost)
                    .signWith(generatePrivateKey(), SignatureAlgorithm.RS256)
                    .setSubject(userName)
                    .compact();
        } catch (Exception e) {
            LOGGER.error("Unable to create JWT token", e);
        }
        return jwtBuilder;
    }

    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    public Map<String, Object> validateSacToken(String jwt) throws Exception {
        Claims claim = null;
        Map<String, Object> map = new HashMap<>();
        try {
            PublicKey pubKey = generatePublicKey();
            if (pubKey != null) {
                claim = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(jwt).getBody();
                if (claim != null) {
                    //map.put("loginUserName", claim.get("loginUserName"));
                    //map.put("environmentId", claim.get("environmentId"));
                    map.put("companyId", claim.get("companyId"));
                    //map.put("tenantId", claim.get("tenantId"));
                    map.put("userId", claim.get("loginUserName"));
//                  map.put("userId", claim.get("userId"));
                    map.put("issuedAt", claim.get("iat"));
                    //map.put("jti", claim.get("jti"));
                    map.put("expiresIn", claim.get("exp"));
                    //map.put("nbf", claim.get("nbf"));
                    //map.put("aud", claim.get("aud"));
                    //map.put("iss", claim.get("iss"));
                    //map.put("sub", claim.get("sub"));
                    map.put("issuedFor", claim.get("issuedFor"));
                    map.put("clientId", claim.get("clientId"));
//                    map.put("sacTenantId", claim.get("sacTenantId"));
//                    map.put("appType", claim.get("appType"));
                } else {
                    LOGGER.error(" validateToken : Colud not get the claim ");
                    throw new ClaimNotFoundException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                            ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                }
            } else {
                LOGGER.error("validateToken :: generatePublicKey return null");
            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature", ex);
            throw ex;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token", ex);
            throw ex;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token", ex);
            throw ex;
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token", ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty", ex);
            throw ex;
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Invalid Key found", e);
            throw e;
        }
        return map;
    }

    public Map<String, Object> validateToken(String jwt) throws Exception {
        Claims claim = null;
        Map<String, Object> map = new HashMap<>();
        try {
            PublicKey pubKey = generatePublicKey();
            if (pubKey != null) {
                claim = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(jwt).getBody();
                if (claim != null) {
                    map.put("loginUserName", claim.get("loginUserName"));
                    map.put("environmentId", claim.get("environmentId"));
                    map.put("companyId", claim.get("companyId"));
                    map.put("tenantId", claim.get("tenantId"));
                    map.put("userId", claim.get("userId"));
                    map.put("issuedAt", claim.get("iat"));
                    //map.put("jti", claim.get("jti"));
                    map.put("expiresIn", claim.get("exp"));
                    //map.put("nbf", claim.get("nbf"));
                    //map.put("aud", claim.get("aud"));
                    //map.put("iss", claim.get("iss"));
                    //map.put("sub", claim.get("sub"));
                    map.put("issuedFor", claim.get("issuedFor"));
                    map.put("sacTenantId", claim.get("sacTenantId"));
                    map.put("clientId", claim.get("clientId"));
                    map.put("appType", claim.get("appType"));
                } else {
                    LOGGER.error(" validateToken : Colud not get the claim ");
                    throw new ClaimNotFoundException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                            ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                }
            } else {
                LOGGER.error("validateToken :: generatePublicKey return null");
            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty");
            throw ex;
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Invalid Key found");
            throw e;
        }
        return map;
    }

    public Map<String, Object> validateOAuthToken(String jwt) throws Exception {
        Claims claim = null;
        Map<String, Object> map = new HashMap<>();
        try {
            PublicKey pubKey = generatePublicKey();
            if (pubKey != null) {
                claim = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(jwt).getBody();
                if (claim != null) {
                    //map.put("loginUserName", claim.get("loginUserName"));
                    //map.put("environmentId", claim.get("environmentId"));
                    map.put("companyId", claim.get("companyId"));
                    map.put("tenantId", claim.get("tenantId"));
                    map.put("userId", claim.get("loginUserName"));
//            map.put("userId", claim.get("userId"));
                    map.put("issuedAt", claim.get("iat"));
                    //map.put("jti", claim.get("jti"));
                    map.put("expiresIn", claim.get("exp"));
                    //map.put("nbf", claim.get("nbf"));
                    //map.put("aud", claim.get("aud"));
                    //map.put("iss", claim.get("iss"));
                    //map.put("sub", claim.get("sub"));
                    map.put("issuedFor", claim.get("issuedFor"));
                    map.put("clientId", claim.get("clientId"));
                } else {
                    LOGGER.error(" validateToken : Colud not get the claim ");
                    throw new ClaimNotFoundException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                            ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                }
            } else {
                LOGGER.error("validateToken :: generatePublicKey return null");
            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature", ex);
            throw ex;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token", ex);
            throw ex;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token", ex);
            throw ex;
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token", ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty", ex);
            throw ex;
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Invalid Key found", e);
            throw e;
        }
        return map;
    }
    public Map<String, Object> validateIdpToken(String jwt) throws Exception {
        Claims claim = null;
        Map<String, Object> map = new HashMap<>();
        try {
            PublicKey pubKey = generatePublicKey();
            if (pubKey != null) {
                claim = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(jwt).getBody();
                if (claim != null) {
                    BigInteger tenantId = getTenantIdFromJWT(jwt);
                    LOGGER.info("tenantId is " + tenantId);
                    Tenant tenant = tenantService.find(tenantId);
                    int exp = (int) claim.get("exp");
                    Date expireDate = new Date(exp * MAX_SCORE);
                    Date now = new Date();
                    if (claim.get("aud").equals(tenant.getScaiClientId())
                            && claim.get("iss").equals(tenant.getIdpHostName()) && !expireDate.before(now)) {
                        map.put("companyId", claim.get("companyId"));
                        map.put("userId", claim.get("loginUserName"));
                        map.put("expiresIn", claim.get("exp"));
                        map.put("issuedFor", claim.get("iss"));
                        map.put("clientId", claim.get("aud"));
                    } else {
                        throw new BadCredentialsException("Invalid claims...");
                    }
                } else {
                    LOGGER.error(" validateToken : Colud not get the claim ");
                    throw new ClaimNotFoundException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                            ResponseCodeConstants.JWT_CLIENT_ERROR_CODE);
                }
            } else {
                LOGGER.error("validateToken :: generatePublicKey return null");
            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature", ex);
            throw ex;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token", ex);
            throw ex;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token", ex);
            throw ex;
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token", ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty", ex);
            throw ex;
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Invalid Key found", e);
            throw e;
        }
        return map;
    }

    public JWTResponse validateOauthToken(String jwt) throws InvalidKeySpecException {
        JWTResponse jwtResponse = new JWTResponse();
        boolean flag = false;

        try {
            PublicKey pubKey = generatePublicKey();
            if (pubKey != null) {
                Jwts.parser().setSigningKey(generatePublicKey()).parseClaimsJws(jwt);
                flag = true;
            } else {
                LOGGER.error("validateToken :: generatePublicKey return null");

            }
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature", ex);
            throw ex;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token", ex);
            throw ex;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token", ex);
            throw ex;
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token", ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty", ex);
            throw ex;
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Invalid Key found", e);
            throw e;
        }
        if (flag) {
            jwtResponse.setSuccess(SUCCESS);
        } else {
            jwtResponse.setSuccess(flag);
        }

        return jwtResponse;
    }

    public BigInteger getTenantIdFromJWT(String jwt) throws JwtTokenEmptyException {
        Claims claims = null;
        LOGGER.info("Utils :: getTenantIdFromJWT :: entry");
        try {
            if (jwt != null && !jwt.isEmpty()) {
                jwt = jwt.replace("Bearer ", "");
                PublicKey pubKey = generatePublicKey();
                if (pubKey != null) {
                     claims = Jwts.parser()
                        .setSigningKey(pubKey)
                        .parseClaimsJws(jwt)    // JWT expired Exception
                        .getBody();
                } else {
                    LOGGER.error("getTenantIdFromJWT :: generatePublicKey() return null");
                }
            } else {
                LOGGER.error("jwt is not found in the request");
                throw new JwtTokenEmptyException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                        ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
            }
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Tenant id is not found ");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token");
            throw new JwtTokenEmptyException(ResponseCodeConstants.JWT_EMPTY_TOKEN_DESCRIPTION,
                    ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
        } catch (SignatureException ex) {
            LOGGER.error("JWT signature does not match locally computed signature. "
                    + "JWT validity cannot be asserted and should not be trusted");
            throw new JwtTokenEmptyException(ResponseCodeConstants.JWT_SIGNATURE_TOKEN_DESCRIPTION,
                    ResponseCodeConstants.JWT_EMPTY_TOKEN_CODE);
        }
        if (claims == null) {
            LOGGER.error("jwt is not found in the request");
            throw new JwtTokenEmptyException();
        }

        return new BigInteger("" + claims.get("tenantId"));
    }

    private PrivateKey generatePrivateKey() throws InvalidKeySpecException {

        KeyFactory factory = null;
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec privKeySpec = null;
        PemReader pemReader = null;
        try {
            factory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
            InputStream in1 = this.getClass().getClassLoader()
                    .getResourceAsStream(privateKeyFilePath);

            pemReader = new PemReader(new InputStreamReader(
                    in1, StandardCharsets.UTF_8));

            /*
            pemReader = new PemReader(new InputStreamReader(
                    new FileInputStream(privateKeyFilePath), StandardCharsets.UTF_8));
            */
            byte[] content = pemReader.readPemObject().getContent();
            privKeySpec = new PKCS8EncodedKeySpec(content);
            privateKey = factory.generatePrivate(privKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | FileNotFoundException e) {
            LOGGER.info("file not found, Provider not found, wrong keystore type " + privateKeyFilePath, e);
        } catch (IOException e) {
            LOGGER.info("Trying to read/write a file but don't have permission");
        } finally {
            if (null != pemReader) {
                try {
                    pemReader.close();
                } catch (IOException e) {
                    LOGGER.info("Unable to read a Private PEM Certificate ", e);
                }
            }
        }
        return privateKey;
    }

    private PublicKey generatePublicKey() throws InvalidKeySpecException {

        KeyFactory factory = null;
        X509EncodedKeySpec pubKeySpec = null;
        PemReader pemReader = null;
        PublicKey publicKey = null;
        try {
            factory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
            InputStream in = this.getClass().getClassLoader()
                    .getResourceAsStream(publicKeyFilePath);
            LOGGER.info("Publickey file path :: " + publicKeyFilePath);
            pemReader = new PemReader(new InputStreamReader(
                    in, StandardCharsets.UTF_8));
            LOGGER.info("Inpoutstream  :: " + in);
            /*
            pemReader = new PemReader(new InputStreamReader(
                    new FileInputStream(publicKeyFilePath), StandardCharsets.UTF_8));
            */
            byte[] content = pemReader.readPemObject().getContent();
            pubKeySpec = new X509EncodedKeySpec(content);
            publicKey = factory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | FileNotFoundException e) {
            LOGGER.info("file not found, Provider not found, wrong keystore type ", e);
        } catch (IOException e) {
            LOGGER.info("Trying to read/write a file but don't have permission", e);
        } finally {
            if (null != pemReader) {
                try {
                    pemReader.close();
                } catch (IOException e) {
                    LOGGER.info("Unable to read a Public PEM Certificate ", e);
                }
            }
        }
        return publicKey;
    }
}
