package de.livepoll.api.repository

import de.livepoll.api.entity.db.OpenTextItem
import org.springframework.stereotype.Repository

@Repository
interface OpenTextItemRepository: PollItemRepository<OpenTextItem, Int>