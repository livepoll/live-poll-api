package de.livepoll.api.service

import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.dto.AnswerDtoOut
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.AnswerRepository
import de.livepoll.api.repository.MultipleChoiceItemRepository
import de.livepoll.api.repository.QuizItemRepository
import de.livepoll.api.util.toDtoOut
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class PollItemService(
        private val answerRepository: AnswerRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val quizItemRepository: QuizItemRepository
) {

    fun getPollItem(pollItemId: Int, itemType: String): PollItemDtoOut {
        when (itemType.toLowerCase()) {
            "multiplechoiceitem" -> {
                val item = multipleChoiceItemRepository.getOne(pollItemId)
                if (userHasAccess(item)) {
                    return item.toDtoOut()
                } else {
                    throw Exception("Not authorzied")
                }
            }
            "quizitem" -> {
                val item = quizItemRepository.getOne(pollItemId)
                if (userHasAccess(item)) {
                    return item.toDtoOut()
                } else {
                    throw Exception("Not authorzied")
                }
            }
            else -> {
                throw Exception("Wrong itemtype")
            }
        }
    }

    fun getAnswers(pollItemId: Int): List<AnswerDtoOut> {
        return answerRepository.findByPollItemId(pollItemId).map { it.toDtoOut() }
    }

    private fun userHasAccess(item: PollItem) = (item.poll.user.username == SecurityContextHolder.getContext().authentication.name)

}