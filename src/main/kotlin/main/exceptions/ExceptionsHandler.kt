package main.exceptions

import main.util.ResponseFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
@ResponseBody
class ExceptionsHandler {

    @ExceptionHandler(Throwable::class)
    fun handleAll(e: Throwable) : ResponseEntity<*>{
        return ResponseFactory.fail(e.message)
    }

    @ExceptionHandler(RestException::class)
    fun handleServiceException(e: RestException): ResponseEntity<*> {
        return ResponseFactory.fail(e.message)
    }
}