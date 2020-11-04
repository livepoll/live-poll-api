package de.livepoll.api.repository

import de.livepoll.api.entity.db.MultipleChoiceItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MultipleChoiceItemRepository: JpaRepository<MultipleChoiceItem, Int> {
}