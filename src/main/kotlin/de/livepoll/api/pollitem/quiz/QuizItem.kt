package de.livepoll.api.pollitem.quiz

import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.db.PollItemType
import de.livepoll.api.poll.Poll
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
    var answers: MutableList<QuizItemAnswer>

) : PollItem(id, poll, question, position, PollItemType.QUIZ)