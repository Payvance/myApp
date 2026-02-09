package com.payvance.erp_saas.erp.db.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.payvance.erp_saas.erp.repository", entityManagerFactoryRef = "erpEntityManagerFactory", transactionManagerRef = "erpTransactionManager")
public class ErpDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.erp")
    public HikariDataSource erpDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean erpEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("erpDataSource") HikariDataSource dataSource) {
        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        jpaProps.put("hibernate.show_sql", true);
        jpaProps.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("com.payvance.erp_saas.erp.entity")
                .persistenceUnit("erp")
                .properties(jpaProps)
                .build();
    }

    @Bean
    public PlatformTransactionManager erpTransactionManager(
            @Qualifier("erpEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
