import helper.util.println
import helper.util.readInput
import kotlin.math.abs

typealias Pos = Pair<Int, Int>

fun main() {
    D10.part2().println()
}

object D10 {
    private val grid = readInput("input-10")
    private val loop: List<Pos>

    init {
        val x = grid.indexOfFirst { s -> 'S' in s }
        val startPosition = x to grid[x].indexOf('S')

        fun getNext(pos: Pos, dir: NESW): Pair<Pos, NESW>? {
            val nextPos = pos + dir.toPos()
            val nextDirs = gridPositionToNESW(nextPos)
            if (dir.opposite() !in nextDirs) return null
            return nextPos to nextDirs.minus(dir.opposite()).first()
        }

        val tmpLoop = mutableListOf<Pos>()
        for (startDir in NESW.entries) {
            tmpLoop.clear()
            tmpLoop.add(startPosition)
            val start = getNext(startPosition, startDir) ?: continue
            var curPos = start.first
            var curDir = start.second
            while (grid[curPos.first][curPos.second] != 'S') {
                tmpLoop.add(curPos)
                val (nextPos, nextDir) = getNext(curPos, curDir) ?: break
                curPos = nextPos
                curDir = nextDir
            }
            if (grid[curPos.first][curPos.second] == 'S') break
        }
        loop = tmpLoop
    }

    fun part1(): String =
        (loop.size / 2).toString()

    fun part2(): String =
        (1..<grid.size - 1).sumOf { x ->
            val idx = grid[x].indices.filter { y ->
                val i1 = loop.indexOf(x to y)
                val i2 = loop.indexOf(x + 1 to y)
                i1 != -1 && i2 != -1 && (abs(i1 - i2) == 1 || i1 in listOf(0, loop.lastIndex) && i2 in listOf(
                    0,
                    loop.lastIndex
                ))
            }
            (idx.indices step 2).sumOf { i ->
                (idx[i]..idx[i + 1]).count { y -> x to y !in loop }
            }
        }.toString()

    private fun gridPositionToNESW(pos: Pos): List<NESW> =
        when (grid[pos.first][pos.second]) {
            '|' -> listOf(NESW.NORTH, NESW.SOUTH)
            '-' -> listOf(NESW.EAST, NESW.WEST)
            'L' -> listOf(NESW.NORTH, NESW.EAST)
            'J' -> listOf(NESW.NORTH, NESW.WEST)
            '7' -> listOf(NESW.SOUTH, NESW.WEST)
            'F' -> listOf(NESW.SOUTH, NESW.EAST)
            'S' -> listOf(NESW.NORTH, NESW.SOUTH, NESW.EAST, NESW.WEST)
            '.' -> listOf()
            else -> error("Missed a char")
        }

    private operator fun Pos.plus(other: Pos): Pos =
        first + other.first to second + other.second

    private enum class NESW {
        NORTH, EAST, SOUTH, WEST
    }

    private fun NESW.toPos(): Pos = when (this) {
        NESW.NORTH -> -1 to 0
        NESW.EAST -> 0 to 1
        NESW.SOUTH -> 1 to 0
        NESW.WEST -> 0 to -1
    }

    private fun NESW.opposite(): NESW = when (this) {
        NESW.NORTH -> NESW.SOUTH
        NESW.EAST -> NESW.WEST
        NESW.SOUTH -> NESW.NORTH
        NESW.WEST -> NESW.EAST
    }
}