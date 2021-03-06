package de.livepoll.api.entity.db

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "open_text_item")
class OpenTextItem(
    id: Long,

    poll: Poll,

    question: String,

    position: Int,

    @OneToMany(mappedBy = "openTextItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    var answers: MutableList<OpenTextItemAnswer>

) : PollItem(id, poll, question, position, PollItemType.OPEN_TEXT)
