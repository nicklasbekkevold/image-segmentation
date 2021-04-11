package ga

import domain.Image
import domain.mooreNeighborhood
import domain.vonNeumannNeighborhood

enum class ObjectiveFunctions {
    overallDeviation {
        // minimize
        override fun apply(image: Image, C: List<Int>): Float {
            val segments = C.toSet().toList().sorted()
            val reds = MutableList(segments.size) { 0 }
            val blues = MutableList(segments.size) { 0 }
            val greens = MutableList(segments.size) { 0 }
            val counts = MutableList(segments.size) { 0 }
            for (i in 0 until image.size) {
                val (r, g, b) = Image.intToRGB(image[i])
                reds[C[i]] += r
                blues[C[i]] += g
                greens[C[i]] += b
                counts[C[i]]++
            }
            val centroids = segments.map { Image.RGBToInt(reds[it] / counts[it], greens[it] / counts[it], blues[it] / counts[it]) }
            return image
                .mapIndexed { index, pixel -> Image.colorDistanceRGB(pixel, centroids[index]) }
                .reduce { accumulator, distance -> accumulator  + distance }
        }
    },
    edgeValue {
        // minimize
        override fun apply(image: Image, C: List<Int>): Float {
            var edgeValue = 0f
            for (pixel in image) {
                for (neighbor in pixel.vonNeumannNeighborhood) {
                    edgeValue -= (if (C[pixel] != C[neighbor]) Image.colorDistanceRGB(pixel, neighbor) else 0f)
                }
            }
            return edgeValue
        }

    },
    connectivityMeasure {
        // minimize
        override fun apply(image: Image, C: List<Int>): Float {
            var connectivity = 0f
            for (pixel in image) {
                var notConnectedNeighbours = 0
                for (neighbor in pixel.mooreNeighborhood) {
                    connectivity += (if (C[pixel] != C[neighbor]) 1f / ++notConnectedNeighbours else 0f)
                }
            }
            return connectivity
        }
    };
    abstract fun apply(image: Image, C: List<Int>): Float
}