package com.github.imaginary11.sqlx.props;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : Imaginary
 * @version : V1.0
 * @date : 2019/6/12 13:52
 */
@ConfigurationProperties("sqlx.primary.db")
public class SqlXPrimaryDbProperties extends DataSourceProperties {
    private String type;
    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private Integer druidInitSize;
    private Integer druidMaxActive;
    private String validateQuery;
}
