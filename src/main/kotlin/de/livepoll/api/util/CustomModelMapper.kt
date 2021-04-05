package de.livepoll.api.util

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*

// --------------------------------------------------- Poll mappers ----------------------------------------------------

fun Poll.toDtoOut(): PollDtoOut {
    return PollDtoOut(this.id, this.name, this.startDate, this.endDate)
}

// --------------------------------------------------- User mappers ----------------------------------------------------

fun User.toDtoOut() = UserDtoOut(this.id, this.username, this.email)

fun UserDtoIn.toDbEntity() = User(0, this.username, this.email, this.password, false, "", emptyList())


// ------------------------------------------------- Poll item mappers -------------------------------------------------

fun PollItem.toDtoOut() : PollItemDtoOut {
    return PollItemDtoOut(this.id, this.poll.id, this.question, this.position, "TODO")
}

fun MultipleChoiceItem.toDtoOut() = MultipleChoiceItemDtoOut(this.id,this.poll.id, this.question, this.position, "multiple-choice", this.answers.map{
    it.toDtoOut()
})

fun QuizItem.toDtoOut() = QuizItemDtoOut(this.id,this.poll.id, this.question, this.position, "quiz", this.answers.map{
    it.toDtoOut()
})


// --------------------------------------------------- Answer mappers --------------------------------------------------

fun Answer.toDtoOut() = AnswerDtoOut(this.id, this.answer)
