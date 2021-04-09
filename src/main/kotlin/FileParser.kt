import com.sun.prism.paint.Color
import domain.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val imagesPath: String = File("src/main/resources/training_images").absolutePath
private val studentPath: String = File("src/main/EVALUATOR/student/blackWhite/").absolutePath

fun getImageDirectories(): List<String> {
    val imagesDirectory = File(imagesPath)
    require(imagesDirectory.exists() && imagesDirectory.isDirectory)
    return imagesDirectory
        .walk()
        .maxDepth(1)
        .filter { it != imagesDirectory }
        .map { it.name }
        .toList()
}

fun getTestImageFromDirectory(directoryName: String): Image {
    val imagePath = "$imagesPath/$directoryName/Test image.jpg"
    val bufferedImage = ImageIO.read(File(imagePath))
    return Image(bufferedImage)
}

fun writeImageToFile(filename: String, image: Image) {
    val imagePath = "$studentPath/$filename.jpg"
    val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            bufferedImage.setRGB(x, y, image[x, y])
        }
    }
    ImageIO.write(bufferedImage, "jpg", File(imagePath))
}