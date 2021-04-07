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
        var id: Int,

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "user_id")
        var user: User,

        @Column(nullable = false)
        var name: String,

        var startDate: Date,

        var endDate: Date,

        var slug: String,

        @JsonIgnore
        @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL])
        var pollItems: MutableList<PollItem>
)
