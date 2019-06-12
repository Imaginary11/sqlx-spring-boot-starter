package com.github.imaginary11.sqlx.props;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : Imaginary
 * @version : V1.0
 * @date : 2019/6/12 13:52
 */
@ConfigurationProperties("sqlx.primary.mybatis")
public class SqlXPrimaryMybatisProperties extends MybatisProperties {
    private String configLocation;
    private String mapperLocations;
    private String aliasTypePackage;
}
