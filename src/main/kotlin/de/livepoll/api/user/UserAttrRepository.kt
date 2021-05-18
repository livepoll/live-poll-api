package de.livepoll.api.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAttrRepository: JpaRepository<UserAttr, Int>