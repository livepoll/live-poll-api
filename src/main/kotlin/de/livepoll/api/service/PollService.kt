package de.livepoll.api.service

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.QuizItemDtoIn
import de.livepoll.api.repository.*
import de.livepoll.api.util.toDtoOut
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class PollService(
        private val userRepository: UserRepository,
        private val pollRepository: PollRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val answerRepository: AnswerRepository,
        private val quizItemRepository: QuizItemRepository,
        private val openTextItemRepository: OpenTextItemRepository
) {

    fun createPollEntity(pollDto: PollDtoIn, userId: Int) {
        userRepository.findById(userId).orElseGet { null }.run {
            val r = Random()
            var slug: String
            do {
                slug = ""
                for (i in 1..6) {
                    slug += r.nextInt(10)
                }
            } while (!isSlugUnique(slug))
            val poll = Poll(0, this, pollDto.name, pollDto.startDate, pollDto.endDate, slug, null, emptyList<PollItem>().toMutableList())
            pollRepository.saveAndFlush(poll)
        }
    }

    fun createMultipleChoiceItem(item: MultipleChoiceItemDtoIn): MultipleChoiceItem {
        pollRepository.findById(item.pollId).orElseGet { null }.run {
            val multipleChoiceItem = MultipleChoiceItem(0, this, item.question, item.position, emptyList())
            val answers = item.answers.map { Answer(0, multipleChoiceItem, it) }
            multipleChoiceItemRepository.saveAndFlush(multipleChoiceItem)
            answerRepository.saveAll(answers)
            return multipleChoiceItem
        }
    }

    fun createQuizItem(item: QuizItemDtoIn): QuizItem {
        pollRepository.findById(item.pollId).orElseGet { null }.run {
            val quizItem = QuizItem(0, this, item.question, item.position, emptyList())
            val answers = item.answers.map { Answer(0, quizItem, it) }
            quizItemRepository.saveAndFlush(quizItem)
            answerRepository.saveAll(answers)
            return quizItem
        }
    }

    fun getPoll(id: Int): ResponseEntity<*> {
        pollRepository.findById(id).orElseGet {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            return ResponseEntity.ok().body(this.toDtoOut())
        }
    }

    fun getPollItemsForPoll(pollId: Int): ResponseEntity<*> {
        pollRepository.findById(pollId).orElseGet {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            val pollItemsOut = this.pollItems.map { it.toDtoOut() }
            return ResponseEntity.ok().body(pollItemsOut)
        }
    }

    fun deletePoll(id: Int) {
        pollRepository.findById(id).orElseGet { null }.run {
            if (this.user.username == SecurityContextHolder.getContext().authentication.name) {
                try {
                    pollRepository.deleteById(id)
                } catch (ex: EmptyResultDataAccessException) {
                }
                try {
                    multipleChoiceItemRepository.deleteById(id)
                } catch (ex: EmptyResultDataAccessException) {
                }
                try {
                    quizItemRepository.deleteById(id)
                } catch (ex: EmptyResultDataAccessException) {
                }
                try {
                    openTextItemRepository.deleteById(id)
                } catch (ex: EmptyResultDataAccessException) {
                }
            }
        }
    }

    fun updatePoll(pollId: Int, poll: PollDtoIn): ResponseEntity<*> {
        pollRepository.findById(pollId).orElseGet {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This poll does not exist")
        }.run {
            this.name = poll.name
            this.startDate = poll.startDate
            this.endDate = poll.endDate
            if (poll.slug != null && isSlugUnique(poll.slug)) {
                this.slug = poll.slug
            }
            if (poll.currentItem != null) {
                this.currentItem = poll.currentItem
            }
            pollRepository.saveAndFlush(this)
            return ResponseEntity.ok().body(this)
        }
    }

    fun isSlugUnique(slug: String): Boolean {
        val poll: Poll? = pollRepository.findBySlug(slug)
        return poll == null
    }
}