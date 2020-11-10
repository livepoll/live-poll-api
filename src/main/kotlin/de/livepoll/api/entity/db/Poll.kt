package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "poll")
data class Poll(
        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "user_id")
        var user: User,

        var name: String,

        var startDate: Date,

        var endDate: Date,

        @OneToMany(mappedBy = "pollId")
        var pollItems: List<MultipleChoiceItem> = emptyList(),

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "poll_id")
        var id: Int = 0
)
