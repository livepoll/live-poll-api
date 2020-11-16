package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "answer")
data class Answer(
        @Id
        @NotNull
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        @Column(name="answer_id", nullable = false)
        var id: Int,

        @Column(nullable = false)
        var pollItemId: Int,

        @Column(nullable = false)
        var answer: String
)