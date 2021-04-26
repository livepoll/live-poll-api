package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
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

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "poll_id")
        open val poll: Poll,

        @Column(nullable = false)
        open val question: String,

        @Column(nullable = false)
        open val position: Int
)