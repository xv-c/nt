package main.exceptions

import main.util.ResponseFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
@ResponseBody
class ExceptionsHandler {

    @ExceptionHandler(Exception::class)
    fun handleAll(e: Exception) : ResponseEntity<*>{
        e.printStackTrace()
        return ResponseFactory.fail(e.message!!)
    }

    @ExceptionHandler(RestException::class)
    fun handleServiceException(e: RestException): ResponseEntity<*> {
        e.printStackTrace()
        return ResponseFactory.fail(e.message!!)
    }
}