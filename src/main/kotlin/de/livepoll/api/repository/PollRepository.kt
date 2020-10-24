package de.livepoll.api.repository

import de.livepoll.api.entity.Poll
import org.springframework.data.jpa.repository.JpaRepository

interface PollRepository: JpaRepository<Poll, Int> {
}