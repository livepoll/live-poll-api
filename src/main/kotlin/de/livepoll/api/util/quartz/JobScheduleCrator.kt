package de.livepoll.api.util.quartz

import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.SimpleTrigger
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
import org.springframework.stereotype.Component
import java.util.*


@Component
class JobScheduleCrator(
        private val applicationContext: ApplicationContext
) {

    fun createJob(jobClass: Class<out Job>, jobName: String, pollId: Long): JobDetail {

        val factoryBean = JobDetailFactoryBean()
        factoryBean.setJobClass(jobClass)
        factoryBean.setDurability(true)
        factoryBean.setApplicationContext(applicationContext)
        factoryBean.setName(jobName)
        val jobDataMap = JobDataMap()
        jobDataMap["pollId"] = pollId
        factoryBean.jobDataMap = jobDataMap
        factoryBean.afterPropertiesSet()

        return factoryBean.getObject()!!
    }

    fun createSimpleTrigger(triggerName: String, startTime: Date): SimpleTrigger {

        val factoryBean = SimpleTriggerFactoryBean()
        factoryBean.setName(triggerName)
        factoryBean.setStartTime(startTime)
        factoryBean.setRepeatCount(0)
        factoryBean.setRepeatInterval(1)
        factoryBean.afterPropertiesSet()

        return factoryBean.getObject()!!
    }
}