package de.livepoll.api.service

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository
) {

    fun createPollEntity(pollDto: PollDtoIn, userId: Int) {
        userRepository.findById(userId).orElseGet { null }.run {
            val poll = Poll(this, pollDto.name, pollDto.startDate, pollDto.endDate)
            pollRepository.saveAndFlush(poll)
        }
    }
}