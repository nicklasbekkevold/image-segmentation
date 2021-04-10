package ga

import domain.Image

enum class ObjectiveFunctions {
    overallDeviation {
        // minimize
        override fun apply(image: Image, C: List<Int>): Float {
            TODO("Not yet implemented")
        }
    },
    edgeValue {
        // maximize
        override fun apply(image: Image, C: List<Int>): Float {
            TODO("Not yet implemented")
        }

    },
    connectivityMeasure {
        // minimize
        override fun apply(image: Image, C: List<Int>): Float {
            TODO("Not yet implemented")
        }
    };
    abstract fun apply(image: Image, C: List<Int>): Float
}