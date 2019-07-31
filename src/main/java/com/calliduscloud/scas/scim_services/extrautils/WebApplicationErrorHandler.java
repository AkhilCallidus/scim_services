package com.calliduscloud.scas.scim_services.extrautils;


import com.calliduscloud.scas.scim_services.util.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This is an error handler that will be used across the
 * thunderbridge platform including SCAN and SCAI applications.
 */
public class WebApplicationErrorHandler extends RuntimeException {
    private static final Logger LOG = LoggerFactory
            .getLogger(WebApplicationErrorHandler.class);
    private static Properties errorCodes;
    static {
        List<String> errorFiles = new ArrayList<String>();
        errorFiles.add("error.properties");
        errorCodes = PropertiesLoadHelper.loadPropertyFiles(errorFiles);
    }

    /**
     * This method will help in raising the exceptions from the web application
     *  Typically the errorcode will look like this..
     *  MODULE_HTTPSTATUSCODE_ERRORCODE
     *  For ex
     *  USER_400_001: Bad data. Please check it.
     *  USER_400_002: Bad user data.. Check the password
     *  USER_401_001: User is not authenticated..
     *  USER_403_001
     *  USER_404_001
     *  USER_500_001
     *
     *  GROUP_400_001
     *  GROUP_401_001
     *  GROUP_403_001
     *  GROUP_404_001
     *  GROUP_500_001
     *
     *
     *  AI_400_001
     *  AI_401_001
     *  AI_403_001
     *  AI_404_001
     *  AI_500_001
     *
     *  TENANT_404_001: TenantConfig not found
     *  TENANT_404_002: TenantConfig not found
     *  TENANT_500_001: Server Error
     *  TENANT_500_002: Server Error 2
     *  In the above error codes, we will have USER_400_001: This means \
     *  User Services has 400 status code issue and the error code is 001.
     * @param errorCode THis is the error code
     */
    public static final void raiseError(final String errorCode) {
        HttpServletResponse response = AuthenticationFilter.getResponse();
        try {
            StringTokenizer st = new StringTokenizer(errorCode, "_");
            List<String> tokens = new ArrayList<>();
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
            /*

            response.setStatus(Integer.parseInt(tokens.get(1)));
            response.setContentType("application/json");
            StringBuffer sb = new StringBuffer("");
            sb.append("{error_code:" + errorCode + " ,message:" + errorCodes.get(errorCode) + "}");
            response.getWriter().write(sb.toString());
            response.flushBuffer();

            */
            response.sendError(
                    Integer.parseInt(tokens.get(1)),
                    "errorCode:" + errorCode + ", "
                            + "message:" + errorCodes.get(errorCode));
            return;
        } catch (Exception e) {
            try {
                /*
                response.setContentType("application/json");
                StringBuffer sb = new StringBuffer("");
                sb.append("{error_code:" + errorCode + " ,message:" + errorCodes.get(errorCode) + "}");
                response.getWriter().write(sb.toString());
                response.flushBuffer();
                */
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "errorCode:" + errorCode + ", "
                                + "message:" + errorCodes.get(errorCode));

                return;
            } catch (Exception e1) {
                LOG.error("Error", e1);

            } finally {
                LOG.debug("Error");
            }
        }

    }

    /**
     * Other way of handing error codes.
     * @param errorCode This is the error code
     * @return ResponseEntity
     */
    public static final ResponseEntity<String> raiseError1(
            final String errorCode) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("", "");
        StringBuffer sb = new StringBuffer("");
        sb.append("error_code:" + errorCode).append("error_message:"
                + errorCodes.get(errorCode));

        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                .headers(responseHeaders)
                .body(sb.toString());
    }
}
