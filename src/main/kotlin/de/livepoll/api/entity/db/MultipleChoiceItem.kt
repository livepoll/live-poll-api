package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "multiple_Choice_Item")
class MultipleChoiceItem(
        id: Int, pollId: Int, question: String, position: Int,

        @Column(nullable = false)
        @OneToMany(mappedBy = "pollItemId")
        val answers: List<Answer>
) : PollItem(id, pollId, question, position)