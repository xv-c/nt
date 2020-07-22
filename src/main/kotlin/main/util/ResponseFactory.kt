package main.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

object ResponseFactory {
    fun buildUnsuccessfulResponse(message: String?): ResponseEntity<*> {
        return buildResponse(message, false, HttpStatus.BAD_REQUEST)
    }

    fun buildResponse(message: String?, success: Boolean, status: HttpStatus?): ResponseEntity<*> {
        return ResponseEntity<Any?>(getBody(success, message, null as String?, null), status!!)
    }

    fun buildResponse(objectNames: Array<String>, objects: Array<Any>, success: Boolean, status: HttpStatus?): ResponseEntity<*> {
        return ResponseEntity<Any?>(getBody(success, "", objectNames, objects), status!!)
    }

    fun buildResponse(objectName: String?, `object`: Any?, success: Boolean, status: HttpStatus?): ResponseEntity<*> {
        return ResponseEntity<Any?>(getBody(success, "", objectName, `object`), status!!)
    }

    fun buildResponse(body: Map<String, Any>, success: Boolean, status: HttpStatus?): ResponseEntity<*> {
        return ResponseEntity<Any?>(getBody(success, "", body), status!!)
    }

    private fun getBody(success: Boolean, message: String, data: Map<String, Any>): Map<*, *> {
        val body = HashMap<Any, Any>()
        body["success"] = success
        body["message"] = message
        body["data"] = data
        return body
    }

    private fun getBody(success: Boolean, message: String?, objectName: String?, `object`: Any?): Map<*, *> {
        val body = HashMap<Any, Any?>()
        body["success"] = success
        body["message"] = message
        body["data"] = getData(objectName, `object`)
        return body
    }

    private fun getBody(success: Boolean, message: String, objectNames: Array<String>, objects: Array<Any>): Map<*, *> {
        val body = HashMap<Any, Any>()
        body["success"] = success
        body["message"] = message
        body["data"] = getData(objectNames, objects)
        return body
    }

    private fun getData(objectNames: Array<String>, objects: Array<Any>): Map<*, *> {
        val map = HashMap<Any, Any>()
        for (i in objects.indices) {
            map.putAll(getData(objectNames[i], objects[i]) as Map<out Any, Any>)
        }
        return map
    }

    private fun getData(objectName: String?, `object`: Any?): Map<*, *> {
        val data = HashMap<Any, Any>()
        if (objectName != null && `object` != null)
            data[objectName] = `object`
        return data
    }
}