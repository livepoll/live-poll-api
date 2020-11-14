package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name = "open_Text_Item")
data class OpenTextItem(
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="open_text_item_id", nullable = false)
        var id: Int,

        @Column(nullable = false)
        var pollId: Int,

        @Column(nullable = false)
        var position: Int,

        @Column(nullable = false)
        var question: String
)