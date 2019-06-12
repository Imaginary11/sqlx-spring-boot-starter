package com.github.imaginary11.sqlx.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.imaginary11.sqlx.props.SqlXPrimaryDbProperties;
import com.github.imaginary11.sqlx.props.SqlXPrimaryMybatisProperties;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author : Imaginary
 * @version : V1.0
 * @date : 2019/6/12 13:52
 */

@Configuration
@ConditionalOnClass({SqlSessionTemplate.class, DataSourceTransactionManager.class, SqlSessionFactory.class, DataSource.class})
@EnableConfigurationProperties({SqlXPrimaryDbProperties.class, SqlXPrimaryMybatisProperties.class})
public class SqlXAutoConfigure {

    private ResourceLoader resourceLoader;

    @Autowired
    private SqlXPrimaryDbProperties sqlXPrimaryDbProperties;

    @Autowired
    private SqlXPrimaryMybatisProperties sqlXPrimaryMybatisProperties;

    public SqlXAutoConfigure(ResourceLoader resourceLoader, SqlXPrimaryMybatisProperties sqlXPrimaryMybatisProperties, SqlXPrimaryDbProperties sqlXPrimaryDbProperties) {
        this.resourceLoader = resourceLoader;
        this.sqlXPrimaryDbProperties = sqlXPrimaryDbProperties;
        this.sqlXPrimaryMybatisProperties = sqlXPrimaryMybatisProperties;
    }

    @Bean(name = "primarySqlSessionTemplate")
    @ConditionalOnMissingBean
    public SqlSessionTemplate primarySqlSessionTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "primary")
    @ConditionalOnMissingBean
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "primarySqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(this.sqlXPrimaryMybatisProperties.resolveMapperLocations());
        bean.setConfigLocation(this.resourceLoader.getResource(this.sqlXPrimaryMybatisProperties.getConfigLocation()));
        bean.setTypeAliasesPackage(this.sqlXPrimaryMybatisProperties.getTypeAliasesPackage());

        PageHelper pageHelper = new PageHelper();
        Properties prop = new Properties();
        prop.put("properties", "dialect=mysql");
        pageHelper.setProperties(prop);
        bean.setPlugins(new Interceptor[]{pageHelper});

        return bean.getObject();
    }

    @Bean("primaryDataSource")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sqlx.primary", value = "enabled", havingValue = "true")
    public DataSource primaryDataSource() {
        System.setProperty("druid.filters", "stat");
        System.setProperty("druid.useGlobalDataSourceStat", "true");
        return sqlXPrimaryDbProperties.initializeDataSourceBuilder()
                .type(DruidDataSource.class)
                .build();
    }
}
