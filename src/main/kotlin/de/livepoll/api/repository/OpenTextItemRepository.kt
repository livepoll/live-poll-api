package de.livepoll.api.repository

import de.livepoll.api.entity.db.OpenTextItem
import javax.transaction.Transactional

@Transactional
interface OpenTextItemRepository : PollItemRepository<OpenTextItem>
