package de.livepoll.api.util

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*

// --------------------------------------------------- Poll mappers ----------------------------------------------------

fun Poll.toDtoOut(): PollDtoOut {
    val pollItems = this.pollItems.map { PollItemDtoOut(it.id, it.poll.id, it.question, it.position, it::class.toString().split(".")[5]) }
    return PollDtoOut(this.id, this.name, this.startDate, this.endDate, pollItems.sortedBy { it.position })
}

// --------------------------------------------------- User mappers ----------------------------------------------------

fun User.toDtoOut() = UserDtoOut(this.id, this.username, this.email, this.polls)

fun UserDtoIn.toDbEntity() = User(0, this.username, this.email, this.password, false, "", emptyList())


// --------------------------------------------------- Poll item mappers -----------------------------------------------

fun MultipleChoiceItem.toDtoOut() = MultipleChoiceItemDtoOut(this.id,this.poll.id, this.question, this.position, "MultipleChoiceItem", this.answers.map{
    it.toDtoOut()
})

fun QuizItem.toDtoOut() = QuizItemDtoOut(this.id,this.poll.id, this.question, this.position, "QuizItem", this.answers.map{
    it.toDtoOut()
})


// --------------------------------------------------- Answer mappers --------------------------------------------------

fun Answer.toDtoOut() = AnswerDtoOut(this.id, this.answer)
