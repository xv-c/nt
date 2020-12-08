package main.controllers

import main.service.FilesCreator
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/files")
class FileController(var filesCreator: FilesCreator) {

    @PostMapping
    fun getPptx(@RequestParam(name = "images[]") images: Array<MultipartFile>, response: HttpServletResponse): HttpEntity<ByteArray> {
        val pptx = filesCreator.createPptx(images.map { it.inputStream })

        Files.write(File("ppt123.pptx").toPath(), pptx)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        response.setHeader("Content-Disposition", "attachment; filename=presentation.pptx")
        return HttpEntity(pptx, headers)
    }
}