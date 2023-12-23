import helper.util.println
import helper.util.readInput
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
    check(Day11Util.part2(sample1, 2) == 374L)
    check(Day11Util.part2(sample1, 10) == 1030L)
    check(Day11Util.part2(sample1, 100) == 8410L)
    val output2 = Day11Util.part2(input1, 1_000_000)
    check(output2 == 519939907614L)
    output2.println()
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

    fun part2(input: List<String>, timeLarger: Int): Long {
        val spaceObservation = mutableListOf<MutableList<Char>>()
        input.forEach { row ->
            spaceObservation.add(row.toCharArray().toMutableList())
        }
        val galaxyInfo = getGalaxyInfo(spaceObservation)
        val (galaxies, emptyRow, emptyColumn) = galaxyInfo

        val emptyRowSet = emptyRow.toSet()
        val emptyColumnSet = emptyColumn.toSet()

        galaxies.forEach { path ->
            emptyRowSet.count { it < path.row }
                .let { path.row += (it * (timeLarger - 1)) }
            emptyColumnSet.count { it < path.column }
                .let { path.column += (it * (timeLarger - 1)) }
        }

        var answer = 0L
        for (i in 0 until galaxies.size - 1) {
            for (j in i + 1 until galaxies.size) {
                val distance = getManhattan(galaxies[i], galaxies[j])
                answer += distance
            }
        }
        return answer
    }

    private fun getManhattan(a: Point, b: Point) =
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

    private fun getStartingPointByEachLabel(spaceObservation: Space): List<Point> {
        val startingPointList = mutableListOf<Point>()
        for (rowIndex in spaceObservation.indices) {
            for (columnIndex in spaceObservation[0].indices) {
                val currentChar = spaceObservation[rowIndex][columnIndex]
                if (currentChar != EMPTY_SPACE) {
                    val point = Point(currentChar, rowIndex, columnIndex)
                    startingPointList.add(point)
                }
            }
        }
        return startingPointList
    }

    private fun getGalaxyInfo(spaceObservation: Space): GalaxyInfo {
        val startingPointList = mutableListOf<Point>()
        for (rowIndex in spaceObservation.indices) {
            for (columnIndex in spaceObservation[0].indices) {
                val currentChar = spaceObservation[rowIndex][columnIndex]
                if (currentChar != EMPTY_SPACE) {
                    val point = Point(currentChar, rowIndex, columnIndex)
                    startingPointList.add(point)
                }
            }
        }
        val emptyRow = getEmptyRow(spaceObservation)
        val emptyColumn = getEmptyColumn(spaceObservation)
        return GalaxyInfo(startingPointList, emptyRow, emptyColumn)
    }

    private fun expandRow(spaceObservation: Space) {
        // Scan all rows
        val emptyRow = getEmptyRow(spaceObservation)
        var count = 0
        emptyRow.forEach { rowIndex ->
            spaceObservation.add(rowIndex + count, MutableList(spaceObservation[0].size) { EMPTY_SPACE })
            count++
        }
    }

    private fun getEmptyRow(spaceObservation: Space): List<Int> {
        val emptyRow = spaceObservation.mapIndexed { index, chars ->
            if (chars.all { it == EMPTY_SPACE }) {
                index
            } else {
                -1
            }
        }.filter { it != -1 }
        return emptyRow
    }

    private fun expandColumn(spaceObservation: Space) {
        // Scan all columns
        val emptyColumnSet = getEmptyColumn(spaceObservation)
        var count = 0
        emptyColumnSet.forEach { columnIndex ->
            spaceObservation.forEach { row ->
                row.add(columnIndex + count, EMPTY_SPACE)
            }
            count++
        }
    }

    private fun getEmptyColumn(spaceObservation: Space): List<Int> {
        val emptyColumnSet = mutableSetOf<Int>()
        for (columnIndex in spaceObservation[0].indices) {
            val columnFromAllRow = spaceObservation.map { it[columnIndex] }
            if (columnFromAllRow.all { it == EMPTY_SPACE }) {
                emptyColumnSet.add(columnIndex)
            }
        }
        return emptyColumnSet.toList()
    }

    data class Point(val label: Char, var row: Int, var column: Int)

    data class GalaxyInfo(val galaxies: List<Point>, val emptyRow: List<Int>, val emptyColumn: List<Int>)
}