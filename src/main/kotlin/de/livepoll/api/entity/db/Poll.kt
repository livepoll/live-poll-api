package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "poll")
data class Poll(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "poll_id", nullable = false)
        var id: Long,

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "user_id")
        var user: User,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = true)
        var startDate: Date?,

        @Column(nullable = true)
        var endDate: Date?,

        var slug: String,

        @Column(nullable = true)
        var currentItem : Long?,

        @JsonIgnore
        @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
        var pollItems: MutableList<PollItem>
)
