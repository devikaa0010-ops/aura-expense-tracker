package com.example.expense_tracker_api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // Spring will look for SPRING_DATASOURCE_URL first, then DATABASE_URL (which Render native injects), then local H2 fallback
    @Value("${SPRING_DATASOURCE_URL:${DATABASE_URL:jdbc:h2:mem:expensedb}}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Bulletproof Fix: If the string accidentally starts with postgres://, forcefully convert it to jdbc:postgresql://
        String url = databaseUrl;
        if (url != null && url.startsWith("postgres://")) {
            url = url.replace("postgres://", "jdbc:postgresql://");
        }
        
        config.setJdbcUrl(url);
        config.setDriverClassName(url.contains("postgres") ? "org.postgresql.Driver" : "org.h2.Driver");
        
        // Hikari will automatically slice the username and password directly out of the jdbc URL string!
        return new HikariDataSource(config);
    }
}
