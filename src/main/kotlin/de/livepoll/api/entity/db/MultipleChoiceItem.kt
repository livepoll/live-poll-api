package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "multiple_Choice_Item")
data class MultipleChoiceItem(
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="multiple_choice_item_id", nullable = false)
        var id: Int,

        @Column(nullable = false)
        var pollId: Int,

        @Column(nullable = false)
        var position: Int,

        @Column(nullable = false)
        var question: String,

        @Column(nullable = false)
        @OneToMany(mappedBy="pollItemId")
        val answers: List<Answer>
)