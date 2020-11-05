package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name="multiple_Choice_Item")
data class MultipleChoiceItem(
        @Id
        @NotNull
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="multiple_choice_item_id")
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var position: Int,

        @NotNull
        var question: String,

        @NotNull
        @OneToMany(mappedBy="pollItemId")
        val answers: List<Answer>
)