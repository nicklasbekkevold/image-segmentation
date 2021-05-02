package domain

import ga.Gene

val Int.twoConnectedNeighborhood: List<Int>
    get() {
        val twoConnectedNeighborhood = mutableListOf<Int>()
        val (x, y) = Image.indexToCoordinate(this)
        if (this % Image.width != 0 && y < Image.height - 1) twoConnectedNeighborhood.add(this + Image.width) // S
        if (x < Image.width - 1) twoConnectedNeighborhood.add(this + 1) // E
        return twoConnectedNeighborhood
    }

val Int.vonNeumannNeighborhood: List<Pair<Int, Gene>>
    get() {
        val vonNeumannNeighborhood = mutableListOf<Pair<Int, Gene>>()
        val (x, y) = Image.indexToCoordinate(this)
        if (y > 0) vonNeumannNeighborhood.add(Pair(this - Image.width, Gene.UP)) // N
        if (x < Image.width - 1) vonNeumannNeighborhood.add(Pair(this + 1, Gene.RIGHT)) // E
        if (this % Image.width != 0 && y < Image.height - 1) vonNeumannNeighborhood.add(Pair(this + Image.width, Gene.DOWN)) // S
        if (x > 0) vonNeumannNeighborhood.add(Pair(this - 1, Gene.LEFT)) // W
        return vonNeumannNeighborhood
    }

val Int.mooreNeighborhood: List<Int>
    get() {
        val mooreNeighborhood = mutableListOf<Int>()
        val (x, y) = Image.indexToCoordinate(this)
        if (y > 0) mooreNeighborhood.add(this - Image.width) // N
        if (x < Image.width - 1 && y > 0) mooreNeighborhood.add(this + 1 - Image.width) // NE
        if (x < Image.width - 1) mooreNeighborhood.add(this + 1) // E
        if (x < Image.width - 1 && y < Image.height - 1) mooreNeighborhood.add(this + 1 + Image.width) // SE
        if (this % Image.width != 0 && y < Image.height - 1) mooreNeighborhood.add(this + Image.width) // S
        if (x > 0 && y < Image.height - 1) mooreNeighborhood.add(this - 1 + Image.width) // SW
        if (x > 0) mooreNeighborhood.add(this - 1) // W
        if (x > 0 && y > 0) mooreNeighborhood.add(this - 1 - Image.width) // NW
        return mooreNeighborhood
    }

val Pair<Int, Int>.index: Int
    get() {
        return this.first + this.second * Image.width
    }