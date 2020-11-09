package de.livepoll.api.util

import de.livepoll.api.entity.db.User
import org.springframework.context.ApplicationEvent

class OnCreateAccountEvent(
        var user: User,
        var appUrl: String
): ApplicationEvent(user)