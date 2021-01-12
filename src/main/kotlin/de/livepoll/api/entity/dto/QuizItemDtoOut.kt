package de.livepoll.api.entity.dto

import de.livepoll.api.entity.db.Answer

class QuizItemDtoOut(
        itemId: Int, pollId: Int, question: String, position: Int, type: String,
        val answers: List<AnswerDtoOut>
) : PollItemDtoOut(itemId, pollId, question, position, type)