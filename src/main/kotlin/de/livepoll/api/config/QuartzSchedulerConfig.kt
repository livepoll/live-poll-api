package de.livepoll.api.config

import de.livepoll.api.quartz.AutoWiringSpringBeanJobFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.*
import javax.sql.DataSource

@Configuration
class QuartzSchedulerConfig {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = AutoWiringSpringBeanJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    @Bean
    fun scheduler(): SchedulerFactoryBean {
        val schedulerFactory = SchedulerFactoryBean()
        schedulerFactory.setConfigLocation(ClassPathResource("quartz.properties"))
        schedulerFactory.setJobFactory(springBeanJobFactory())
        schedulerFactory.setDataSource(dataSource)

        return schedulerFactory
    }
}