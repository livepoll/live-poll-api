package de.livepoll.api.repository

import de.livepoll.api.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository

interface AnswerRepository: JpaRepository<Answer, Int> {
}