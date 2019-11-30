package com.allya.kerilia.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigParser {
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String LOADING_PROPERTIES = "Loading properties from: {}";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);

    private ConfigParser() {
        // utility class
    }

    public static Properties getProperties(final String appName) {
        return getProperties(getConfigurationFiles(appName));
    }

    private static Properties getProperties(final String... configFiles) {
        final Properties properties = new Properties();

        Stream.of(configFiles).forEach(path -> {
            if (path != null) {
                if (path.startsWith(CLASSPATH_PREFIX)) {
                    loadResource(properties, path);
                }
                else {
                    loadFile(properties, path);
                }
            }
        });

        Collections
            .list(properties.propertyNames())
            .forEach(name -> properties.setProperty(
                name.toString(),
                System.getProperty(name.toString(), properties.getProperty(name.toString()))));

        return properties;
    }

    private static String[] getConfigurationFiles(final String appName) {
        Validate.notBlank(appName, "appName cannot be blank");

        final String defaultConfig = CLASSPATH_PREFIX + appName + "-default.properties";
        final String overrideConfig = CLASSPATH_PREFIX + appName + "-override.properties";

        final String envConfigName = appName.toUpperCase().replace('-', '_') + "_CONF";
        final String sysConfigValue = System.getProperty(envConfigName);
        final String localConfig = StringUtils.isNotBlank(sysConfigValue)
                                   ? sysConfigValue
                                   : System.getenv(envConfigName);

        return StringUtils.isNotBlank(localConfig)
               ? new String[]{defaultConfig, overrideConfig, localConfig}
               : new String[]{defaultConfig, overrideConfig};
    }

    private static void loadFile(final Properties properties, final String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        if (!file.canRead()) {
            throw new IllegalStateException("Properties file " + filePath + " exists but is not readable");
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            LOGGER.info(LOADING_PROPERTIES, filePath);
            properties.load(reader);
        }
        catch (final IOException ioe) {
            throw new IllegalStateException("Failed to load properties from: " + filePath);
        }
    }

    private static void loadResource(final Properties properties, final String resource) {
        try (
            InputStream resourceStream = ConfigParser.class
                .getClassLoader()
                .getResourceAsStream(resource.substring(CLASSPATH_PREFIX.length()));) {
            if (resourceStream != null) {
                LOGGER.info(LOADING_PROPERTIES, resource);
                properties.load(resourceStream);
            }
        }
        catch (final IOException ioe) {
            throw new IllegalStateException("Failed to load properties from classpath: " + resource, ioe);
        }
    }
}
