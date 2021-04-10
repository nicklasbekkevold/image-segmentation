package ga

import domain.Image

class Population(private val populationSize: Int) {

    private var generation = 0
    private var maxFitness = 1
    private var averageFitness = 2
    private var diversity = 3
    private val population: List<Individual>

    init {
        val individuals = mutableListOf<Individual>()
        for (individual in 0 until populationSize) {
            individuals.add(Individual())
            println(individuals[individual])
        }
        population = individuals
    }

    fun getGeneration(): Int {
        return generation
    }

    fun evaluate() { }
    fun select() { }
    fun replace() { generation++ }

    override fun toString(): String {
        return """
            generation=$generation 
            maxFitness=$maxFitness
            averageFitness=$averageFitness
            diversity=$diversity
        """.trimIndent()
    }
}