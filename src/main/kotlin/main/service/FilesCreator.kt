package main.service

import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.SlideLayout
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Service
class FilesCreator {
    fun createPptx(images: List<InputStream>): ByteArray {
        val ppt = XMLSlideShow()
        val master = ppt.slideMasters[0]

        for (image in images){
            val slide = ppt.createSlide(master.getLayout(SlideLayout.BLANK))
            val pic = ppt.addPicture(image, PictureData.PictureType.PNG)
            slide.createPicture(pic)
        }
        val output = ByteArrayOutputStream()
        ppt.write(output)
        return output.toByteArray()
    }
}