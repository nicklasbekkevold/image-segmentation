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

    fun fastNonDominatedSort(population: Population): MutableList<MutableSet<Individual>> {
        val S = mutableMapOf<Individual, MutableSet<Individual>>().withDefault { mutableSetOf() }
        val n = mutableMapOf<Individual, Int>().withDefault { 0 }
        val fronts = mutableListOf<MutableSet<Individual>>().also { it.add(mutableSetOf()) }

        for (p in population) {
            for (q in population) {
                if (p dominates q) {
                    val s = mutableSetOf<Individual>()
                    S[p] = S.getValue(p).also { it.add(q) }
                } else if (q dominates p) {
                    n[p] = n.getValue(p) + 1
                }
            }
            if (n[p] == 0) {
                p.rank = 1
                fronts[0].add(p)
            }
        }

        var i = 0
        while (fronts[i].size != 0) {
            val Q = mutableSetOf<Individual>()
            for (p in fronts[i]) {
                for (q in S.getValue(p)) {
                    n[q] = n.getValue(q) - 1
                    if (n.getValue(q) == 0) {
                        q.rank = i + 1
                        Q.add(q)
                    }
                }
            }
            i++
            fronts[i] = Q
        }
        return fronts
    }

    fun exit() {
        println("Genetic algorithm exit.")
    }
}