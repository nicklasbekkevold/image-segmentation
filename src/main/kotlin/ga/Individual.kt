package ga

import kotlin.random.Random

class Individual(private val genotype: List<Gene>) : Comparable<Individual> {

    constructor() : this(PrimsAlgorithm.create())

    private val phenotype: List<Int> = PrimsAlgorithm.getSegments(genotype)

    private val overallDeviation = 0 // min
    private val edgeValue = 0 // min
    private val connectivityMeasure = 0 // min

    private val rank = 0
    private val crowdingDistance = 0

    fun mutate(mutationRate: Float): Individual {
        val mutatedGenotype = mutableListOf<Gene>().also { it.addAll(genotype) }
        for (i in genotype.indices) {
            if (Random.nextFloat() < mutationRate) {
                mutatedGenotype[i] = Gene.values().random()
            }
        }
        return Individual(mutatedGenotype)
    }

    fun crossover(other: Individual, crossoverRate: Float): Pair<Individual, Individual> {
        require(genotype.size == other.genotype.size)
        val thisOffspringGenotype = mutableListOf<Gene>().also { it.addAll(genotype) }
        val otherOffspringGenotype = mutableListOf<Gene>().also { it.addAll(other.genotype) }
        if (Random.nextFloat() < crossoverRate) {
            val crossoverPoint = 1 + Random.nextInt(genotype.size - 1)
            for (i in crossoverPoint until genotype.size) {
                thisOffspringGenotype[i] = other.genotype[i].also { otherOffspringGenotype[i] = genotype[i] }
            }
        }
        return Pair(Individual(thisOffspringGenotype), Individual(otherOffspringGenotype))
    }

    fun hammingDistance(other: Individual): Int {
        require(genotype.size == other.genotype.size)
        var hammingDistance = 0
        for (i in genotype.indices) {
            if (genotype[i] != other.genotype[i]) {
                hammingDistance += 1
            }
        }
        return hammingDistance
    }

    infix fun Individual.dominates(other: Individual): Boolean {
        return (
                overallDeviation <= other.overallDeviation &&
                edgeValue >= other.edgeValue &&
                connectivityMeasure <= other.connectivityMeasure
                ) && (
                    overallDeviation < other.overallDeviation ||
                    edgeValue > other.edgeValue ||
                    connectivityMeasure < other.connectivityMeasure
                )
    }

    override fun compareTo(other: Individual): Int {
        return when {
            rank != other.rank -> {
                rank - other.rank
            }
            crowdingDistance != other.crowdingDistance -> {
                crowdingDistance - other.crowdingDistance
            }
            else -> 0
        }
    }

    override fun toString(): String {
        return "Individual(rank=$rank, segments=${phenotype.toSet()})"
    }
}