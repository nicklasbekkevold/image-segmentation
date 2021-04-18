package ga

import crossoverRate
import mutationRate
import populationSize

class GeneticAlgorithm() {

    private var population: Population

    init {
        printParameters()
        population = Population(populationSize)
    }

    private fun printParameters() {
        println("""
            Genetic algorithm initialized with the following configuration
            ┝ population size: $populationSize
            ┝ crossover rate: $crossoverRate
            ┕ mutation rate: $mutationRate
        """.trimIndent())
    }

    fun update(): Population {
        // Evaluation already done from previous loop
        val parents = population.select() // P_t
//        val offsprings = population.recombination(parents) // Q_t
        val combinedPopulation = parents + parents//+ offsprings // R_t

        val fronts = fastNonDominatedSort(combinedPopulation)

        val resultPopulation = mutableListOf<Individual>()
        var i = 0
        while (resultPopulation.size + fronts[i].size < populationSize) {
            crowdingDistanceAssignment(fronts[i])
            resultPopulation.addAll(fronts[i++])
        }

        crowdingDistanceAssignment(fronts[i])
        val difference = populationSize - resultPopulation.size
        resultPopulation.addAll(fronts[i].sorted().subList(0, difference - 1))

        population = population.replace(resultPopulation)
        return population
    }

    private fun fastNonDominatedSort(population: List<Individual>): List<List<Individual>> {
        val S = mutableMapOf<Individual, MutableSet<Individual>>().withDefault { mutableSetOf() } // set of other solutions which is dominated by solution
        val n = mutableMapOf<Individual, Int>().withDefault { 0 } // number of other solutions which dominates solution
        val F = mutableListOf<MutableList<Individual>>().also { it.add(mutableListOf()) } // 0-indexed

        for (p in population) {
            for (q in population) {
                if (p dominates q) {
                    S[p] = S.getValue(p).also { it.add(q) }
                } else if (q dominates p) {
                    n[p] = n.getValue(p) + 1
                }
            }
            if (n[p] == 0) {
                p.rank = 0
                F[0].add(p)
            }
        }

        var i = 0
        while (F[i].size != 0) {
            val updatedFront = mutableListOf<Individual>()
            for (p in F[i]) {
                for (q in S.getValue(p)) {
                    n[q] = n.getValue(q) - 1
                    if (n.getValue(q) == 0) {
                        q.rank = i + 1
                        updatedFront.add(q)
                    }
                }
            }
            i++
            F[i] = updatedFront
        }
        return F
    }

    private fun crowdingDistanceAssignment(population: List<Individual>) {
        val l = population.size
        population.forEach { it.crowdingDistance = 0f }
        for (objectiveFunction in ObjectiveFunction.values()) {
            population.sortedWith { i1, i2 -> i1.compareToWith(i2, objectiveFunction) }
            population[0].crowdingDistance = Float.MAX_VALUE
            population[l-1].crowdingDistance = Float.MAX_VALUE
            val maxLength = population[l-1].getOrEvaluate(objectiveFunction) - population[0].getOrEvaluate(objectiveFunction)  // max - min
            for (i in 1 until l-1) {
                val length = population[i+1].getOrEvaluate(objectiveFunction) - population[i-1].getOrEvaluate(objectiveFunction)
                population[i].crowdingDistance += length / maxLength
            }
        }
    }

    fun exit() {
        println("Genetic algorithm exit.")
    }
}