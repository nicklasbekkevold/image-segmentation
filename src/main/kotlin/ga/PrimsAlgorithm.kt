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

private val scale_l = (sqrt(Image.size.toFloat()) / 2f).toInt()
private val nodes = (0 until Image.size).toList()

data class PrimsSequentialRepresentation(val edgesList: List<Pair<Node, Node>>, val weightsList: List<Float>)
data class MinimumSpanningTree(val adjacencyList: List<MutableList<Node>>, val genotype: List<Gene>)
data class PSR_MST(val PSR: PrimsSequentialRepresentation, val MST: MinimumSpanningTree)

fun minimumSpanningTree(): PSR_MST {
    val genotype = MutableList(Image.size) { Gene.NONE }

    val keys = FloatArray(nodes.size) { Float.MAX_VALUE }
    val adjacencyList = List(Image.size) { mutableListOf<Node>() }
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
                adjacencyList[u].add(v)
                adjacencyList[v].add(u)
                keys[v] = Image.w(u, v)
                edgesList.add(Pair(v, u))
                weightsList.add(Image.w(u, v))
            }
        }
    }
    return PSR_MST(PrimsSequentialRepresentation(edgesList, weightsList), MinimumSpanningTree(adjacencyList, genotype))
}

fun sequentialClustering(PSR_MST: PSR_MST, l: Int = scale_l): IntArray {
    val (PSR, MST) = PSR_MST
    val (edgesList, weightsList) = PSR
    val (adjacencyList, _genotype) = MST
    val genotype = mutableListOf<Gene>().also { it.addAll(_genotype) }
    val segments = IntArray(Image.size)

    var sum = 0f
    for (i in 1 until weightsList.size) {
        val diff = abs(weightsList[i + 1] - weightsList[i])
        sum += diff
    }
    val c = sum / (weightsList.size - 1)
    var segmentId = 1
    for (i in l until edgesList.size - l) {
        val max1 = weightsList.subList(i-l, i-1).maxOf { it }
        val max2 = weightsList.subList(i+1, i+l).maxOf { it }

        if (weightsList[i] > min(max1, max2) + c) {
            val (v, u) = edgesList[i]
            labelNodes(segments, adjacencyList, u, v, segmentId)
            segmentId++
            // Cut
            adjacencyList[u].remove(v)
            adjacencyList[v].remove(u)
            genotype[v] = Gene.NONE
        }
    }
    return segments
}

fun labelNodes(segments: IntArray, adjacencyList: List<MutableList<Node>>, u: Node, v: Node, segmentId: Int) {
    segments[u] = segmentId
    for (neighbour in adjacencyList[u]) {
        if (neighbour != v) {
            labelNodes(segments, adjacencyList, neighbour, u, segmentId)
        }
    }
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



