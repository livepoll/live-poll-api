package de.livepoll.api.controller

import de.livepoll.api.entity.db.User
import de.livepoll.api.util.toDtoOut
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(value = "User", description = "User-related information", tags = ["User"])
@RequestMapping("/v1/user")
class UserController {

    @ApiOperation(value = "Get current user", tags = ["User"])
    @GetMapping
    fun getCurrentUser(@AuthenticationPrincipal user: User): ResponseEntity<*> {
        return ResponseEntity.ok().body(user.toDtoOut())
    }

}
