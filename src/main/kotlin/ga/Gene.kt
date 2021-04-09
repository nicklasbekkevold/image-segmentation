package ga

enum class Gene(
    private val direction: Pair<Int, Int>
) {
    UP(Pair(0, -1)),
    RIGHT(Pair(1, 0)),
    DOWN(Pair(0, 1)),
    LEFT(Pair(-1, 0)),
    NONE(Pair(0, 0));

    fun opposite(): Gene {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP -> DOWN
            DOWN -> UP
            NONE -> NONE
            else -> NONE
        }
    }

    override fun toString(): String {
        return when (this) {
            LEFT -> "←"
            RIGHT -> "→"
            UP -> "↑"
            DOWN -> "↓"
            NONE -> "●"
            else -> ""
        }
    }

    operator fun plus(node: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(this.direction.first + node.first, this.direction.second + node.second)
    }
}