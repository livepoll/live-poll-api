package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "quiz_Item")
class QuizItem(
        id: Int, pollId: Int, question: String, position: Int
) : PollItem(id, pollId, question, position)