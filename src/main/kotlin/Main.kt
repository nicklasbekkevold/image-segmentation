import ga.GeneticAlgorithm
import javafx.scene.image.WritableImage

const val imageDirectory = "86016"
const val tournamentSize = 4

fun main() {
    val generations = 100
    val populationSize = 10
    val crossoverRate = 0.8f
    val mutationRate = 0.2f
    val elitism = true

    val geneticAlgorithm = GeneticAlgorithm(populationSize, crossoverRate, mutationRate, elitism)

    for (generation in 1..generations) {
        println("Generation $generation")
        geneticAlgorithm.update()
    }
}