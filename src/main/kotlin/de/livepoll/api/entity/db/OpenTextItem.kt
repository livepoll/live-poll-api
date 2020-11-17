package de.livepoll.api.entity.db

import javax.persistence.*

@Entity
@Table(name = "open_Text_Item")
class OpenTextItem(
        id: Int, pollId: Int, question: String, position: Int
) : PollItem(id, pollId, question, position)