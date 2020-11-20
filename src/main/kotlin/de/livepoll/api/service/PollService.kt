package de.livepoll.api.service

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.repository.*
import org.springframework.stereotype.Service

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val answerRepository: AnswerRepository,
        private val quizItemRepository: QuizItemRepository
) {

    fun createPollEntity(pollDto: PollDtoIn, userId: Int) {
        userRepository.findById(userId).orElseGet { null }.run {
            val poll = Poll(0, this, pollDto.name, pollDto.startDate, pollDto.endDate, emptyList<PollItem>().toMutableList())
            pollRepository.saveAndFlush(poll)
        }
    }

    fun addMultipleChoiceItem(item: MultipleChoiceItemDtoIn): MultipleChoiceItem {
        pollRepository.findById(item.pollId).orElseGet { null }.run {
            val multipleChoiceItem = MultipleChoiceItem(0, this, item.question, item.position, emptyList())
            val answers = item.answers.map { Answer(0, multipleChoiceItem, it) }
            multipleChoiceItemRepository.saveAndFlush(multipleChoiceItem)
            answerRepository.saveAll(answers)
            return multipleChoiceItem
        }
    }

    fun addQuizItem(item: MultipleChoiceItemDtoIn): QuizItem {
        pollRepository.findById(item.pollId).orElseGet { null }.run {
            val quizItem = QuizItem(0, this, item.question, item.position, emptyList())
            val answers = item.answers.map { Answer(0, quizItem, it) }
            quizItemRepository.saveAndFlush(quizItem)
            answerRepository.saveAll(answers)
            return quizItem
        }
    }
}