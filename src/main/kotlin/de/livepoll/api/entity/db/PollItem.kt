package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "poll_item")
@Inheritance(strategy = InheritanceType.JOINED)
open class PollItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    @Column(name = "poll_item_id")
    open val id: Long,

    @JsonIgnore
    @NonNull
    @ManyToOne
    @JoinColumn(name = "poll_id")
    open val poll: Poll,

    @NonNull
    @Column
    open val question: String,

    @NonNull
    @Column
    open val position: Int,

    @NonNull
    @Column
    open val type: PollItemType
)

enum class PollItemType {
    MULTIPLE_CHOICE, OPEN_TEXT, QUIZ
}
