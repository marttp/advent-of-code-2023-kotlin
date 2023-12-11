import kotlin.math.abs

typealias Space = MutableList<MutableList<Char>>

fun main() {
    val input1 = readInput("input-11")
    // Sample
    val sample1 = readInput("sample1-11")
    // Part1
    check(Day11Util.part1(sample1) == 374)
    val output1 = Day11Util.part1(input1)
    check(output1 == 9947476)
    output1.println()

    // Part2
}

private object Day11Util {

    private const val EMPTY_SPACE = '.'
    private const val GALAXY = '#'

    fun part1(input: List<String>): Int {
        val spaceObservation = createExpandSpaceWithLabel(input)
        val startingPointList = getStartingPointByEachLabel(spaceObservation)
        var answer = 0
        for (i in 0 until startingPointList.size - 1) {
            for (j in i + 1 until startingPointList.size) {
                val distance = getManhattan(startingPointList[i], startingPointList[j])
                answer += distance
            }
        }
        return answer
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    private fun getManhattan(a: Path, b: Path) =
        abs(a.row - b.row) + abs(a.column - b.column)

    private fun markGalaxyLabel(spaceObservation: Space) {
        var label = 1
        for (rowIndex in spaceObservation.indices) {
            for (columnIndex in spaceObservation[0].indices) {
                if (spaceObservation[rowIndex][columnIndex] == GALAXY) {
                    spaceObservation[rowIndex][columnIndex] = label.toString().first()
                    label++
                }
            }
        }
    }

    private fun createExpandSpaceWithLabel(input: List<String>): Space {
        val spaceObservation = mutableListOf<MutableList<Char>>()
        input.forEach { row ->
            spaceObservation.add(row.toCharArray().toMutableList())
        }
        expandRow(spaceObservation)
        expandColumn(spaceObservation)
        markGalaxyLabel(spaceObservation)
        return spaceObservation
    }

    private fun getStartingPointByEachLabel(spaceObservation: Space): List<Path> {
        val startingPointList = mutableListOf<Path>()
        for (rowIndex in spaceObservation.indices) {
            for (columnIndex in spaceObservation[0].indices) {
                val currentChar = spaceObservation[rowIndex][columnIndex]
                if (currentChar != EMPTY_SPACE) {
                    val path = Path(currentChar, rowIndex, columnIndex, 0)
                    startingPointList.add(path)
                }
            }
        }
        return startingPointList
    }

    private fun expandRow(spaceObservation: Space) {
        // Scan all rows
        val emptyRow = spaceObservation.mapIndexed { index, chars ->
            if (chars.all { it == EMPTY_SPACE }) {
                index
            } else {
                -1
            }
        }.filter { it != -1 }
        var count = 0
        emptyRow.forEach { rowIndex ->
            spaceObservation.add(rowIndex + count, MutableList(spaceObservation[0].size) { EMPTY_SPACE })
            count++
        }
    }

    private fun expandColumn(spaceObservation: Space) {
        // Scan all columns
        val emptyColumnSet = mutableSetOf<Int>()
        for (columnIndex in spaceObservation[0].indices) {
            val columnFromAllRow = spaceObservation.map { it[columnIndex] }
            if (columnFromAllRow.all { it == EMPTY_SPACE }) {
                emptyColumnSet.add(columnIndex)
            }
        }
        var count = 0
        emptyColumnSet.forEach { columnIndex ->
            spaceObservation.forEach { row ->
                row.add(columnIndex + count, EMPTY_SPACE)
            }
            count++
        }
    }

    data class Path(val label: Char, val row: Int, val column: Int, val distance: Int) {
        fun generateNextMoves(spaceObservation: Space): List<Path> {
            val nextMoves = mutableListOf<Path>()
            for (direction in FOUR_DIRECTIONS) {
                val nextRow = row + direction.first
                val nextColumn = column + direction.second
                if (isInBounds(spaceObservation, nextRow, nextColumn)) {
                    nextMoves.add(Path(spaceObservation[nextRow][nextColumn], nextRow, nextColumn, distance + 1))
                }
            }
            return nextMoves
        }
    }

}