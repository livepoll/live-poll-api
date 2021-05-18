package de.livepoll.api.pollitem.opentext

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "open_text_item_answer")
class OpenTextItemAnswer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    @Column
    val id: Long,

    @JsonIgnore
    @NonNull
    @ManyToOne
    @JoinColumn(name = "poll_item_id")
    val openTextItem: OpenTextItem,

    @Column
    val answer: String

)
