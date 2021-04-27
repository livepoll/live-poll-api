package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "multiple_choice_item_answer")
class MultipleChoiceItemAnswer (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    @Column
    val id: Long,

    @JsonIgnore
    @NonNull
    @ManyToOne
    @JoinColumn(name = "poll_item_id")
    var multipleChoiceItem: MultipleChoiceItem,

    @Column(name = "selection_option")
    val selectionOption: String,

    @Column(name = "answer_count")
    val answerCount: Int

)
