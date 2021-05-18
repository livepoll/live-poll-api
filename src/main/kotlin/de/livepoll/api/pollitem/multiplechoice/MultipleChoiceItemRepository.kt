package de.livepoll.api.pollitem.multiplechoice

import de.livepoll.api.pollitem.PollItemRepository
import javax.transaction.Transactional

@Transactional
interface MultipleChoiceItemRepository: PollItemRepository<MultipleChoiceItem>