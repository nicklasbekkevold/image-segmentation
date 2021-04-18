import ga.GeneticAlgorithm

const val imageDirectory = "86016"
const val generations = 100
const val tournamentSize = 4
const val crossoverRate = 0.8f
const val mutationRate = 0.2f
const val populationSize = 10

fun main() {
    val geneticAlgorithm = GeneticAlgorithm()
    for (generation in 1..generations) {
        println("Generation $generation")
        geneticAlgorithm.update()
    }
    geneticAlgorithm.exit()
}