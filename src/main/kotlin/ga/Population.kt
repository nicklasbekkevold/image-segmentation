package ga

import tournamentSize

class Population(
    private val population: List<Individual>,
    private var generation: Int
    ): Iterable<Individual> {

    constructor(populationSize: Int) : this((1..populationSize).map { Individual() }, 0)

    private var maxFitness = 0
    private var averageFitness = 0
    private var diversity = 0

    fun getGeneration(): Int {
        return generation
    }

    val size: Int
        get() {
            return population.size
        }

    operator fun get(index: Int): Individual {
        return population[index]
    }

    fun evaluate() {

    }

    fun select(): List<Individual> = population.map { tournamentSelection(tournamentSize) }

    private fun tournamentSelection(k: Int) = (1..k).map { population.random() }.sorted()[0]

    fun replace(newPopulation: List<Individual>): Population {
        generation++
        return Population(newPopulation, generation)
    }

    override fun toString(): String {
        return """
            generation=$generation 
            maxFitness=$maxFitness
            averageFitness=$averageFitness
            diversity=$diversity
        """.trimIndent()
    }

    override fun iterator(): Iterator<Individual> {
        return population.iterator()
    }
}