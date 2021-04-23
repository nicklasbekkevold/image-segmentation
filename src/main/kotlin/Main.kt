import ga.GeneticAlgorithm
import ga.Initialization
import kotlin.system.measureTimeMillis

const val imageDirectory = "86016"
const val generations = 100
const val tournamentSize = 4
const val crossoverRate = 0.8f
const val mutationRate = 0.2f
const val populationSize = 40
val initialization = Initialization.HeuristicClustering()

fun main() {
    val geneticAlgorithm: GeneticAlgorithm
    val time = measureTimeMillis {
        geneticAlgorithm = GeneticAlgorithm()
    }
    println("Initialization took $time ms")
    for (generation in 1..generations) {
        println("Generation $generation")
        geneticAlgorithm.update()
    }
    geneticAlgorithm.exit()
}