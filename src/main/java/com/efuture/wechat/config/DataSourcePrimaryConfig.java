package com.efuture.wechat.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @title: DataSourcePrimaryConfig
 * @description: 数据源配置
 * @author: wangf
 * @date: 2020/07/13
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef="entityManagerFactoryPrimary", transactionManagerRef="transactionManagerPrimary", basePackages= {"com.efuture.wechat.db.repository"})
public class DataSourcePrimaryConfig {

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private Integer maximumPoolSize;

    @Value("${spring.datasource.primary.jdbc-url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryPassword;

    /**
     * 主库数据源配置
     * @return
     */
    @Primary
    @Bean(name = "dataSourcePrimary")
    public DataSource dataSourcePrimary()
    {
        HikariDataSource dataSourcePrimary = new HikariDataSource();
        dataSourcePrimary.setDriverClassName(driverClassName);
        dataSourcePrimary.setJdbcUrl(primaryUrl);
        dataSourcePrimary.setUsername(primaryUsername);
        dataSourcePrimary.setPassword(primaryPassword);
        dataSourcePrimary.setMaximumPoolSize(maximumPoolSize);

        return dataSourcePrimary;
    }

    /**
     * 主库jpa 实例管理器工厂配置
     */
    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder)
    {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(dataSourcePrimary())
                .packages("com.efuture.wechat.db.model")
                .build();
        Properties properties = new Properties();
        properties.setProperty("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        em.setJpaProperties(properties);
        return em;
    }

    /**
     * 主库事务管理器配置
     */
    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder)
    {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactoryPrimary(builder).getObject());
        return txManager;
    }
}
