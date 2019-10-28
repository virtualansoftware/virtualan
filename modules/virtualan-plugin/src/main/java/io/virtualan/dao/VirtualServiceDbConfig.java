/*
 * Copyright 2018 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This is Virtual Service Db Config bean.
 *
 * @author Elan Thangamani
 *
 **/
@Configuration
@EntityScan("io.virtualan.entity")
@EnableJpaRepositories(entityManagerFactoryRef = "virtualEntityManagerFactory",
        transactionManagerRef = "virtualTransactionManager", basePackages = {"io.virtualan"})
public class VirtualServiceDbConfig {

    private static final Logger log = LoggerFactory.getLogger(VirtualServiceDbConfig.class);

    @Bean(name = "virtualDataSource")
    @ConditionalOnProperty(name = {"virtualan.datasource.jdbc-url"}, matchIfMissing = false)
    @ConfigurationProperties(prefix = "virtualan.datasource")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "virtualDataSource")
    @ConditionalOnProperty(name = {"virtualan.datasource.jdbc-url"}, matchIfMissing = true)
    @Primary
    public DataSource virtualDataSource() {
        VirtualServiceDbConfig.log
                .warn("Runs in standalone mode. virtualan.datasource.jdbc-url is missing");
        return DataSourceBuilder.create().url("jdbc:hsqldb:mem:virtulan-inmem-db").username("sa")
                .build();
    }

    @Bean(name = "virtualEntityManagerFactory")
    @PersistenceContext(unitName = "virtualan-unit")
    @Primary
    public LocalContainerEntityManagerFactoryBean virtualEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("virtualDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("io.virtualan.entity")
                .properties(jpaProperties()).persistenceUnit("virtualan-unit").build();
    }

    private Map<String, Object> jpaProperties() {
        final Map<String, Object> props = new HashMap<>();
        props.put("hibernate.ejb.naming_strategy", new SpringPhysicalNamingStrategy());
        return props;
    }

    @Bean(name = "virtualTransactionManager")
    @Primary
    public PlatformTransactionManager virtualTransactionManager(
            @Qualifier("virtualEntityManagerFactory") EntityManagerFactory mockEntityManagerFactory) {
        return new JpaTransactionManager(mockEntityManagerFactory);
    }
}
