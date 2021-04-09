package ga

import domain.Image
import getTestImageFromDirectory
import imageDirectory
import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import java.util.*
import javax.lang.model.type.UnionType
import kotlin.math.*
import kotlin.random.Random

typealias Node = Pair<Int, Int>

object PrimsAlgorithm {

    private val image: Image = getTestImageFromDirectory(imageDirectory)
    private val width: Int = image.width
    private val height: Int = image.height
    private val memoizedWeights: MutableMap<Set<Node>, Float> = mutableMapOf()
    private val nodes = ((0 until width).flatMap { x -> (0 until height).map { y -> Pair(x, y) } })

    init {
        println("Prims algorithm initialized on image $imageDirectory (${width}x${height})")
    }

    fun create(): List<Gene> {
        val genotype = MutableList(width * height) { Gene.NONE }

        val keys: MutableMap<Node, Float> = mutableMapOf<Node, Float>().withDefault { Float.MAX_VALUE }
        val predecessors: MutableMap<Node, Node> = mutableMapOf()
        val Q: PriorityQueue<Node> = PriorityQueue(compareBy { keys.getValue(it) })

        val root = Pair(Random.nextInt(width), Random.nextInt(height))
        keys[root] = 0F
        Q.addAll(nodes)

        while (Q.isNotEmpty()) {
            val u = Q.remove()
            for (edge in Gene.values()) {
                val v = edge + u
                if (v.first in 0..width && v.second in 0..height) {
                    if (v in Q && w(u, v) < keys.getValue(v)) {
                        genotype[v.index] = edge.opposite()
                        predecessors[v] = u
                        keys[v] = w(u, v)
                    }
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

            var currentNode = genotype[i] + image.indexToCoordinate(i)
            while (segments[currentNode.index] == -1) {
                currentSegment.add(currentNode.index)
                segments[currentNode.index] = segmentId
                currentNode = genotype[currentNode.index] + currentNode
            }

            if (segments[i] == segments[currentNode.index]) {
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

    private fun w(u: Node, v: Node): Float {
        return memoizedWeights.computeIfAbsent(setOf(u, v)) { rgbDistance(it) }
    }

    private fun rgbDistance(nodes: Set<Node>): Float {
        val (u, v) = nodes.toList()
        val uColor = image[u.index].toFloat()
        val vColor = image[v.index].toFloat()
        return hypot(uColor, vColor)
    }

    private val Node.index: Int
        get() {
            return this.first + this.second * width  // x + y * width
        }
}
