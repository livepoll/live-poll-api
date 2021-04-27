package de.livepoll.api.repository

import de.livepoll.api.entity.db.OpenTextItemAnswer
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface OpenTextItemAnswerRepository : JpaRepository<OpenTextItemAnswer, Long>
