package org.simulatest.example.springboot

import org.h2.jdbcx.JdbcDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Properties
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(basePackages = ["org.simulatest.example.springboot"])
@EnableTransactionManagement
class JpaConfig {

	@Bean
	fun dataSource(): DataSource {
		return JdbcDataSource().apply {
			setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
		}
	}

	@Bean
	fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
		return LocalContainerEntityManagerFactoryBean().apply {
			setDataSource(dataSource)
			setPackagesToScan("org.simulatest.example.springboot")
			jpaVendorAdapter = HibernateJpaVendorAdapter()
			setJpaProperties(Properties().apply {
				setProperty("hibernate.hbm2ddl.auto", "create")
			})
		}
	}

	@Bean
	fun transactionManager(emf: jakarta.persistence.EntityManagerFactory): PlatformTransactionManager {
		return JpaTransactionManager(emf)
	}
}
