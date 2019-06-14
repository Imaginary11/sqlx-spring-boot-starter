## sqlx-spring-boot-starter
基于p6sy、druid 封装的springboot mysql starter，包含多数据源支持、日志打印开关、事务支持等功能，零配置，简单易用。


## 快速上手开发
### 1.引入jar包

    <dependency>
      <groupId>com.github.imaginary11</groupId>
      <artifactId>sqlx-spring-boot-starter</artifactId>
      <version>1.0.0-RELEASE</version>
    </dependency>

### 2.在Springboot 配置文件中加入一下配置（默认支持双数据源，sqlx.secondary.xxxxx，如需拓展，可以拉取源码修改，ps 建议一个服务使用一个数据源）
    # 启用开关
    sqlx.primary.enabled=true 
    # mybatis 配置文件位置
    sqlx.primary.mybatis.config-location = classpath:mybatis-config.xml 
    # mybatis sql xml 路径 {db_name} 替换为你的目录名称
    sqlx.primary.mybatis.mapper-locations = classpath:mapper/{db_name}/*.xml
    # 对应 model 包
    sqlx.primary.mybatis.type-aliases-package = com.example.demostarter.model
    # druid 驱动
    sqlx.primary.db.type = com.alibaba.druid.pool.DruidDataSource
    # mysql 配置
    sqlx.primary.db.url = jdbc:p6spy:mysql://{ip}:{port}/itsm_account?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&socketTimeout=300000
    sqlx.primary.db.driverClassName = com.p6spy.engine.spy.P6SpyDriver
    sqlx.primary.db.username = {username}
    sqlx.primary.db.password = {password}
    sqlx.primary.db.druid-initial-size = 10
    sqlx.primary.db.druid-max-active = 10
    sqlx.primary.db.v-query = select 1 from dual
    # sql 日志打印级别
    logging.level.p6spy = info

### 3.编写dao

### 4.编写sql xml 文件

### 5.springboot启动类加入 注解 

    @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
    @MapperScan(basePackages = "com.example.demostarter.dao", sqlSessionTemplateRef = "primarySqlSessionTemplate")

### 6.启动 Application测试
### 7. 事务支持 @Transactional("primary")
### 8.demo 演示 目录结构
![](https://github.com/Imaginary11/sqlx-spring-boot-starter/blob/master/sqlx.png)
![](https://github.com/Imaginary11/sqlx-spring-boot-starter/blob/master/sqlx-app.png)
![](https://github.com/Imaginary11/sqlx-spring-boot-starter/blob/master/sqlx-mybatis.png)
![](https://github.com/Imaginary11/sqlx-spring-boot-starter/blob/master/test.png)

### 9. 相关配置

mybatis-config.xml

        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
        <configuration>
            <settings>
                <setting name="mapUnderscoreToCamelCase" value="false"/>
                <setting name="cacheEnabled" value="true"/>
                <setting name="lazyLoadingEnabled" value="false"/>
                <setting name="defaultExecutorType" value="REUSE"/>
                <setting name="defaultStatementTimeout" value="300"/>
            </settings>
        </configuration>        
        
 application.properties
 
        sqlx.primary.enabled=true

        sqlx.primary.mybatis.config-location = classpath:mybatis-config.xml
        sqlx.primary.mybatis.mapper-locations = classpath:mapper/itsm_account/*.xml
        sqlx.primary.mybatis.type-aliases-package = com.example.demostarter.model

        sqlx.primary.db.type = com.alibaba.druid.pool.DruidDataSource
        sqlx.primary.db.url = jdbc:p6spy:mysql://xxx:3306/itsm_account?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&socketTimeout=300000
        sqlx.primary.db.driverClassName = com.p6spy.engine.spy.P6SpyDriver
        sqlx.primary.db.username = xxx
        sqlx.primary.db.password = xxx
        sqlx.primary.db.druid-initial-size = 10
        sqlx.primary.db.druid-max-active = 10
        sqlx.primary.db.v-query = select 1 from dual

        logging.level.p6spy = info
 

