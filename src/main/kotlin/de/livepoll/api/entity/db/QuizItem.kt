package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "quiz_Item")
data class QuizItem(
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="quiz_item_id", nullable = false)
        var id: Int,

        @Column(nullable = false)
        var pollId: Int,

        @Column(nullable = false)
        var pos:Int,

        @Column(nullable = false)
        var question:String
)