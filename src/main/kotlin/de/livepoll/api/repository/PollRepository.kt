package de.livepoll.api.repository

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.db.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface PollRepository : JpaRepository<Poll, Int> {
    // https://stackoverflow.com/a/37491875/9655481
    @Transactional
    fun deleteByUser(user: User)

    @Transactional
    fun findBySlug(slug: String): Poll?
}