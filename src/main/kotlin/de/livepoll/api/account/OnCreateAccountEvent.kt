package de.livepoll.api.account

import de.livepoll.api.user.User
import org.springframework.context.ApplicationEvent

class OnCreateAccountEvent(
    var user: User,
    var appUrl: String
): ApplicationEvent(user)