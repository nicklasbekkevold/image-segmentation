package ga

import domain.Image
import domain.index
import domain.vonNeumannNeighborhood
import sun.security.provider.certpath.AdjacencyList
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

typealias Node = Int

private val nodes = (0 until Image.size).toList()

data class PSR_MST(val genotype: List<Gene>, val edgesList: List<Pair<Node, Node>>, val weightsList: List<Float>)

fun minimumSpanningTree(): PSR_MST {
    val genotype = MutableList(Image.size) { Gene.NONE }

    val keys = FloatArray(nodes.size) { Float.MAX_VALUE }
    val cheapestNodes = PriorityQueue<Node>(compareBy { keys[it] })
    val edgesList = mutableListOf<Pair<Node, Node>>()
    val weightsList = mutableListOf<Float>()

    val root = nodes.random()
    keys[root] = 0f
    cheapestNodes.addAll(nodes)

    while (cheapestNodes.isNotEmpty()) {
        val u = cheapestNodes.remove()
        for ((v, edge) in u.vonNeumannNeighborhood) {
            if (v in cheapestNodes && Image.w(u, v) < keys[v]) {
                genotype[v] = edge.opposite()
                keys[v] = Image.w(u, v)
                edgesList.add(Pair(u, v))
                weightsList.add(Image.w(u, v))
            }
        }
    }
    return sequentialClustering(PSR_MST(genotype, edgesList, weightsList))
}

fun sequentialClustering(PSR_MST: PSR_MST): PSR_MST {
    val (_genotype, edgesList, weightsList) = PSR_MST
    val genotype = mutableListOf<Gene>().also { it.addAll(_genotype) }

    val l = (sqrt(Image.size.toFloat()) / 2f).toInt()
    var sum = 0f
    for (i in 1 until weightsList.size) {
        val diff = abs(weightsList[i - 1] - weightsList[i])
        sum += diff
    }
    val c = sum / (weightsList.size - 1)
    for (i in l until edgesList.size - l) {
        val max1 = weightsList.subList(i-l, i-1).maxOf { it }
        val max2 = weightsList.subList(i+1, i+l).maxOf { it }

        if (weightsList[i] > min(max1, max2) + c) {
            val (_, v) = edgesList[i]
            genotype[v] = Gene.NONE
        }
    }
    return PSR_MST(genotype, edgesList, weightsList)
}

fun makeSegments(genotype: List<Gene>): List<Int> {
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



