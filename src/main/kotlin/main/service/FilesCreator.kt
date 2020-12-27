package main.service

import main.repo.TestRepo
import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.SlideLayout
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.springframework.stereotype.Service
import java.awt.Dimension
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

@Service
class FilesCreator(var testService: TestService) {

    fun createPptx(key: String, images: MutableList<InputStream>): ByteArray {

        val test = testService.getTest(key)

        val example = ImageIO.read(images[0])
        val slideWidth = example.width / 1.33
        val slideHeight = example.height / 1.33
        val back = ByteArrayOutputStream()
        ImageIO.write(example, "png", back)
        images[0] = ByteArrayInputStream(back.toByteArray())

        val ppt = XMLSlideShow()
        ppt.pageSize = Dimension(slideWidth.toInt(), slideHeight.toInt())
        val master = ppt.slideMasters[0]

        for (image in images) {
            val slide = ppt.createSlide(master.getLayout(SlideLayout.BLANK))
            slide.createPicture(ppt.addPicture(image, PictureData.PictureType.PNG))
        }
        val output = ByteArrayOutputStream()
        ppt.write(output)
        return output.toByteArray()
    }
}