package ga

import domain.Image

class GeneticAlgorithm(
    private val populationSize: Int,
    private val crossoverRate: Float,
    private val mutationRate: Float,
    private val elitism: Boolean
) {

    private val population: Population

    init {
        printParameters()
        population = Population(populationSize)
    }

    private fun printParameters() {
        println("""
            Genetic algorithm initialized with the following configuration
            ┝ population size: $populationSize
            ┝ crossover rate: $crossoverRate
            ┝ mutation rate: $mutationRate
            ┕ elitism: $elitism
        """.trimIndent())
    }

    fun update(): Population {
        val matingPool = population.select()
        population.replace()
        return population
    }

    fun exit() {
        println("Genetic algorithm exit.")
    }
}