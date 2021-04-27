package de.livepoll.api.repository

import de.livepoll.api.entity.db.QuizItemAnswer
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface QuizItemAnswerRepository : JpaRepository<QuizItemAnswer, Long>
