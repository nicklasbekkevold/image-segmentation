import ga.GeneticAlgorithm
import ga.Initialization
import kotlin.system.measureTimeMillis

const val imageDirectory = "86016"
const val generations = 0
const val tournamentSize = 4
const val crossoverRate = 0.7f
const val mutationRate = 0.2f
const val populationSize = 50
val initialization = Initialization.Random()

const val multiObjective = true
const val overallDeviationWeight = 0.05
const val edgeValueWeight = 1
const val connectivityMeasureWeight = 1

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

    val paretoFront = geneticAlgorithm.exit()
    for (i in paretoFront.indices) {
        writeBlackAndWhiteImageToFile("s$i", paretoFront[i])
        writeGreenEdgeImageToFile("s$i", paretoFront[i])
    }
}