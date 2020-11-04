package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name= "user_Attr")
data class UserAttr(
        @Id
        @NotNull
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="user_attr_id")
        var id: Int,

        @NotNull
        @OneToOne
        @JoinColumn(name="user_id")
        var user: User,

        @NotNull
        var key1: String,

        @NotNull
        var value: String
){

}