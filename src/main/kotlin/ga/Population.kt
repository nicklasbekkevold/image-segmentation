package ga

import crossoverRate
import multiObjective
import mutationRate
import tournamentSize
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Population(private val population: List<Individual>, private val generation: Int): Iterable<Individual> {

    constructor(populationSize: Int) : this((1..populationSize).map { Individual() }, 0)
    private val evaluationFunction = if (multiObjective) evaluateNSGAII else evaluateSGA

    val size: Int
        get() {
            return population.size
        }

    operator fun get(index: Int): Individual {
        return population[index]
    }

    fun evaluate() {
        evaluationFunction(population)
    }

    private val evaluateSGA: (population: List<Individual>) -> Unit
        get() = {
            population.forEach { it.computeFitness() }
        }

    private val evaluateNSGAII: (population: List<Individual>) -> Unit
        get() = {
            val paretoFront = GeneticAlgorithm.fastNonDominatedSort(population).first()
            println("Pareto front size: ${paretoFront.size}")
        }

    fun select(): List<Individual> = population.map { tournamentSelection(tournamentSize) }.shuffled()

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

    fun getElite(eliteSize: Int): List<Individual> {
        return population.sorted().subList(0, eliteSize)
    }
}