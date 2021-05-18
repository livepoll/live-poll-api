package de.livepoll.api.pollitem.multiplechoice

import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.db.PollItemType
import de.livepoll.api.poll.Poll
import javax.persistence.*

@Entity
@Table(name = "multiple_choice_item")
class MultipleChoiceItem(
    id: Long,

    poll: Poll,

    position: Int,

    question: String,

    @Column(name = "allow_multiple_answers")
    var allowMultipleAnswers: Boolean,

    @Column(name = "allow_blank_field")
    var allowBlankField: Boolean,

    @OneToMany(mappedBy = "multipleChoiceItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    var answers: MutableList<MultipleChoiceItemAnswer>

) : PollItem(id, poll, question, position, PollItemType.MULTIPLE_CHOICE)
