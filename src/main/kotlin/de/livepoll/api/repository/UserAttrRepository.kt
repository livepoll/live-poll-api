package de.livepoll.api.repository

import de.livepoll.api.entity.UserAttr
import org.springframework.data.jpa.repository.JpaRepository

interface UserAttrRepository: JpaRepository<UserAttr, Int> {

}