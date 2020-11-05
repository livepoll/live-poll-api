package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import java.util.*
import javax.persistence.*

@Entity
@Table(name="poll")
data class Poll(
        @Id
        @NotNull
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="poll_id")
        var id: Int,

        @NotNull
        @ManyToOne
        @JoinColumn(name="user_id")
        var user: User,

        @NotNull
        var name:String,

        @NotNull
        var startDate: Date,

        @NotNull
        var endDate: Date,

        @NotNull
        @OneToMany(mappedBy="pollId")
        val pollItems: List<MultipleChoiceItem>
)