package ga

import domain.Image
import domain.index
import domain.vonNeumannNeighborhood
import imageDirectory
import java.util.*
import kotlin.random.Random

typealias Node = Int

private val nodes = (0 until Image.size).toList()

fun primsAlgorithm(): List<Gene> {
    val genotype = MutableList(Image.width * Image.height) { Gene.NONE }

    val keys: MutableMap<Node, Float> = mutableMapOf<Node, Float>().withDefault { Float.MAX_VALUE }
    val predecessors: MutableMap<Node, Node> = mutableMapOf()
    val Q: PriorityQueue<Node> = PriorityQueue(compareBy { keys.getValue(it) })

    val root = nodes.random()
    keys[root] = 0f
    Q.addAll(nodes)

    while (Q.isNotEmpty()) {
        val u = Q.remove()
        for ((v, edge) in u.vonNeumannNeighborhood) {
            if (v in Q && Image.w(u, v) < keys.getValue(v)) {
                genotype[v] = edge.opposite()
                predecessors[v] = u
                keys[v] = Image.w(u, v)
            }
        }
    }
    return genotype
}

fun getSegments(genotype: List<Gene>): List<Int> {
    val segments = MutableList(genotype.size) { -1 }
    var segmentId = 0

    for (i in genotype.indices) {
        if (segments[i] != -1) {
            continue
        }

        val currentSegment = mutableListOf<Int>()
        segments[i] = segmentId

        var currentNode = genotype[i] + Image.indexToCoordinate(i)
        while (segments[currentNode.index] == -1) {
            currentSegment.add(currentNode.index)
            segments[currentNode.index] = segmentId
            currentNode = genotype[currentNode.index] + currentNode
        }

        if (segments[i] != segments[currentNode.index]) {
            val segment = segments[currentNode.index]
            for (node in currentSegment) {
                segments[node] = segment
            }
        } else {
            segmentId++
        }
    }
    return segments
}
