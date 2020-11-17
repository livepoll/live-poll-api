package de.livepoll.api.entity.db

import javax.persistence.*

@Entity
@Table(name = "poll_item")
@Inheritance(strategy = InheritanceType.JOINED)
open class PollItem(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "poll_item_id")
        open val id: Int,

        @Column(nullable = false)
        open val pollId: Int,

        @Column(nullable = false)
        open val question: String,

        @Column(nullable = false)
        open val position: Int
)