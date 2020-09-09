package main.controllers

import main.service.ResultService
import main.util.ResponseFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/results")
class ResultController(val resultService: ResultService) {

    @PostMapping("{id}")
    fun postResult(@PathVariable id: String, @RequestParam resultJson:String): ResponseEntity<*> {
        val testResult = resultService.parseResult(resultJson)
        val result = resultService.save(testResult)
        return ResponseFactory.buildResponse("result", result, true, HttpStatus.OK)
    }
}