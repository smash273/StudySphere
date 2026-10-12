package com.studysphere.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration holder for database connection properties with environment variable override.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded configuration from application.properties");
            } else {
                logger.warn("application.properties not found on classpath, using defaults");
            }
        } catch (Exception e) {
            logger.error("Failed to load application.properties: {}", e.getMessage());
        }
    }

    public static String getDbUrl() {
        String env = System.getenv("DB_URL");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/studysphere");
    }

    public static String getDbUser() {
        String env = System.getenv("DB_USER");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("db.user", "postgres");
    }

    public static String getDbPassword() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null) return env;
        return properties.getProperty("db.password", "postgres");
    }

    public static int getPoolSize() {
        String env = System.getenv("DB_POOL_SIZE");
        if (env != null && !env.isBlank()) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(properties.getProperty("db.pool.size", "10"));
    }

    public static String getAiProvider() {
        String env = System.getenv("AI_PROVIDER");
        if (env != null && !env.isBlank()) return env.toLowerCase();
        return properties.getProperty("ai.provider", "fallback").toLowerCase();
    }

    public static String getAiApiKey() {
        String env = System.getenv("AI_API_KEY");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("ai.api.key", "");
    }

    public static String getAiEndpoint() {
        String env = System.getenv("AI_ENDPOINT");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("ai.endpoint", "https://api.openai.com/v1/chat/completions");
    }

    public static String getAiModel() {
        String env = System.getenv("AI_MODEL_NAME");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("ai.model", "gpt-4o-mini");
    }

    public static String getUploadDir() {
        String env = System.getenv("UPLOAD_DIR");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("upload.directory", "./uploads");
    }

    public static long getMaxUploadSizeBytes() {
        String env = System.getenv("UPLOAD_MAX_SIZE_MB");
        int mb = 25;
        if (env != null && !env.isBlank()) {
            try { mb = Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        } else {
            try { mb = Integer.parseInt(properties.getProperty("upload.max.size.mb", "25")); } catch (NumberFormatException ignored) {}
        }
        return mb * 1024L * 1024L;
    }

    public static int getThreadPoolSize() {
        String env = System.getenv("THREAD_POOL_SIZE");
        if (env != null && !env.isBlank()) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(properties.getProperty("concurrency.worker.threads", "4"));
    }
}
