package ga

enum class ObjectiveFunctions {
    overallDeviation {
        // minimize
        override fun apply(C: Set<kotlin.Int>): Float {
            TODO("Not yet implemented")
        }
    },
    edgeValue {
        // maximize
        override fun apply(C: Set<kotlin.Int>): Float {
            TODO("Not yet implemented")
        }

    },
    connectivityMeasure {
        // minimize
        override fun apply(C: Set<kotlin.Int>): Float {
            TODO("Not yet implemented")
        }
    };
    abstract fun apply(C: Set<Int>): Float
}