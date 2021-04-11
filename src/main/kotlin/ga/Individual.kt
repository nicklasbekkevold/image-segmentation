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

    private val List<Gene>.loci: IntRange
        get() = this.indices

    fun mutate(mutationRate: Float): Individual {
        val mutatedGenotype = mutableListOf<Gene>().also { it.addAll(genotype) }
        for (locus in genotype.loci) {
            if (Random.nextFloat() < mutationRate) {
                mutatedGenotype[locus] = Gene.values().random()
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
            for (locus in crossoverPoint until genotype.size) {
                thisOffspringGenotype[locus] = other.genotype[locus].also { otherOffspringGenotype[locus] = genotype[locus] }
            }
        }
        return Pair(Individual(thisOffspringGenotype), Individual(otherOffspringGenotype))
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
                edgeValue <= other.edgeValue &&
                connectivityMeasure <= other.connectivityMeasure
                ) && (
                    overallDeviation < other.overallDeviation ||
                    edgeValue < other.edgeValue ||
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