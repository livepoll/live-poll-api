package de.livepoll.api.pollitem.multiplechoice

import com.fasterxml.jackson.annotation.JsonIgnore
import de.livepoll.api.pollitem.SelectionOptionAnswer
import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "multiple_choice_item_answer")
class MultipleChoiceItemAnswer (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    @Column
    override val id: Long,

    @JsonIgnore
    @NonNull
    @ManyToOne
    @JoinColumn(name = "poll_item_id")
    var multipleChoiceItem: MultipleChoiceItem,

    @Column(name = "selection_option")
    override val selectionOption: String,

    @Column(name = "answer_count")
    override var answerCount: Int

) : SelectionOptionAnswer
