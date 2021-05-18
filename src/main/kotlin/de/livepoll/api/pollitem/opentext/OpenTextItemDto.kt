package de.livepoll.api.pollitem.opentext

import de.livepoll.api.pollitem.PollItemDtoIn
import de.livepoll.api.pollitem.PollItemDtoOut

open class OpenTextItemDtoIn(
    pollId: Long,
    question: String,
) : PollItemDtoIn(pollId, question)

class OpenTextItemWithPositionDtoIn(
    pollId: Long,
    question: String,
    val position: Int
) : OpenTextItemDtoIn(pollId, question)

class OpenTextItemDtoOut(
    itemId: Long,
    pollId: Long,
    question: String,
    position: Int,
    type: String,
    val answers: List<OpenTextItemAnswerDtoOut>
) : PollItemDtoOut(itemId, pollId, question, position, type)
