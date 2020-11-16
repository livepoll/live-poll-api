package de.livepoll.api.repository

import de.livepoll.api.entity.db.Answer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository: JpaRepository<Answer, Int>