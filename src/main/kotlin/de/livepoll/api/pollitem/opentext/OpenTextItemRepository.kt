package de.livepoll.api.pollitem.opentext

import de.livepoll.api.pollitem.PollItemRepository
import javax.transaction.Transactional

@Transactional
interface OpenTextItemRepository : PollItemRepository<OpenTextItem>
