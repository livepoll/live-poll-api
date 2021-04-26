package de.livepoll.api.repository

import de.livepoll.api.entity.db.MultipleChoiceItemAnswer
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface MultipleChoiceItemAnswerRepository : JpaRepository<MultipleChoiceItemAnswer, Long>
