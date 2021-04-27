import ga.GeneticAlgorithm
import ga.Initialization
import kotlin.system.measureTimeMillis

const val imageDirectory = "86016"
const val generations = 10
const val tournamentSize = 4
const val crossoverRate = 0.7f
const val mutationRate = 0.2f
const val populationSize = 50
val initialization = Initialization.Random()

const val multiObjective = false
const val overallDeviationWeight = 10
const val edgeValueWeight = 2
const val connectivityMeasureWeight = 100

fun main() {
    deleteImages()
    copyGroundTruthImages(imageDirectory)

    val geneticAlgorithm: GeneticAlgorithm
    val initializationTime = measureTimeMillis {
        geneticAlgorithm = GeneticAlgorithm()
    }
    println("Initialization took $initializationTime ms")
    val runningTime = measureTimeMillis {
        for (generation in 1..generations) {
            println("Generation $generation")
            geneticAlgorithm.update()
        }
    }
    println("Running $generations generations took $runningTime ms")

    val elite = geneticAlgorithm.exit()
    for (i in elite.indices) {
        writeBlackAndWhiteImageToFile("s$i", elite[i])
        writeGreenEdgeImageToFile("s$i", elite[i])
    }
}