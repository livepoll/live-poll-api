package de.livepoll.api.repository

import de.livepoll.api.entity.db.MultipleChoiceItem
import org.springframework.data.jpa.repository.JpaRepository

interface MultipleChoiceItemRepository: JpaRepository<MultipleChoiceItem, Int> {
}