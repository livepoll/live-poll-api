package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name="open_Text_Item")
data class OpenTextItem(
        @Id
        @NotNull
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="open_text_item_id")
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var position: Int,

        @NotNull
        var question: String
) {
}