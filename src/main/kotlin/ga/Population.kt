package ga

import crossoverRate
import mutationRate
import tournamentSize

class Population(private val population: List<Individual>, private val generation: Int): Iterable<Individual> {

    constructor(populationSize: Int) : this((1..populationSize).map { Individual() }, 0)

    val size: Int
        get() {
            return population.size
        }

    operator fun get(index: Int): Individual {
        return population[index]
    }

    fun evaluate(): List<List<Individual>> {
        return GeneticAlgorithm.fastNonDominatedSort(population)
    }

    fun select(): List<Individual> = population.map { tournamentSelection(tournamentSize) }

    private fun tournamentSelection(k: Int) = (1..k).map { population.random() }.sorted()[0].copy()

    fun recombine(parents: List<Individual>): List<Individual> {
        val offsprings = mutableListOf<Individual>()
        for (i in parents.indices step 2) {
            val (a, b) = parents[i].crossoverAndMutate(parents[i+1], crossoverRate, mutationRate)
            offsprings.add(a)
            offsprings.add(b)
        }
        return offsprings
    }

    fun replace(newPopulation: List<Individual>): Population {
        return Population(newPopulation, generation + 1)
    }

    override fun iterator(): Iterator<Individual> {
        return population.iterator()
    }
}