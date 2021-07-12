package de.livepoll.api.util

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

// --------------------------------------------------- Poll mappers ----------------------------------------------------

fun Poll.toDtoOut(): PollDtoOut {
    return PollDtoOut(this.id, this.name, this.startDate, this.endDate, this.slug, this.currentItem)
}


// --------------------------------------------------- User mappers ----------------------------------------------------

fun User.toDtoOut(): UserDtoOut {
    return UserDtoOut(this.id, this.username, this.email)
}

fun UserDtoIn.toDbEntity(): User {
    return User(0, this.username, this.email, this.password, false, "", emptyList())
}


// ------------------------------------------------- Poll item mappers -------------------------------------------------

fun MultipleChoiceItem.toDtoOut(): MultipleChoiceItemDtoOut {
    return MultipleChoiceItemDtoOut(this.id, this.poll.id, this.question, this.position,
        "multiple-choice", this.answers.map { it.toDtoOut() }
    )
}

fun MultipleChoiceItemAnswer.toDtoOut(): MultipleChoiceItemAnswerDtoOut {
    return MultipleChoiceItemAnswerDtoOut(this.id, this.selectionOption, this.answerCount)
}

fun QuizItem.toDtoOut(): QuizItemDtoOut {
    // Guarantee that first item is the correct one
    val answersSorted: MutableList<QuizItemAnswer> = this.answers
    val correctItemIndex = answersSorted.indexOfFirst { it.isCorrect }
    if (correctItemIndex == -1) {
        // Should never happen
        val msg = "There was no correct item in the list -> data inconsistency (should NEVER happen !!!)"
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, msg)
    }

    if (answersSorted.size > 1) {
        // Swap
        answersSorted[0] = answersSorted[correctItemIndex].also { answersSorted[correctItemIndex] = answersSorted[0] }
    }

    return QuizItemDtoOut(this.id, this.poll.id, this.question, this.position,
        "quiz", answersSorted.map { it.toDtoOut() }
    )
}

fun QuizItemAnswer.toDtoOut(): QuizItemAnswerDtoOut {
    return QuizItemAnswerDtoOut(this.id, this.selectionOption, this.isCorrect, this.answerCount)
}

fun OpenTextItem.toDtoOut(): OpenTextItemDtoOut {
    return OpenTextItemDtoOut(this.id, this.poll.id, this.question, this.position,
        "open-text", this.answers.map { it.toDtoOut() })
}

fun OpenTextItemAnswer.toDtoOut(): OpenTextItemAnswerDtoOut {
    return OpenTextItemAnswerDtoOut(this.id, this.answer)
}

fun OpenTextItemParticipantAnswerDtoIn.toDbEntity(item: OpenTextItem): OpenTextItemAnswer {
    return OpenTextItemAnswer(0, item, this.answer)
}

fun PollItemDtoOut.toPollItemWithPollName(pollName: String): PollItemWithPollNameDtoOut {
    return PollItemWithPollNameDtoOut(this.itemId, this.pollId, this.question, this.position, this.type, pollName)
}
