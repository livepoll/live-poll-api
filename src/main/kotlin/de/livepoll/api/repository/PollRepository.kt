package de.livepoll.api.repository

import de.livepoll.api.entity.db.Poll
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PollRepository: JpaRepository<Poll, Int>