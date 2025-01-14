package kt.aivle.configuration.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import lombok.SneakyThrows;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@MapperScan(
    basePackages = {"kt.aivle"},
    annotationClass = DATA_SOURCE.class,
    sqlSessionFactoryRef = "sqlSessionFactoryWrite")
@Configuration
public class DatabaseConfig {

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public HikariConfig hikariConfigWrite() {

    // Create a Properties object
    Properties dataSourceProperties = new Properties();

// Set properties
    dataSourceProperties.setProperty("cachePrepStmts", "true"); // Replace with your JDBC driver class
    dataSourceProperties.setProperty("prepStmtCacheSize", "250"); // Database username
    dataSourceProperties.setProperty("prepStmtCacheSqlLimit", "2048"); // Database password
    dataSourceProperties.setProperty("useServerPrepStmts", "true"); // Database name
// Add other data source specific properties here



    HikariConfig hi = new HikariConfig();
    hi.setConnectionTimeout(60000);
    hi.setIdleTimeout(10000);
    hi.setMaximumPoolSize(20);
    hi.setMinimumIdle(5);
    hi.setMaxLifetime(2000000);
    hi.setConnectionTestQuery("SELECT 2");
    hi.setTransactionIsolation("TRANSACTION_READ_UNCOMMITTED");
    hi.setPoolName("System-HikariPool");
    hi.setDataSourceProperties(dataSourceProperties);
    return hi;
  }

  @Bean(name = "dataSourceWrite")
  @Primary
  public DataSource dataSourcWrite() {
    DataSource dataSource = new HikariDataSource(hikariConfigWrite());
    return dataSource;
  }

  @SneakyThrows
  @Bean(name = "sqlSessionFactoryWrite")
  @Primary
  public SqlSessionFactory sqlSessionFactory(
      @Qualifier("dataSourceWrite") DataSource dataSource,
      ApplicationContext applicationContext) {

    final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setConfiguration(configurationWrite());
    sessionFactory.setMapperLocations(
        applicationContext.getResources("classpath*:/mapper/**/*.xml"));
    return sessionFactory.getObject();
  }

  @Bean
  @Primary
  public SqlSessionTemplate sqlSessionTemplateWrite(SqlSessionFactory sqlSessionFactory) {
    final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
    return sqlSessionTemplate;
  }

  @Bean
  @Primary
  public DataSourceTransactionManager transactionManagerWrite() {
    DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSourcWrite());
    return manager;
  }

  @Bean
  @Primary
  public org.apache.ibatis.session.Configuration configurationWrite() {
    org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
    config.setCacheEnabled(false);
    config.setUseGeneratedKeys(true);
    config.setDefaultExecutorType(ExecutorType.REUSE);
    config.setAggressiveLazyLoading(false);
    config.setMapUnderscoreToCamelCase(true);
    config.setCallSettersOnNulls(true);
    config.setJdbcTypeForNull(JdbcType.NULL);
    return config;
  }


}
