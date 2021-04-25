import domain.Image
import ga.Individual
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

private val imagesPath: String = File("src/main/resources/training_images").absolutePath
private val optimalPath: String = File("src/main/scripts/Optimal_Segmentation_Files").absolutePath
private val studentPath: String = File("src/main/scripts/Student_Segmentation_Files").absolutePath

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

fun getTestImageFromDirectory(directoryName: String): BufferedImage {
    val imagePath = "$imagesPath/$directoryName/Test image.jpg"
    return ImageIO.read(File(imagePath))
}

fun copyGroundTruthImages(directoryName: String) {
    val imageDirectory = "$imagesPath/$directoryName"
    for (file in Objects.requireNonNull(File(imageDirectory).listFiles())) {
        if (file.name.contains("GT_")) {
            file.copyTo(File("$optimalPath/${file.name}"))
        }
    }
}

fun deleteImages() {
    for (file in Objects.requireNonNull(File(optimalPath).listFiles())) {
        if (!file.isDirectory) {
            file.delete()
        }
    }
    for (file in Objects.requireNonNull(File(studentPath).listFiles())) {
        if (!file.isDirectory) {
            file.delete()
        }
    }
}

fun writeBlackAndWhiteImageToFile(filename: String, individual: Individual) {
    val imagePath = "$studentPath/${filename}_BLACKANDWHITE.jpg"
    val bufferedImage = BufferedImage(Image.width, Image.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until Image.height) {
        for (x in 0 until Image.width) {
            if (individual.edgeAt(x, y)) {
                bufferedImage.setRGB(x, y, Color.BLACK.rgb)
            } else {
                bufferedImage.setRGB(x, y, Color.WHITE.rgb)
            }
        }
    }
    ImageIO.write(bufferedImage, "jpg", File(imagePath))
}

fun writeGreenEdgeImageToFile(filename: String, individual: Individual) {
    val imagePath = "$studentPath/${filename}_GREEN.jpg"
    val bufferedImage = Image.image.deepCopy()
    for (y in 0 until Image.height) {
        for (x in 0 until Image.width) {
            if (individual.edgeAt(x, y)) {
                bufferedImage.setRGB(x, y, Color.GREEN.rgb)
            }
        }
    }
    ImageIO.write(bufferedImage, "jpg", File(imagePath))
}

fun BufferedImage.deepCopy(): BufferedImage {
    val colorModel = this.colorModel;
    val isAlphaPremultiplied = colorModel.isAlphaPremultiplied;
    val raster = this.copyData(null);
    return BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
}