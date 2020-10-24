package de.livepoll.api.repository

import de.livepoll.api.entity.QuizItem
import org.springframework.data.jpa.repository.JpaRepository

interface QuizItemRepository: JpaRepository<QuizItem, Int> {
}