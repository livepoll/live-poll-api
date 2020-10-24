package de.livepoll.api.controller

import de.livepoll.api.entity.Poll
import de.livepoll.api.repository.PollRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PollController {

    @Autowired
    lateinit var pollRepository: PollRepository

    @GetMapping("/polls")
    fun getAllPolls(): List<Poll>{
        val pollsList: List<Poll> = pollRepository.findAll();
        return pollsList;
    }
}