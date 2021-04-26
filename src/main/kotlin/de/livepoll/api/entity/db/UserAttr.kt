package de.livepoll.api.entity.db

import javax.persistence.*

@Entity
@Table(name = "user_attr")
data class UserAttr(
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name="user_attr_id", nullable = false)
        var id: Int,

        @OneToOne
        @JoinColumn(name="user_id")
        var user: User,

        @Column(nullable = false)
        var key1: String,

        @Column(nullable = false)
        var value: String
)