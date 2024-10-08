package com.dev.usersmanagementsystem.config;

import com.dev.usersmanagementsystem.service.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        // Set up dynamic data source
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        Map<Object, Object> dataSources = new HashMap<>();

        // Add default data source (for user metadata)
        DataSource defaultDataSource = defaultDataSource();
        dataSources.put("default", defaultDataSource);

        // You can add more user-specific data sources dynamically
        dynamicDataSource.setTargetDataSources(dataSources);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);

        return dynamicDataSource;
    }

    @Bean
    public DynamicDataSource dynamicDataSource() {
        return new DynamicDataSource();
    }

    @Bean
    public DataSource defaultDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/users_management");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }
}

