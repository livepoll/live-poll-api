package de.livepoll.api.util.quartz

import de.livepoll.api.repository.PollRepository
import de.livepoll.api.service.PollService
import de.livepoll.api.service.WebSocketService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StartPollPresentationJob : Job {

    @Autowired
    private lateinit var pollService: PollService

    @Transactional
    override fun execute(context: JobExecutionContext) {
        println("Execute start poll event with poll-id: " + context.jobDetail.jobDataMap["pollId"].toString())
        pollService.executeStartPoll(context.jobDetail.jobDataMap["pollId"].toString().toLong())
    }

}