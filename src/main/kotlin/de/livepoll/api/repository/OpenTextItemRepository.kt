package de.livepoll.api.repository

import de.livepoll.api.entity.db.OpenTextItem
import org.springframework.data.jpa.repository.JpaRepository

interface OpenTextItemRepository: JpaRepository<OpenTextItem, Int> {
}