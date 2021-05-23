package de.livepoll.api.entity.db

abstract class PollItemAnswerable(
    id: Long,
    poll: Poll,
    question: String,
    position: Int,
    type: PollItemType,
    open val answers: MutableList<out SelectionOptionAnswer>

) : PollItem(id, poll, question, position, type)
