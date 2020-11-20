package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "multiple_Choice_Item")
class MultipleChoiceItem(
        id: Int, poll: Poll, question: String, position: Int,

        @JsonIgnore
        @OneToMany(mappedBy = "pollItem", cascade = [CascadeType.ALL])
        val answers: List<Answer>
) : PollItem(id, poll, question, position)