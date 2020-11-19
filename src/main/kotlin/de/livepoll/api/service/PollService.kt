package de.livepoll.api.service

import de.livepoll.api.entity.db.Answer
import de.livepoll.api.entity.db.MultipleChoiceItem
import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.repository.AnswerRepository
import de.livepoll.api.repository.MultipleChoiceItemRepository
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val answerRepository: AnswerRepository
) {

    fun createPollEntity(pollDto: PollDtoIn, userId: Int) {
        userRepository.findById(userId).orElseGet { null }.run {
            val poll = Poll(0, this, pollDto.name, pollDto.startDate, pollDto.endDate, emptyList<PollItem>().toMutableList())
            pollRepository.saveAndFlush(poll)
        }
    }

    fun addMultipleChoiceItem(item: MultipleChoiceItemDtoIn): MultipleChoiceItem{
        pollRepository.findById(item.pollId).orElseGet { null }.run {
            val multipleChoiceItem =MultipleChoiceItem(0, item.pollId, item.question, item.position, emptyList())
            val answers = emptyList<Answer>().toMutableList()
            item.answers.forEach{
                answers.add(Answer(0, item.pollId, it))
            }
            answerRepository.saveAll(answers)
            return multipleChoiceItemRepository.saveAndFlush(multipleChoiceItem)

        }


    }
}