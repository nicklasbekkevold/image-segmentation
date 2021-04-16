package ga

import domain.Image
import domain.mooreNeighborhood
import domain.vonNeumannNeighborhood

enum class ObjectiveFunction {
    overallDeviation {
        // minimize
        override fun apply(individual: Individual): Float {
            val segments = individual.phenotype.toSet().toList().sorted()
            val reds = MutableList(segments.size) { 0 }
            val blues = MutableList(segments.size) { 0 }
            val greens = MutableList(segments.size) { 0 }
            val counts = MutableList(segments.size) { 0 }
            for (i in 0 until Image.size) {
                val (r, g, b) = Image.intToRGB(Image[i])
                reds[individual.phenotype[i]] += r
                blues[individual.phenotype[i]] += g
                greens[individual.phenotype[i]] += b
                counts[individual.phenotype[i]]++
            }
            val centroids = segments.map { Image.RGBToInt(reds[it] / counts[it], greens[it] / counts[it], blues[it] / counts[it]) }
            return Image
                .mapIndexed { index, pixel -> Image.colorDistanceRGB(pixel, centroids[index]) }
                .reduce { accumulator, distance -> accumulator  + distance }
        }
    },
    edgeValue {
        // minimize
        override fun apply(individual: Individual): Float {
            var edgeValue = 0f
            for (pixel in Image) {
                for ((neighbor, _) in pixel.vonNeumannNeighborhood) {
                    edgeValue -= (if (individual.phenotype[pixel] != individual.phenotype[neighbor]) Image.w(pixel, neighbor) else 0f)
                }
            }
            return edgeValue
        }

    },
    connectivityMeasure {
        // minimize
        override fun apply(individual: Individual): Float {
            var connectivity = 0f
            for (pixel in Image) {
                var notConnectedNeighbours = 0
                for (neighbor in pixel.mooreNeighborhood) {
                    connectivity += (if (individual.phenotype[pixel] != individual.phenotype[neighbor]) 1f / ++notConnectedNeighbours else 0f)
                }
            }
            return connectivity
        }
    };
    abstract fun apply(individual: Individual): Float
}