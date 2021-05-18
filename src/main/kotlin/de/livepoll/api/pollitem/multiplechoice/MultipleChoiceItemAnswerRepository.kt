package de.livepoll.api.pollitem.multiplechoice

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface MultipleChoiceItemAnswerRepository : JpaRepository<MultipleChoiceItemAnswer, Long>
