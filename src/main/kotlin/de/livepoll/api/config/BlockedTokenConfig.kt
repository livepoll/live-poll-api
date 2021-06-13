package de.livepoll.api.config

import de.livepoll.api.repository.BlockedTokenRepository
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.*

@Configuration
@EnableScheduling
class BlockedTokenConfig(
    val blockedTokenRepository: BlockedTokenRepository
) {

    @Scheduled(cron = "0 0 12 * * ?")
    fun clearBlockedTokens() {
        val blockedTokens = blockedTokenRepository.findAll()
        blockedTokens.forEach {
            if (it.expiryDate.before(Date())) {
                blockedTokenRepository.delete(it)
            }
        }
    }
}
