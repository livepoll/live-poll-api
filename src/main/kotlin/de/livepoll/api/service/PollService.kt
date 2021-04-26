package de.livepoll.api.service

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.util.toDtoOut
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PollService(
    private val userRepository: UserRepository,
    private val pollRepository: PollRepository,
    private val pollItemService: PollItemService
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
                val poll = Poll(
                    0,
                    this,
                    pollDto.name,
                    pollDto.startDate,
                    pollDto.endDate,
                    java.util.UUID.randomUUID().toString(),
                    null,
                    emptyList<PollItem>().toMutableList()
                )
                pollRepository.saveAndFlush(poll)
                return poll.toDtoOut()
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
            this.startDate = poll.startDate
            this.endDate = poll.endDate
            this.slug = poll.slug.toString()
            if (poll.currentItem != null) {
                this.currentItem = poll.currentItem
            }
            return pollRepository.saveAndFlush(this).toDtoOut()
        }
    }

}
