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
    open var question: String,

    @NonNull
    @Column
    open var position: Int,

    @NonNull
    @Column
    open val type: PollItemType
)

enum class PollItemType(val representation: String) {
    MULTIPLE_CHOICE("multiple-choice"),
    OPEN_TEXT("open-text"),
    QUIZ("quiz")
}
