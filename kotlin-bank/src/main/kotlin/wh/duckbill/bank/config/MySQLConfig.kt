package wh.duckbill.bank.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class MySQLConfig(
  @Value("\${database.mysql.url}") val url: String,
  @Value("\${database.mysql.username}") val username: String,
  @Value("\${database.mysql.password}") val password: String,
  @Value("\${database.mysql.driver-class-name}") val driver: String,
) {

  @Bean
  fun dataSource(): DataSource {
    val datasource = DriverManagerDataSource(url, username, password)
    datasource.setDriverClassName(driver)
    return datasource
  }

  @Bean
  fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
    return DataSourceTransactionManager(dataSource)
  }
}