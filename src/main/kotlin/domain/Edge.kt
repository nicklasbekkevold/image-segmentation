package domain

data class Edge(private val from: Int, private val to: Int, private val weight: Float) : Comparable<Edge> {

    override fun compareTo(other: Edge): Int {
        return weight.compareTo(other.weight)
    }
}
