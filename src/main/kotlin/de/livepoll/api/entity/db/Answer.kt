package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="Answer")
class Answer(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var pollItemId: Int,

        @NotNull
        var answer: String
) {

}