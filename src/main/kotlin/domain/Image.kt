package domain

import java.awt.Color
import java.awt.image.BufferedImage

class Image(image: BufferedImage) {

    val width = image.width
    val height = image.height
    private val pixels = Array(height) { IntArray(width) }

    init {
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y][x] = image.getRGB(x, y)
            }
        }
    }

    fun intToRGB(intRGB: Int): Triple<Int, Int, Int> {
        return Triple((intRGB shr 16) and 255, (intRGB shr 8) and 255, (intRGB) and 255)
    }

    fun RGBToInt(red: Int, green: Int, blue: Int): Int {
        return ((red and 255) shl 16) or ((green and 255) shl 8) or (blue and 255 )
    }

    operator fun get(index: Int): Int {
        return pixels[index / width][index % width]
    }

    operator fun get(x: Int, y: Int): Int {
        return pixels[y][x]
    }

    fun indexToCoordinate(index: Int): Pair<Int, Int> {
        return Pair(index % width, index / width)  // (x, y)
    }
}