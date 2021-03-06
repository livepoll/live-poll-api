package de.livepoll.api.entity.db

import javax.persistence.CascadeType
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

    @OneToMany(mappedBy = "quizItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    override var answers: MutableList<QuizItemAnswer>

) : PollItemAnswerable(id, poll, question, position, PollItemType.QUIZ, answers)
