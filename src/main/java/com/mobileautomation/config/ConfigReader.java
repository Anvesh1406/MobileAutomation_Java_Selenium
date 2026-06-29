package com.mobileautomation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config/config.properties";

    private ConfigReader() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            log.info("Configuration loaded from: {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage(), e);
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties");
        }
        return value.trim();
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
