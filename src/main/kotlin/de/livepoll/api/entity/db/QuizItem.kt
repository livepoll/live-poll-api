package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="Quiz_Item")
class QuizItem(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var pos:Int,

        @NotNull
        var question:String,

        @NotNull
        val answers: List<Answer>
) {
}