package de.livepoll.api.repository

import de.livepoll.api.entity.db.QuizItem
import javax.transaction.Transactional

@Transactional
interface QuizItemRepository : PollItemRepository<QuizItem>
