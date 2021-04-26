package de.livepoll.api.repository

import de.livepoll.api.entity.db.PollItem
import org.springframework.data.jpa.repository.JpaRepository

interface PollItemRepository<T> : JpaRepository<T, Long> where T : PollItem
