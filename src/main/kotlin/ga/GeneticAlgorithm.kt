package ga

import crossoverRate
import initialization
import multiObjective
import mutationRate
import populationSize
import kotlin.math.abs

class GeneticAlgorithm {

    private var population: Population
    private var paretoFront: List<Individual> = listOf()
    private val updateFunction = if (multiObjective) updateNSGAII else updateSGA

    init {
        printParameters()
        population = Population(populationSize)
        population.evaluate()
    }

    private fun printParameters() {
        println("""
            Genetic algorithm initialized with the following configuration
            ┝ population size: $populationSize
            ┝ crossover rate: $crossoverRate
            ┝ mutation rate: $mutationRate
            ┕ initialization: ${initialization.name}
        """.trimIndent())
    }

    fun update(): Population {
        return updateFunction()
    }

    private val updateSGA: () -> Population
        get() = {
            // Evaluation step implicitly done from previous loop
            population.evaluate()
            val parents = population.select() // P_t
            val offsprings = population.recombine(parents) // Q_t
            population = population.replace(offsprings)
            population
        }

    private val updateNSGAII: () -> Population
        get() = {
            // Evaluation step implicitly done from previous loop
            val parents = population.select() // P_t
            val offsprings = population.recombine(parents) // Q_t
            val combinedPopulation = parents + offsprings // R_t

            val fronts = fastNonDominatedSort(combinedPopulation)
            paretoFront = fronts.first()

            val resultPopulation = mutableListOf<Individual>()
            var i = 0
            while (resultPopulation.size + fronts[i].size <= populationSize) {
                crowdingDistanceAssignment(fronts[i])
                resultPopulation.addAll(fronts[i++])
            }

            crowdingDistanceAssignment(fronts[i])
            val difference = populationSize - resultPopulation.size
            resultPopulation.addAll(fronts[i].sorted().subList(0, difference))

            population = population.replace(resultPopulation)
            population
        }

    companion object {
        fun fastNonDominatedSort(population: List<Individual>): List<List<Individual>> {
            population.forEach { it.reset() }
            for(i in population.indices) {
                val p = population[i]
                for (j in i + 1 until population.size) {
                    val q = population[j]
                    if (p dominates q) {
                        p.dominates.add(q)
                        q.dominationCount++
                    } else if (q dominates p) {
                        q.dominates.add(p)
                        p.dominationCount++
                    } else {
                        break
                    }
                }
                if (p.dominationCount == 0) {
                    p.rank = 1
                }
            }
            for (p in population) {
                for (individual in p.dominates) {
                    individual.dominationCount--
                    if (individual.dominationCount == 0)  {
                        individual.rank = p.rank + 1
                    }
                }
            }
            return population.groupBy { it.rank }.toSortedMap().values.toList()
        }

        fun crowdingDistanceAssignment(population: List<Individual>) {
            val l = population.size
            population.forEach { it.crowdingDistance = 0f }
            for (objectiveFunction in ObjectiveFunction.values()) {
                population.sortedWith { i1, i2 -> i1.compareToWith(i2, objectiveFunction) }
                population[0].crowdingDistance = Float.MAX_VALUE
                population[l-1].crowdingDistance = Float.MAX_VALUE
                val maxLength = population[l-1].getOrEvaluate(objectiveFunction) - population[0].getOrEvaluate(objectiveFunction)  // max - min
                for (i in 1 until l-1) {
                    val length = abs(population[i+1].getOrEvaluate(objectiveFunction) - population[i-1].getOrEvaluate(objectiveFunction))
                    population[i].crowdingDistance += length / maxLength
                }
            }
        }
    }

    fun exit(): List<Individual> {
        println("Genetic algorithm exit.")
        if (paretoFront.isNotEmpty()) {
            return paretoFront
        }
        return population.getElite(populationSize/10)
    }
}