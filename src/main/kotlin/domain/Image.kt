package domain

import ga.Gene
import getTestImageFromDirectory
import imageDirectory
import kotlin.math.pow
import kotlin.math.sqrt

val Int.neighbors: List<Int>
    get() {
        return Gene.values().map { it + Image.indexToCoordinate(this) }.filter { (it.first in 0..Image.width) and (it.second in 0..Image.height) }.map { it.first + it.second * Image.width }
    }


val Pair<Int, Int>.neighbors: List<Pair<Int, Int>>
    get() {
        return Gene.values().map { it + this }.filter { (it.first in 0..Image.width) and (it.second in 0..Image.height) }
    }

val Pair<Int, Int>.index: Int
    get() {
        return this.first + this.second * Image.width
    }

object Image: Iterable<Int> {

    val image = getTestImageFromDirectory(imageDirectory)
    val width = image.width
    val height = image.height
    val size = width * height
    private val pixels = Array(height) { Array(width) { 0 } }

    init {
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y][x] = image.getRGB(x, y)
            }
        }
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

    fun colorDistanceRGB(intRGB: Int, other: Int): Float {
        val (r1, g1, b1) = intToRGB(intRGB).map { it.toFloat() }
        val (r2, g2, b2) = intToRGB(other).map { it.toFloat() }
        return sqrt((r1 - r2).pow(2) + (g1 - g2).pow(2) + (b1 - b2).pow(2))
    }

    fun intToRGB(intRGB: Int): List<Int> {
        return listOf((intRGB shr 16) and 255, (intRGB shr 8) and 255, (intRGB) and 255)
    }

    fun RGBToInt(red: Int, green: Int, blue: Int): Int {
        return ((red and 255) shl 16) or ((green and 255) shl 8) or (blue and 255)
    }

    override fun iterator(): Iterator<Int> {
        return pixels.flatten().iterator()
    }
}