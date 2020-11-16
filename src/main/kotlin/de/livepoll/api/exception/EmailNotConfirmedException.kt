package de.livepoll.api.exception

class EmailNotConfirmedException(
        override val message: String
) : Exception()