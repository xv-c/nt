package main.controllers

import main.service.FilesCreator
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import javax.servlet.http.HttpServpletResponse

@RestController
@RequestMapping("api/files")
class FileController(var filesCreator: FilesCreator) {

    @PostMapping
    fun getPptx(@RequestParam key : String, @RequestParam(name = "images[]") images: Array<MultipartFile>, response: HttpServletResponse): HttpEntity<ByteArray> {
        val pptx = filesCreator.createPptx(key, images.map { it.inputStream } as MutableList<InputStream>)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers["Content-Disposition"] = "attachment; filename=presentation.pptx"

        return HttpEntity(pptx, headers)
    }
}