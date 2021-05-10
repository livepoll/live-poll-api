package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "quiz_item_answer")
class QuizItemAnswer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    @Column
    val id: Long,

    @JsonIgnore
    @NonNull
    @ManyToOne
    @JoinColumn(name = "poll_item_id")
    val quizItem: QuizItem,

    @Column(name = "selection_option")
    val selectionOption: String,

    @Column(name = "is_correct")
    var isCorrect: Boolean,

    @Column(name = "answer_count")
    var answerCount: Int

)
