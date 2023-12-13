import kotlin.math.max

fun main() {
    val input1 = readInput("input-13")
    // Sample
    val sample1 = readInput("sample1-13")
    // Part1
    check(Day13Util.part1(sample1) == 405)
    val output1 = Day13Util.part1(input1)
    check(output1 == 33780)
    output1.println()

    // Part2
//    check(Day12Util.part2(sample1) == 525152L)
//    val output2 = Day12Util.part2(input1)
//    check(output2 == 3384337640277L)
//    output2.println()
}

private object Day13Util {

    private const val ASH = '.'
    private const val ROCK = '#'

    fun part1(input: List<String>): Int {
        val patterns = parseInput(input)
        return patterns.sumOf {
            calculateMirrorPattern(it)
        }
    }

    private fun calculateMirrorPattern(pattern: List<String>): Int {
        val maxRow = pattern.size
        val maxCol = pattern[0].length
        val allPoints = mutableListOf<Point>()
        // Store all points
        for (row in 0 until maxRow) {
            for (col in 0 until maxCol) {
                val label = pattern[row][col]
                val point = Point(label, row, col)
                allPoints.add(point)
            }
        }
        return countRowMatch(maxRow, allPoints) * 100 + countColMatch(maxCol, allPoints)
    }

    private fun countRowMatch(maxRow: Int, allPoints: MutableList<Point>): Int {
        val rangeList = (0 until maxRow).windowed(2)
        val rowGroup = mutableMapOf<Int, MutableSet<Pair<Char, Int>>>()
        allPoints.forEach {
            val row = it.r
            val col = it.c
            val set = rowGroup.getOrPut(row) { mutableSetOf() }
            set.add(it.label to col)
        }
        var mirrorCount = 0
        rangeList.forEach {
            var (up, down) = it
            var localCount = 0
            var isMatch = true
            while (up >= 0 && down < maxRow) {
                val upPoints = rowGroup[up]!!
                val downPoints = rowGroup[down]!!
                if (upPoints.containsAll(downPoints)) {
                    localCount++
                } else {
                    isMatch = false
                    break
                }
                up--
                down++
            }
            if (isMatch) {
                val finalizeLocalCount = if (it.first() - localCount <= 0 && down >= maxRow) {
                    localCount + 1
                } else {
                    (localCount + (it.first() - localCount)) + 1
                }
                mirrorCount = max(mirrorCount, finalizeLocalCount)
            }
        }
        return mirrorCount
    }

    private fun countColMatch(maxCol: Int, allPoints: MutableList<Point>): Int {
        val rangeList = (0 until maxCol).windowed(2)
        val rowGroup = mutableMapOf<Int, MutableSet<Pair<Char, Int>>>()
        allPoints.forEach {
            val row = it.r
            val col = it.c
            val set = rowGroup.getOrPut(col) { mutableSetOf() }
            set.add(it.label to row)
        }
        var mirrorCount = 0
        rangeList.forEach {
            var (left, right) = it
            var localCount = 0
            var isMatch = true
            while (left >= 0 && right < maxCol) {
                val leftPoints = rowGroup[left]!!
                val rightPoints = rowGroup[right]!!
                if (leftPoints.containsAll(rightPoints)) {
                    localCount++
                } else {
                    isMatch = false
                    break
                }
                left--
                right++
            }
            if (isMatch) {
                val finalizeLocalCount = if (it.first() - localCount <= 0 && right >= maxCol) {
                    localCount + 1
                } else {
                    (localCount + (it.first() - localCount)) + 1
                }
                mirrorCount = max(mirrorCount, finalizeLocalCount)
            }
        }
        return mirrorCount
    }

    fun part2(input: List<String>): Int {
        val patterns = parseInput(input)
        return 0
    }

    private fun parseInput(input: List<String>): List<List<String>> =
        input.joinToString("\n") // Join all lines after the first two
            .split("\n\n") // Split the line contain empty line
            .map {
                it.split("\n") // Split by line
            }

    data class Point(val label: Char, val r: Int, val c: Int)
}