package de.livepoll.api.service

import com.sun.mail.iap.Response
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.dto.AnswerDtoOut
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.AnswerRepository
import de.livepoll.api.repository.MultipleChoiceItemRepository
import de.livepoll.api.repository.OpenTextItemRepository
import de.livepoll.api.repository.QuizItemRepository
import de.livepoll.api.util.toDtoOut
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PollItemService(
        private val answerRepository: AnswerRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val quizItemRepository: QuizItemRepository,
        private val openTextItemRepository: OpenTextItemRepository
) {

    fun getPollItem(pollItemId: Int, itemType: String): ResponseEntity<*> {
        when (itemType.toLowerCase()) {
            "multiplechoiceitem" -> {
                multipleChoiceItemRepository.findById(pollItemId).orElseGet {
                    throw ResponseStatusException(HttpStatus.NOT_FOUND, "This poll item does not exist")
                }.run {
                    if (userHasAccess(this)) {
                        return ResponseEntity.ok().body(this.toDtoOut())
                    } else {
                        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized")
                    }
                }
            }
            "quizitem" -> {
                quizItemRepository.findById(pollItemId).orElseGet {
                    throw ResponseStatusException(HttpStatus.NOT_FOUND, "This poll item does not exist")
                }.run {
                    if (userHasAccess(this)) {
                        return ResponseEntity.ok().body(this.toDtoOut())
                    } else {
                        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized")
                    }
                }
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong itemtype")
            }
        }
    }

    fun getAnswers(pollItemId: Int): List<AnswerDtoOut> {
        return answerRepository.findByPollItemId(pollItemId).map { it.toDtoOut() }
    }

    private fun userHasAccess(item: PollItem) = (item.poll.user.username == SecurityContextHolder.getContext().authentication.name)


    fun deleteItem(itemId: Int, itemType: String) {
        when (itemType.toLowerCase()) {
            "multiplechoiceitem" -> {
                val item = multipleChoiceItemRepository.getOne(itemId)
                if (userHasAccess(item)) {
                    try {
                        multipleChoiceItemRepository.deleteById(itemId)
                    } catch (ex: EmptyResultDataAccessException) {
                    }
                } else {
                    throw Exception("Not authorzied")
                }
            }
            "quizitem" -> {
                val item = quizItemRepository.getOne(itemId)
                if (userHasAccess(item)) {
                    try {
                        quizItemRepository.deleteById(itemId)
                    } catch (ex: EmptyResultDataAccessException) {
                        println("Item existiert nicht")
                    }
                } else {
                    throw Exception("Not authorzied")
                }
            }
            else -> {
                throw Exception("Wrong itemtype")
            }
        }
    }
}
