package de.livepoll.api.util.quartz

import de.livepoll.api.service.PollService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StopPollPresentationJob : Job {

    @Autowired
    private lateinit var pollService: PollService

    @Transactional
    override fun execute(context: JobExecutionContext) {
        println("Execute stop poll event with poll-id: " + context.jobDetail.jobDataMap["pollId"].toString())
        pollService.executeStopPoll(context.jobDetail.jobDataMap["pollId"].toString().toLong())
    }
}
