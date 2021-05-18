package de.livepoll.api.pollitem.quiz

import de.livepoll.api.pollitem.PollItemRepository
import javax.transaction.Transactional

@Transactional
interface QuizItemRepository : PollItemRepository<QuizItem>
