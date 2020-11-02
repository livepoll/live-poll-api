package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name="Multiple_Choice_Item")
data class MultipleChoiceItem(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var pollId: Int,

        @NotNull
        var position: Int,

        @NotNull
        var question: String,

        @NotNull
        @OneToMany(mappedBy="pollItemId")
        val answers: List<Answer>
) {

}