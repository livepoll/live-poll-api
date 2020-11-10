package de.livepoll.api.service

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository
) {

    fun create(pollDto: PollDtoIn, userId: Int) {
        val user = userRepository.getOne(userId)
        val poll = Poll(user, pollDto.name, pollDto.startDate, pollDto.endDate)
        pollRepository.saveAndFlush(poll)
    }

}

fun Poll.toDtoOut(): PollDtoOut {
    return PollDtoOut(this.name, this.startDate, this.endDate)
}
