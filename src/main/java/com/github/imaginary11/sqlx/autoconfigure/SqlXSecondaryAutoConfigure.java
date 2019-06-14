package com.github.imaginary11.sqlx.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.imaginary11.sqlx.props.SqlXPrimaryDbProperties;
import com.github.imaginary11.sqlx.props.SqlXPrimaryMybatisProperties;
import com.github.imaginary11.sqlx.props.SqlXSecondaryDbProperties;
import com.github.imaginary11.sqlx.props.SqlXSecondaryMybatisProperties;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author : Imaginary
 * @version : V1.0
 * @date : 2019/6/12 13:52
 */

@Configuration
@ConditionalOnClass({SqlSessionTemplate.class, DataSourceTransactionManager.class, SqlSessionFactory.class, DataSource.class})
@EnableConfigurationProperties({SqlXSecondaryMybatisProperties.class,SqlXSecondaryDbProperties.class})
public class SqlXSecondaryAutoConfigure {

    private ResourceLoader resourceLoader;



    @Resource
    private SqlXSecondaryDbProperties sqlXSecondaryDbProperties;

    @Resource
    private SqlXSecondaryMybatisProperties sqlXSecondaryMybatisProperties;

    public SqlXSecondaryAutoConfigure(ResourceLoader resourceLoader,SqlXSecondaryDbProperties sqlXSecondaryDbProperties, SqlXSecondaryMybatisProperties sqlXSecondaryMybatisProperties) {
        this.resourceLoader = resourceLoader;

        this.sqlXSecondaryDbProperties = sqlXSecondaryDbProperties;
        this.sqlXSecondaryMybatisProperties = sqlXSecondaryMybatisProperties;
    }


    @Bean(name = "secondarySqlSessionTemplate")
    @ConditionalOnMissingBean(name = "secondarySqlSessionTemplate")
    @ConditionalOnProperty(prefix = "sqlx.secondary", value = "enabled", havingValue = "true")
    public SqlSessionTemplate secondarySqlSessionTemplate(@Qualifier("secondarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "secondary")
    @ConditionalOnMissingBean(name = "secondary")
    @ConditionalOnProperty(prefix = "sqlx.secondary", value = "enabled", havingValue = "true")
    public DataSourceTransactionManager secondaryTransactionManager(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondarySqlSessionFactory")
    @ConditionalOnMissingBean(name = "secondarySqlSessionFactory")
    @ConditionalOnProperty(prefix = "sqlx.secondary", value = "enabled", havingValue = "true")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(this.sqlXSecondaryMybatisProperties.resolveMapperLocations());
        bean.setConfigLocation(this.resourceLoader.getResource(this.sqlXSecondaryMybatisProperties.getConfigLocation()));
        bean.setTypeAliasesPackage(this.sqlXSecondaryMybatisProperties.getTypeAliasesPackage());

        PageHelper pageHelper = new PageHelper();
        Properties prop = new Properties();
        prop.put("properties", "dialect=mysql");
        pageHelper.setProperties(prop);
        bean.setPlugins(new Interceptor[]{pageHelper});

        return bean.getObject();
    }

    @Bean("secondaryDataSource")
    @ConditionalOnMissingBean(name = "secondaryDataSource")
    @ConditionalOnProperty(prefix = "sqlx.secondary", value = "enabled", havingValue = "true")
    public DataSource secondaryDataSource() {
        System.setProperty("druid.filters", "stat");
        System.setProperty("druid.useGlobalDataSourceStat", "true");
        return sqlXSecondaryDbProperties.initializeDataSourceBuilder()
                .type(DruidDataSource.class)
                .build();
    }
}
