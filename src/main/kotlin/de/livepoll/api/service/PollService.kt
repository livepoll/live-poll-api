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
import java.util.*

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository,
        private val pollItemService: PollItemService,
        private val webSocketService: WebSocketService
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
                    return pollRepository.saveAndFlush(poll).toDtoOut()
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
            if (poll.slug != null && isSlugUnique(poll.slug)) {
                this.slug = poll.slug
            }
            if (poll.currentItem != null) {
                this.currentItem = poll.currentItem
                webSocketService.sendCurrenItem(this.slug, this.currentItem!!)
            } else {
                this.currentItem = null
            }
            return pollRepository.saveAndFlush(this).toDtoOut()
        }
    }

    fun isSlugUnique(slug: String) = pollRepository.findBySlug(slug) == null
}
