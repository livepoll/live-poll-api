package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name="quiz_Item")
data class QuizItem(
        @Id
        @NotNull
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="quiz_item_id")
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var pos:Int,

        @NotNull
        var question:String,

      /*  @NotNull
        @OneToMany(mappedBy="pollItemId")
        val answers: List<Answer>*/
)