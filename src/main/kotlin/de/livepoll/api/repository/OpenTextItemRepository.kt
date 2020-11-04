package de.livepoll.api.repository

import de.livepoll.api.entity.db.OpenTextItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenTextItemRepository: JpaRepository<OpenTextItem, Int> {
}