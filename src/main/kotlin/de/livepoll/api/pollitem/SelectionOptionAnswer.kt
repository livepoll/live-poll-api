package de.livepoll.api.pollitem

interface SelectionOptionAnswer{
    val id: Long
    var answerCount: Int
    val selectionOption: String

}