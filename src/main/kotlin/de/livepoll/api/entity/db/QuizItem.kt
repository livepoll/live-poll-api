package de.livepoll.api.entity.db

import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "quiz_item")
class QuizItem(
    id: Long,

    poll: Poll,

    position: Int,

    question: String,

    @OneToMany(mappedBy = "quizItem")
    var answers: MutableList<QuizItemAnswer>

) : PollItem(id, poll, question, position, PollItemType.QUIZ)