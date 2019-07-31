package com.calliduscloud.scas.scim_services.extrautils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * This class helps in loading multiple properties files
 * and return the Property object for calling parties.
 */
public class PropertiesLoadHelper {
    private static final Logger LOG = LoggerFactory.getLogger(
            PropertiesLoadHelper.class);

    /**
     * loadPropertyFiles will load the properties from multiple property files.
     * @param propertyFiles the property files to be loaded.
     * @return Properties object
     */
    public static Properties loadPropertyFiles(
            final List<String> propertyFiles) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        for (String configFile : propertyFiles) {
            try {
                inputStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(configFile);
                if (inputStream != null) {
                    properties.load(inputStream);
                }
            } catch (Exception e) {
                LOG.debug(" Missing properties file: " + configFile, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return properties;
    }
}
