package de.livepoll.api.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface PollItemRepository<PollItem, Int> : JpaRepository<PollItem, Int>