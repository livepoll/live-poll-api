package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name="answer")
data class Answer(
        @Id
        @NotNull
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        @Column(name="answer_id")
        var id: Int,

        @NotNull
        var pollItemId: Int,

        @NotNull
        var answer: String
)