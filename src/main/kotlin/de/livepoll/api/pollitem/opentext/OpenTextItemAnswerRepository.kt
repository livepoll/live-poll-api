package de.livepoll.api.pollitem.opentext

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface OpenTextItemAnswerRepository : JpaRepository<OpenTextItemAnswer, Long>
