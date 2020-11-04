package de.livepoll.api.repository

import de.livepoll.api.entity.db.QuizItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizItemRepository: JpaRepository<QuizItem, Int> {
}