package de.livepoll.api.repository

import de.livepoll.api.entity.db.MultipleChoiceItem
import javax.transaction.Transactional

@Transactional
interface MultipleChoiceItemRepository : PollItemRepository<MultipleChoiceItem>
