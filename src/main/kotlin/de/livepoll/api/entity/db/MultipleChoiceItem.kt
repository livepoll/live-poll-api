package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="Multiple_Choice_Item")
class MultipleChoiceItem(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var position: Int,

        @NotNull
        var question: String
) {

}