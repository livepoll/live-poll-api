package de.livepoll.api.controller

import de.livepoll.api.entity.Poll
import de.livepoll.api.entity.User
import de.livepoll.api.repository.PollRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import de.livepoll.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired

@RestController
class helloworld{

    @Autowired
    lateinit var pollRepository: PollRepository

    @GetMapping("/helloworld")
    fun helloworld(): String{
        return "Hello World";
    }

    @GetMapping("/getAllPolls")
    fun getAllPolls(): List<Poll>{
        var pollsList: List<Poll> = pollRepository.findAll();
        return pollsList;
    }
}