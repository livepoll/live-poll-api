package de.livepoll.api.util

import de.livepoll.api.poll.Poll
import de.livepoll.api.poll.PollDtoOut
import de.livepoll.api.pollitem.multiplechoice.MultipleChoiceItem
import de.livepoll.api.pollitem.multiplechoice.MultipleChoiceItemAnswer
import de.livepoll.api.pollitem.multiplechoice.MultipleChoiceItemAnswerDtoOut
import de.livepoll.api.pollitem.multiplechoice.MultipleChoiceItemDtoOut
import de.livepoll.api.pollitem.opentext.*
import de.livepoll.api.pollitem.quiz.QuizItem
import de.livepoll.api.pollitem.quiz.QuizItemAnswer
import de.livepoll.api.pollitem.quiz.QuizItemAnswerDtoOut
import de.livepoll.api.pollitem.quiz.QuizItemDtoOut
import de.livepoll.api.user.User
import de.livepoll.api.user.UserDtoIn
import de.livepoll.api.user.UserDtoOut

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

fun OpenTextItemParticipantAnswerDtoIn.toDbEntity(item: OpenTextItem): OpenTextItemAnswer {
    return OpenTextItemAnswer(0, item, this.answer)
}
