package ga

import connectivityMeasureWeight
import domain.Image
import domain.ObjectiveFunction
import domain.index
import domain.twoConnectedNeighborhood
import edgeValueWeight
import initialization
import overallDeviationWeight
import kotlin.random.Random

class Individual(private val genotype: List<Gene>, val phenotype: List<Int>) : Comparable<Individual> {

    constructor(genotype: List<Gene>) : this(genotype, makePhenotype(genotype))
    constructor() : this(makeGenotype(initialization))

    private val objectiveValues = mutableMapOf<ObjectiveFunction, Float>()

    private var fitness = Float.MAX_VALUE // inverted fitness for this problem
    var rank = Int.MAX_VALUE
    var crowdingDistance = Float.MIN_VALUE
    var dominationCount = 0
    var dominates = mutableListOf<Individual>()

    fun copy(): Individual {
        return Individual(genotype, phenotype)
    }

    // Just for fun. Renames .indices to .loci to match the nomenclature. Low is good
    private val List<Gene>.loci: IntRange
        get() = this.indices

    companion object {
        fun correctBorderNodes(genotype: List<Gene>): List<Gene> {
            val correctedGenotype = genotype.toMutableList()
            for (i in 0 until Image.width) {
                if (genotype[i] == Gene.UP) {
                    correctedGenotype[i] = Gene.NONE
                }
                if (genotype[(Image.height - 1) * Image.width + i] == Gene.DOWN) {
                    correctedGenotype[(Image.height - 1) * Image.width + i] = Gene.NONE
                }
            }
            for (i in 0 until Image.height) {
                if (genotype[i * Image.width] == Gene.LEFT) {
                    correctedGenotype[i * Image.width] = Gene.NONE
                }
                if (genotype[(i + 1) * Image.width - 1] == Gene.RIGHT) {
                    correctedGenotype[(i + 1) * Image.width - 1] = Gene.NONE
                }
            }
            return correctedGenotype
        }
    }

    fun edgeAt(x: Int, y: Int): Boolean {
        val pixel = Pair(x, y).index
        for (pixelNeighbor in pixel.twoConnectedNeighborhood) {
            if (phenotype[pixel] != phenotype[pixelNeighbor]) {
                return true
            }
        }
        return false
    }

    fun getOrEvaluate(objectiveFunction: ObjectiveFunction): Float {
        return objectiveValues.computeIfAbsent(objectiveFunction) { objectiveFunction.apply(phenotype) }
    }

    fun computeFitness() {
        fitness = overallDeviationWeight * getOrEvaluate(ObjectiveFunction.OverallDeviation)
            + edgeValueWeight * getOrEvaluate(ObjectiveFunction.EdgeValue)
            + connectivityMeasureWeight * getOrEvaluate(ObjectiveFunction.ConnectivityMeasure)
    }

    fun crossoverAndMutate(other: Individual, crossoverRate: Float, mutationRate: Float): Pair<Individual, Individual> {
        require(genotype.size == other.genotype.size)
        val thisOffspringGenotype = mutableListOf<Gene>().also { it.addAll(genotype) }
        val otherOffspringGenotype = mutableListOf<Gene>().also { it.addAll(other.genotype) }
        if (Random.nextFloat() < crossoverRate) {
            val crossoverPoint = 1 + Random.nextInt(genotype.size - 1)
            for (locus in crossoverPoint until genotype.size) {
                thisOffspringGenotype[locus] = other.genotype[locus].also { otherOffspringGenotype[locus] = genotype[locus] }
            }
        }
        if (Random.nextFloat() < mutationRate) {
            val locus = Random.nextInt(0, genotype.size)
            thisOffspringGenotype[locus] = Gene.values().random()
        }
        if (Random.nextFloat() < mutationRate) {
            val locus = Random.nextInt(0, genotype.size)
            otherOffspringGenotype[locus] = Gene.values().random()
        }
        return Pair(Individual(correctBorderNodes(thisOffspringGenotype)), Individual(correctBorderNodes(otherOffspringGenotype)))
    }

    infix fun dominates(other: Individual): Boolean {
        return ObjectiveFunction.values()
            .all { this.getOrEvaluate(it) <= other.getOrEvaluate(it) }
            .and(ObjectiveFunction.values().any { this.getOrEvaluate(it) < other.getOrEvaluate(it) })
    }

    fun compareToWith(other: Individual, objectiveFunction: ObjectiveFunction): Int {
        return this.getOrEvaluate(objectiveFunction).compareTo(other.getOrEvaluate(objectiveFunction))
    }

    // Crowded-Comparison operator
    override fun compareTo(other: Individual): Int {
        return compareBy<Individual> { it.rank }
            .thenByDescending { it.crowdingDistance } // high crowding distance means less crowded
            .thenBy { it.fitness } // Not used simultaneous with rank and crowdingDistance
            .compare(this, other)
    }

    override fun toString(): String {
        return "segments=${phenotype.toSet().size}, connectivity=${getOrEvaluate(ObjectiveFunction.ConnectivityMeasure)}, edgeValue=${-getOrEvaluate(
            ObjectiveFunction.EdgeValue)}, overallDeviation=${getOrEvaluate(ObjectiveFunction.OverallDeviation)}"
    }

    fun reset() {
        rank = Int.MAX_VALUE
        crowdingDistance = Float.MIN_VALUE
        dominationCount = 0
        dominates = mutableListOf()
    }

}