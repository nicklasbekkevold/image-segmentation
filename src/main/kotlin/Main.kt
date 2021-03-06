import ga.GeneticAlgorithm
import ga.Initialization
import kotlin.system.measureTimeMillis

const val imageDirectory = "147091"
const val generations = 100
const val populationSize = 50
const val tournamentSize = 4
const val crossoverRate = 0.7f
const val mutationRate = 0.2f
val initialization = Initialization.HeuristicClustering()

const val multiObjective = true
const val overallDeviationWeight = 2
const val edgeValueWeight = 1
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
        writeGreenEdgeImageToFile("g$i (${elite[i]})", elite[i])
        writeBlackAndWhiteImageToFile("b$i (${elite[i]})", elite[i])
    }
}