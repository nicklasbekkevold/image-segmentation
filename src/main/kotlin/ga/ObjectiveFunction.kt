package ga

import domain.Image
import domain.mooreNeighborhood
import domain.vonNeumannNeighborhood

enum class ObjectiveFunction {
    OverallDeviation {
        // minimize
        override fun apply(segments: List<Int>): Float {
            val segmentIds = segments.toSet().toList().sorted()
            val reds = MutableList(segmentIds.size) { 0 }
            val blues = MutableList(segmentIds.size) { 0 }
            val greens = MutableList(segmentIds.size) { 0 }
            val counts = MutableList(segmentIds.size) { 0 }
            for (i in 0 until Image.size) {
                val (r, g, b) = Image.intToRGB(Image[i])
                reds[segments[i]] += r
                blues[segments[i]] += g
                greens[segments[i]] += b
                counts[segments[i]]++
            }
            val centroids = segmentIds.map { Image.RGBToInt(reds[it] / counts[it], greens[it] / counts[it], blues[it] / counts[it]) }
            return Image
                .mapIndexed { index, pixel -> Image.colorDistanceRGB(pixel, centroids[segments[index]]) }
                .reduce { accumulator, distance -> accumulator  + distance }
        }
    },
    EdgeValue {
        // minimize
        override fun apply(segments: List<Int>): Float {
            var edgeValue = 0f
            for (pixel in 0 until Image.size) {
                for ((pixelNeighbor, _) in pixel.vonNeumannNeighborhood) {
                    edgeValue -= (if (segments[pixel] != segments[pixelNeighbor]) Image.w(pixel, pixelNeighbor) else 0f)
                }
            }
            return edgeValue
        }

    },
    ConnectivityMeasure {
        // minimize
        override fun apply(segments: List<Int>): Float {
            var connectivity = 0f
            for (pixel in 0 until Image.size) {
                var notConnectedNeighbours = 0
                for (pixelNeighbor in pixel.mooreNeighborhood) {
                    connectivity += (if (segments[pixel] != segments[pixelNeighbor]) 1f / ++notConnectedNeighbours else 0f)
                }
            }
            return connectivity
        }
    };
//    ,
//    NumberOfSegments {
//        // minimize
//        override fun apply(segments: List<Int>): Float {
//            return segments.toSet().size.toFloat()
//        }
//    };

    abstract fun apply(segments: List<Int>): Float
}