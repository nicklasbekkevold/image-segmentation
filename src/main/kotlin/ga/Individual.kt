package ga

import domain.Image
import kotlin.random.Random

class Individual(private val genotype: List<Gene>) : Comparable<Individual> {

    constructor() : this(primsAlgorithm())

    val phenotype: List<Int> = getSegments(genotype)
    private val objectiveValues = mutableMapOf<ObjectiveFunction, Float>()

    var rank = 0
    var crowdingDistance = 0f

    // Just for fun. Renames .indices to .loci to match the nomenclature
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
                    correctedGenotype[i] = Gene.NONE
                }
            }
            for (i in 0 until Image.height) {
                if (genotype[i * Image.width] == Gene.LEFT) {
                    correctedGenotype[i] = Gene.NONE
                }
                if (genotype[(i + 1) * Image.width - 1] == Gene.RIGHT) {
                    correctedGenotype[i] = Gene.NONE
                }
            }
            return correctedGenotype
        }
    }

    fun getOrEvaluate(objectiveFunction: ObjectiveFunction): Float {
        return objectiveValues.computeIfAbsent(objectiveFunction) {  objectiveFunction.apply(this) }
    }

    fun compareToWith(other: Individual, objectiveFunction: ObjectiveFunction): Int {
        return this.getOrEvaluate(objectiveFunction).compareTo(other.getOrEvaluate(objectiveFunction))
    }

    fun crossoverAndMutate(other: Individual, crossoverRate: Float, mutationRate: Float): Pair<Individual, Individual> {
        require(genotype.size == other.genotype.size)
        val thisOffspringGenotype = mutableListOf<Gene>().also { it.addAll(genotype) }
        val otherOffspringGenotype = mutableListOf<Gene>().also { it.addAll(other.genotype) }
        if (Random.nextFloat() < crossoverRate) {
            val crossoverPoint = 1 + Random.nextInt(genotype.size - 1)
            for (locus in crossoverPoint until genotype.size) {
                thisOffspringGenotype[locus] = other.genotype[locus].also { otherOffspringGenotype[locus] = genotype[locus] }
                if (Random.nextFloat() < mutationRate) thisOffspringGenotype[locus] = Gene.values().random()
                if (Random.nextFloat() < mutationRate) otherOffspringGenotype[locus] = Gene.values().random()
            }
        } else {
            for (locus in genotype.loci) {
                if (Random.nextFloat() < mutationRate) thisOffspringGenotype[locus] = Gene.values().random()
                if (Random.nextFloat() < mutationRate) otherOffspringGenotype[locus] = Gene.values().random()
            }
        }
        return Pair(Individual(correctBorderNodes(thisOffspringGenotype)), Individual(correctBorderNodes(otherOffspringGenotype)))
    }

    infix fun dominates(other: Individual): Boolean {
        return objectiveValues
            .filter { it.value <= other.getOrEvaluate(it.key) }
            .any { it.value < other.getOrEvaluate(it.key) }
    }

    // Crowded-Comparison operator
    override fun compareTo(other: Individual): Int {
        return compareBy<Individual> { it.rank }
            .thenByDescending { it.crowdingDistance } // high crowding distance means less crowded
            .compare(this, other)
    }

    override fun toString(): String {
        return "Individual(rank=$rank, segments=${phenotype.toSet()})"
    }

}