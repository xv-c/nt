package main.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RestResponse(body: HashMap<String, Any>, status: HttpStatus) :
    ResponseEntity<HashMap<String, Any>>(body, status) {
    var success: Boolean = body["success"] as Boolean
    var message: String = body["message"] as String
    var data: HashMap<String, Any> = body["data"] as HashMap<String, Any>

}

object ResponseFactory {
    fun fail(message: String): RestResponse {
        return build(arrayOf(), arrayOf(), message, false, HttpStatus.BAD_REQUEST)
    }

    fun ok(): RestResponse {
        return ok(arrayOf(), arrayOf())
    }

    fun ok(objectName: String, `object`: Any): RestResponse {
        return ok(arrayOf(objectName), arrayOf(`object`))
    }

    fun ok(objectNames: Array<String>, objects: Array<Any>): RestResponse {
        return build(objectNames, objects, "", true, HttpStatus.OK)
    }

    private fun build(objectNames: Array<String>, objects: Array<Any>, message: String, success: Boolean, status: HttpStatus): RestResponse {
        return RestResponse(createBody(success, message, objectNames, objects), status)
    }

    private fun createBody(success: Boolean, message: String, objectNames: Array<String>, objects: Array<Any>): HashMap<String, Any> {
        val body = HashMap<String, Any>()
        body["success"] = success
        body["message"] = message
        body["data"] = getData(objectNames, objects)
        return body
    }

    private fun getData(objectNames: Array<String>, objects: Array<Any>): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in objects.indices)
            map[objectNames[i]] = objects[i]

        return map
    }
}