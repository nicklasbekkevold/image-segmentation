package ga

sealed class Initialization {
    data class Random(val name: String = "Random") : Initialization()
    data class HeuristicPrim(val name: String = "Prim") : Initialization()
    data class HeuristicClustering(val name: String = "Clustering") : Initialization()
}
