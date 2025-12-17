package com.sentries.SentinelX.app_server.config;

import com.sentries.SentinelX.secret.SecretConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final SecretConfig secretConfig;

    /**
     * Configure HikariCP DataSource using SecretConfig database properties
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(secretConfig.getDatasourceUrl());
        hikariConfig.setUsername(secretConfig.getDatasourceUsername());
        hikariConfig.setPassword(secretConfig.getDatasourcePassword());
        hikariConfig.setDriverClassName(secretConfig.getDriverClassName());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);

        return new HikariDataSource(hikariConfig);
    }
}
