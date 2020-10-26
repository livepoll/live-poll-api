package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="Poll")
class Poll(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var userId: Int,

        @NotNull
        var name:String,

        @NotNull
        var startDate: Date,

        @NotNull
        var endDate: Date
) {

}