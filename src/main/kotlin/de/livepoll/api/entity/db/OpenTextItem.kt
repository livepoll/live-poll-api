package de.livepoll.api.entity.db

import javax.persistence.*

@Entity
@Table(name = "open_Text_Item")
class OpenTextItem(
        id: Int, poll: Poll, question: String, position: Int
) : PollItem(id, poll, question, position)