package com.example.expense_tracker_api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Value("${SPRING_DATASOURCE_URL:${DATABASE_URL:jdbc:h2:mem:expensedb}}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        HikariConfig config = new HikariConfig();
        
        String url = databaseUrl;
        
        // If the URL contains credentials inline (which causes PostgreSQL JDBC Driver to crash), we parse them out cleanly!
        if (url != null && (url.startsWith("postgres://") || url.contains("@"))) {
            String cleanUrl = url.replace("jdbc:", ""); // Strip 'jdbc:' out temporarily so standard java.net.URI can parse it 
            URI uri = new URI(cleanUrl);
            
            String username = uri.getUserInfo().split(":")[0];
            String password = uri.getUserInfo().split(":")[1];
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "") + uri.getPath();
            
            // Set the clean URL and isolated credentials separately 
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
        } else {
            // Standard fallback (H2 local or pre-cleaned JDBC string)
            config.setJdbcUrl(url);
            if (url != null) {
                config.setDriverClassName(url.contains("postgres") ? "org.postgresql.Driver" : "org.h2.Driver");
            }
        }
        
        return new HikariDataSource(config);
    }
}
