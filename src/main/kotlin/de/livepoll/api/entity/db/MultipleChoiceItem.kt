package de.livepoll.api.entity.db

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "multiple_choice_item")
class MultipleChoiceItem(
    id: Long,

    poll: Poll,

    position: Int,

    question: String,

    @Column(name = "allow_multiple_answers")
    val allowMultipleAnswers: Boolean,

    @Column(name = "allow_blank_field")
    val allowBlankField: Boolean,

    @OneToMany(mappedBy = "multipleChoiceItem")
    var answers: MutableList<MultipleChoiceItemAnswer>

) : PollItem(id, poll, question, position, PollItemType.MULTIPLE_CHOICE)
