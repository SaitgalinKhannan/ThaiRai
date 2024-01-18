package com.khannan.thaiboard.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.NOT_FOUND)
class UserIsNotOwnerException(message: String?) : RuntimeException(message)