package de.livepoll.api.entity

import de.livepoll.api.repository.PollRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PollTest(
        val entityManager: TestEntityManager,
        val pollRepository: PollRepository
) {
    @Test
    @Throws
    fun testFind() {
        val poll = pollRepository.getOne(1)
        assertNotNull(poll)
    }

}