package com.dev.usersmanagementsystem.service;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> dynamicDataSources = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        // Determines which database to use for the current user.
        return UserContext.getCurrentUserDatabase();
    }

    @Override
    public void afterPropertiesSet() {
        // Set dynamic data sources.
        super.setTargetDataSources(dynamicDataSources);
        super.afterPropertiesSet();
    }

    // Dynamically add a data source for a new user
    public void addDataSource(String dbName, DataSource dataSource) {
        dynamicDataSources.put(dbName, dataSource);
        // Update the target data sources and reinitialize
        super.setTargetDataSources(dynamicDataSources);
        super.afterPropertiesSet();
    }
}

