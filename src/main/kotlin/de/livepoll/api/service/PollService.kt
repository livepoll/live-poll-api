package de.livepoll.api.service

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.util.quartz.JobScheduleCrator
import de.livepoll.api.util.quartz.StartPollPresentationJob
import de.livepoll.api.util.quartz.StopPollPresentationJob
import de.livepoll.api.util.toDtoOut
import org.quartz.JobKey
import org.quartz.TriggerKey
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository,
        private val pollItemService: PollItemService,
        private val webSocketService: WebSocketService,
        private val schedulerFactory: SchedulerFactoryBean,
        private val jobScheduleCrator: JobScheduleCrator
) {


    //--------------------------------------------- Get ----------------------------------------------------------------

    fun getPoll(pollId: Long): PollDtoOut {
        return pollRepository.findById(pollId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found") }
                .run { this.toDtoOut() }
    }

    fun getPollItemsForPoll(pollId: Long): List<PollItemDtoOut> {
        pollRepository.findById(pollId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found") }
                .run {
                    return this.pollItems.map { pollItemService.getPollItem(it.id) }
                }
    }


    //-------------------------------------------- Create --------------------------------------------------------------

    fun createPoll(pollDto: PollDtoIn, userId: Long): PollDtoOut {
        userRepository.findById(userId)
                .orElseThrow {
                    ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                }
                .run {
                    val r = Random()
                    var slug: String
                    do {
                        slug = ""
                        for (i in 1..6) {
                            slug += r.nextInt(16)
                        }
                    } while (!isSlugUnique(slug))
                    val poll = Poll(
                            0,
                            this,
                            pollDto.name,
                            pollDto.startDate,
                            pollDto.endDate,
                            slug,
                            null,
                            emptyList<PollItem>().toMutableList()
                    )
                    val pollFromDb = pollRepository.saveAndFlush(poll)
                    try {
                        if (pollFromDb.startDate != null && pollFromDb.endDate != null) {
                            schedulePoll(pollFromDb.id, pollFromDb.startDate!!, pollFromDb.endDate!!)
                            return pollFromDb.toDtoOut()
                        } else {
                            pollFromDb.startDate = null
                            pollFromDb.endDate = null
                            return pollRepository.saveAndFlush(pollFromDb).toDtoOut()
                        }
                    } catch (ex: ResponseStatusException) {
                        pollFromDb.startDate = null
                        pollFromDb.endDate = null
                        return pollRepository.saveAndFlush(pollFromDb).toDtoOut()
                    }
                }
    }


    //-------------------------------------------- Delete --------------------------------------------------------------

    fun deletePoll(id: Long) {
        try {
            pollRepository.deleteById(id)
        } catch (ex: EmptyResultDataAccessException) {
            throw ResponseStatusException(HttpStatus.NO_CONTENT)
        }
    }


    //-------------------------------------------- Update --------------------------------------------------------------

    fun updatePoll(pollId: Long, poll: PollDtoIn): PollDtoOut {
        pollRepository.findById(pollId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            this.name = poll.name
            this.currentItem = poll.currentItem
            if (poll.startDate != null && poll.endDate != null) {
                updateScheduledPoll(pollId, poll.startDate, poll.endDate)
                this.startDate = poll.startDate
                this.endDate = poll.endDate
            } else if (poll.startDate == null && poll.endDate == null) {
                stopScheduledPoll(pollId)
                this.startDate = null
                this.endDate = null
            }
            if (poll.slug != null && !this.slug.equals(poll.slug)) {
                if (isSlugUnique(poll.slug)) {
                    this.slug = poll.slug
                } else {
                    pollRepository.saveAndFlush(this)
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug already exists")
                }
            }
            if (this.currentItem != null) {
                webSocketService.sendCurrentItem(this.slug, this.id, this.currentItem)
                webSocketService.sendItemWithAnswers(this.currentItem!!)
            }
            return pollRepository.saveAndFlush(this).toDtoOut()
        }
    }

    fun getNextPollItem(pollId: Long): PollItemDtoOut? {
        pollRepository.findById(pollId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            pollItems.sortBy { it.position }
            if (currentItem == null) {
                this.currentItem = pollItems[0].id
            } else if (currentItem == pollItems.last().id) {
                this.currentItem = null
            } else {
                val oldItem = pollItems.find { it.id == currentItem }
                val newItem = pollItems.find { it.position == requireNotNull(oldItem).position + 1 }
                this.currentItem = requireNotNull(newItem).id
            }
            webSocketService.sendCurrentItem(this.slug, this.id, this.currentItem)
            pollRepository.saveAndFlush(this)
            return if (this.currentItem != null) {
                pollItemService.getPollItem(this.currentItem!!)
            } else {
                null
            }
        }
    }

    fun isSlugUnique(slug: String) = pollRepository.findBySlug(slug) == null

    private fun schedulePoll(pollId: Long, startDate: Date, stopDate: Date) {
        if (startDate.before(GregorianCalendar.getInstance().time) || stopDate.before(GregorianCalendar.getInstance().time)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Poll was not planned because start or end date is in the past")
        } else {
            val jobDetailStart = jobScheduleCrator.createJob(StartPollPresentationJob::class.java, "start-poll-" + pollId.toString(), pollId)
            val triggerStart = jobScheduleCrator.createSimpleTrigger("start-poll-trigger-" + pollId.toString(), startDate)
            schedulerFactory.`object`!!.scheduleJob(jobDetailStart, triggerStart)

            val jobDetailStop = jobScheduleCrator.createJob(StopPollPresentationJob::class.java, "stop-poll-" + pollId.toString(), pollId)
            val triggerStop = jobScheduleCrator.createSimpleTrigger("stop-poll-trigger-" + pollId.toString(), stopDate)
            schedulerFactory.`object`!!.scheduleJob(jobDetailStop, triggerStop)
        }
    }

    fun updateScheduledPoll(pollId: Long, startDate: Date, stopDate: Date) {
        if (startDate.before(GregorianCalendar.getInstance().time) || stopDate.before(GregorianCalendar.getInstance().time)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Poll was not planned because start or end date is in the past")
        } else {
            val jobNameStart: String = "start-poll-trigger-" + pollId
            val jobNameStop: String = "stop-poll-trigger-" + pollId
            val triggerStart = jobScheduleCrator.createSimpleTrigger(jobNameStart, startDate)
            val triggerStop = jobScheduleCrator.createSimpleTrigger(jobNameStop, stopDate)
            val returnDate = schedulerFactory.`object`!!.rescheduleJob(TriggerKey.triggerKey(jobNameStart), triggerStart)
            schedulerFactory.`object`!!.rescheduleJob(TriggerKey.triggerKey(jobNameStop), triggerStop)
            if (returnDate == null) {
                schedulePoll(pollId, startDate, stopDate)
            }
        }
    }

    @Transactional
    fun executeStartPoll(pollId: Long) {
        pollRepository.findById(pollId).ifPresent {
            it.currentItem = it.pollItems[0].id
            webSocketService.sendCurrentItem(it.slug, it.id, it.currentItem)
            pollRepository.saveAndFlush(it)
        }
    }

    fun executeStopPoll(pollId: Long) {
        pollRepository.findById(pollId).ifPresent {
            it.currentItem = null
            webSocketService.sendCurrentItem(it.slug, it.id, null)
            pollRepository.saveAndFlush(it)
        }
    }

    fun stopScheduledPoll(pollId: Long) {
        schedulerFactory.`object`!!.deleteJob(JobKey("start-poll-" + pollId))
        schedulerFactory.`object`!!.deleteJob(JobKey("stop-poll-" + pollId))
    }

}
