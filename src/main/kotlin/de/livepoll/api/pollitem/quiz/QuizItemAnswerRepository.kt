package de.livepoll.api.pollitem.quiz

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface QuizItemAnswerRepository : JpaRepository<QuizItemAnswer, Long>
