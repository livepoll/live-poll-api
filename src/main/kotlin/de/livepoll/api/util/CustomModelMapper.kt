package de.livepoll.api.util

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*

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
    return QuizItemDtoOut(this.id, this.poll.id, this.question, this.position,
        "quiz", this.answers.map { it.toDtoOut() }
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
