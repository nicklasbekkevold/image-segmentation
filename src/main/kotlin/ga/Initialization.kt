package ga

sealed class Initialization {
    data class Random(val name: String = "random") : Initialization()
    data class HeuristicPrim(val name: String = "heuristic minimum spanning tree (Prim)") : Initialization()
    data class HeuristicClustering(val name: String = "heuristic clustering") : Initialization()
}
