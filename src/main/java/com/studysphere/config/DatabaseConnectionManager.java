package com.studysphere.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Singleton database connection pool manager using HikariCP.
 * Provides schema auto-initialization from SQL scripts and connection lifecycle management.
 */
public class DatabaseConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionManager.class);
    private static volatile DatabaseConnectionManager instance;
    private HikariDataSource dataSource;
    private boolean initialized = false;

    private DatabaseConnectionManager() {
        initDataSource();
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    private void initDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DatabaseConfig.getDbUrl());
            config.setUsername(DatabaseConfig.getDbUser());
            config.setPassword(DatabaseConfig.getDbPassword());
            config.setMaximumPoolSize(DatabaseConfig.getPoolSize());
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setDriverClassName("org.postgresql.Driver");

            // Pool verification
            this.dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully for URL: {}", DatabaseConfig.getDbUrl());

            // Run database.sql schema initialization
            initializeSchema();
            initialized = true;
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool: {}", e.getMessage());
            // Do not crash the application, allow reconnection attempts
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        if (dataSource == null) {
            throw new SQLException("Database connection pool is not available. Please verify PostgreSQL is running.");
        }
        return dataSource.getConnection();
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.warn("Database connectivity test failed: {}", e.getMessage());
            return false;
        }
    }

    public void initializeSchema() {
        executeScript("database.sql");
    }

    public void seedSampleData() {
        executeScript("sample-data.sql");
    }

    public void executeScript(String scriptName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(scriptName)) {
            if (is == null) {
                logger.warn("SQL script {} not found on classpath", scriptName);
                return;
            }
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                // Execute script commands
                stmt.execute(sql);
                logger.info("Successfully executed SQL script: {}", scriptName);
            }
        } catch (Exception e) {
            logger.warn("Notice during execution of {}: {}", scriptName, e.getMessage());
        }
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed.");
        }
    }
}
