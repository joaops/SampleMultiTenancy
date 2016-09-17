/*
 * Copyright (C) 2016 João
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.joaops.smt.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author João
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "br.com.joaops.smt.repository")
public class PersistenceConfig {
    
    /*@Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost/smt");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }*/
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        //em.setDataSource(dataSource());
        em.setPackagesToScan(this.getPackagesToScan());
        
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(getAdditionalProperties());
        
        return em;
    }
    
    private String[] getPackagesToScan() { //Informa os Pacotes Que Devem Ser Scaneados
        List<String> packages = new ArrayList<>();
        packages.add("br.com.joaops.smt.model");
        return packages.toArray(new String[packages.size()]);
    }
    
    private String importFiles() {
        String files;
        files = "/META-INF/sql/sequences.sql, "+
                "/META-INF/sql/empresa.sql, "+
                "/META-INF/sql/system_database.sql, "+
                "/META-INF/sql/system_module.sql, "+
                "/META-INF/sql/system_user.sql, "+
                "/META-INF/sql/system_user_permission.sql, "+
                "/META-INF/sql/empresa_data.sql, "+
                "/META-INF/sql/system_database_data.sql, "+
                "/META-INF/sql/system_module_data.sql, "+
                "/META-INF/sql/system_user_data.sql, "+
                "/META-INF/sql/system_user_permission_data.sql";
        return files;
    }
    
    private Properties getAdditionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        //o multi-tenancy não suporta hibernate.hbm2ddl.auto nem hibernate.hbm2ddl.import_files, o BD deve ser criado na mão
        //properties.setProperty("hibernate.hbm2ddl.auto", "create-drop"); //trocar por validate
        //properties.setProperty("hibernate.hbm2ddl.import_files", importFiles()); //Obs. O validate não realiza importação dos arquivos sql
        
        properties.setProperty("hibernate.tenant_identifier_resolver", CurrentTenantIdentifierResolverImpl.class.getName());
        properties.setProperty("hibernate.multi_tenant_connection_provider", MultiTenantConnectionProvider.class.getName());
        properties.setProperty("hibernate.multiTenancy", "DATABASE");
        return properties;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
}