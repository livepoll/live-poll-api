package de.livepoll.api.entity.db

interface SelectionOptionAnswer {
    val id: Long
    var answerCount: Int
    val selectionOption: String
}
