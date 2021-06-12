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

    /**
     * Get a poll.
     *
     * @param pollId the id of the poll
     * @return poll in dto format
     */
    fun getPoll(pollId: Long): PollDtoOut {
        return pollRepository.findById(pollId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found") }
            .run { this.toDtoOut() }
    }

    /**
     * Get poll items for a specific poll.
     *
     * @param pollId the id of the poll
     * @return a list of poll items in dto format
     */
    fun getPollItemsForPoll(pollId: Long): List<PollItemDtoOut> {
        pollRepository.findById(pollId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found") }
            .run {
                return this.pollItems.map { pollItemService.getPollItem(it.id) }
            }
    }


    //-------------------------------------------- Create --------------------------------------------------------------

    /**
     * Create a new poll.
     *
     * @param pollDto the new poll in dto format
     * @param userId the user who creates the new poll
     * @return the new poll in dto format
     */
    fun createPoll(pollDto: PollDtoIn, userId: Long): PollDtoOut {
        userRepository.findById(userId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            }
            .run {
                // Generate random slug
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

                return try {
                    if (pollFromDb.startDate != null && pollFromDb.endDate != null) {
                        schedulePoll(pollFromDb.id, pollFromDb.startDate!!, pollFromDb.endDate!!)
                        pollFromDb.toDtoOut()
                    } else {
                        pollFromDb.startDate = null
                        pollFromDb.endDate = null
                        pollRepository.saveAndFlush(pollFromDb).toDtoOut()
                    }
                } catch (ex: ResponseStatusException) {
                    pollFromDb.startDate = null
                    pollFromDb.endDate = null
                    pollRepository.saveAndFlush(pollFromDb).toDtoOut()
                }
            }
    }


    //-------------------------------------------- Delete --------------------------------------------------------------

    /**
     * Delete a single poll. All items belonging to the poll are also deleted.
     *
     * @param pollId
     */
    fun deletePoll(pollId: Long) {
        try {
            pollRepository.deleteById(pollId)
        } catch (ex: EmptyResultDataAccessException) {
            throw ResponseStatusException(HttpStatus.NO_CONTENT)
        }
    }


    //-------------------------------------------- Update --------------------------------------------------------------

    /**
     * Update a poll.
     *
     * @param pollId the id of the poll which should be updated
     * @param poll a poll in dto format that contains the new data
     * @return the updated poll in dto format
     */
    fun updatePoll(pollId: Long, poll: PollDtoIn): PollDtoOut {
        pollRepository.findById(pollId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            this.name = poll.name
            this.currentItem = poll.currentItem

            try {
                if (poll.startDate == null) {
                    this.startDate = null
                    stopScheduledPoll("start-poll-$pollId")
                } else {
                    updateScheduledPollStart(pollId, poll.startDate)
                    this.startDate = poll.startDate
                }

                if (poll.endDate == null) {
                    this.endDate = null
                    stopScheduledPoll("stop-poll-$pollId")
                } else {
                    updateScheduledPollEnd(pollId, poll.endDate)
                    this.endDate = poll.endDate
                }
            } catch (ex: ResponseStatusException) {
                pollRepository.saveAndFlush(this)
                throw ResponseStatusException(HttpStatus.CONFLICT, ex.message)
            }

            if (poll.slug != null && this.slug != poll.slug) {
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

    /**
     * Set the active item from the poll to the next following item.
     *
     * @param pollId the id of the poll where the active item is to be continued
     * @return the next poll item in dto format
     */
    fun getNextPollItem(pollId: Long): PollItemDtoOut? {
        pollRepository.findById(pollId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            pollItems.sortBy { it.position }
            when (currentItem) {
                null -> this.currentItem = pollItems[0].id // set to first item
                pollItems.last().id -> this.currentItem = null // end reached
                else -> {
                    val oldItem = pollItems.find { it.id == currentItem }
                    val newItem = pollItems.find { it.position == requireNotNull(oldItem).position + 1 }
                    this.currentItem = requireNotNull(newItem).id
                }
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

    /**
     * Schedule a poll.
     *
     * @param pollId the id of the poll that should be scheduled
     * @param startDate the start date of the poll
     * @param stopDate the end date of the poll
     */
    private fun schedulePoll(pollId: Long, startDate: Date, endDate: Date) {
        checkIfDateIsValid(startDate)
        checkIfDateIsValid(endDate)
        schedulePollStart(pollId, startDate)
        schedulePollEnd(pollId, endDate)
    }

    /**
     * Internal method to create start event.
     *
     * @param pollId the id of the poll that should be scheduled
     * @param startDate the start date of the poll
     */
    private fun schedulePollStart(pollId: Long, startDate: Date) {
        val jobDetailStart = jobScheduleCrator.createJob(
            StartPollPresentationJob::class.java,
            "start-poll-$pollId",
            pollId
        )
        val triggerStart =
            jobScheduleCrator.createSimpleTrigger("start-poll-trigger-$pollId", startDate)
        schedulerFactory.`object`!!.scheduleJob(jobDetailStart, triggerStart)
    }

    /**
     * Internal method to create end event.
     *
     * @param pollId the id of the poll that should be scheduled
     * @param endDate the end date of the poll
     */
    private fun schedulePollEnd(pollId: Long, endDate: Date) {
        val jobDetailStop = jobScheduleCrator.createJob(
            StopPollPresentationJob::class.java,
            "stop-poll-$pollId",
            pollId
        )
        val triggerStop = jobScheduleCrator.createSimpleTrigger("stop-poll-trigger-$pollId", endDate)
        schedulerFactory.`object`!!.scheduleJob(jobDetailStop, triggerStop)
    }

    /**
     * Update the start date of an poll which has already been planned.
     *
     * @param pollId the id of the poll that should be scheduled
     * @param startDate the new start date of the poll
     */
    private fun updateScheduledPollStart(pollId: Long, startDate: Date) {
        checkIfDateIsValid(startDate)
        val jobNameStart = "start-poll-trigger-$pollId"
        val triggerStart = jobScheduleCrator.createSimpleTrigger(jobNameStart, startDate)
        val returnDate = schedulerFactory.`object`!!.rescheduleJob(TriggerKey.triggerKey(jobNameStart), triggerStart)
        if (returnDate == null) {
            schedulePollStart(pollId, startDate)
        }
    }

    /**
     * Update the end date of an poll which has already been planned.
     *
     * @param pollId the id of the poll that should be scheduled
     * @param endDate the new end date of the poll
     */
    private fun updateScheduledPollEnd(pollId: Long, endDate: Date) {
        checkIfDateIsValid(endDate)
        val jobNameStop = "stop-poll-trigger-$pollId"
        val triggerStop = jobScheduleCrator.createSimpleTrigger(jobNameStop, endDate)
        val returnDate = schedulerFactory.`object`!!.rescheduleJob(TriggerKey.triggerKey(jobNameStop), triggerStop)
        if (returnDate == null) {
            schedulePollEnd(pollId, endDate)
        }
    }

    /**
     * Check that the date is in the future.
     *
     * @param date the date
     * @throws ResponseStatusException an exception is thrown if the date is in the past
     */
    private fun checkIfDateIsValid(date: Date) {
        if (date.before(GregorianCalendar.getInstance().time)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Poll was not planned because start or end date is in the past"
            )
        }
    }

    /**
     * Start a poll.
     *
     * @param pollId the id of the poll which should be started
     */
    @Transactional
    fun executeStartPoll(pollId: Long) {
        pollRepository.findById(pollId).ifPresent {
            it.currentItem = it.pollItems[0].id
            webSocketService.sendCurrentItem(it.slug, it.id, it.currentItem)
            pollRepository.saveAndFlush(it)
        }
    }

    /**
     * Stop a poll.
     *
     * @param pollId the id of the poll which should be stoped
     */
    fun executeStopPoll(pollId: Long) {
        pollRepository.findById(pollId).ifPresent {
            it.currentItem = null
            webSocketService.sendCurrentItem(it.slug, it.id, null)
            pollRepository.saveAndFlush(it)
        }
    }

    /**
     * Unschedule a poll.
     *
     * @param pollId the id the poll which should be unscheduled
     */
    fun stopScheduledPoll(jobkey: String) {
        schedulerFactory.`object`!!.deleteJob(JobKey(jobkey))
    }

}
