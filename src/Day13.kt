import helper.util.println
import helper.util.readInput
import kotlin.math.abs


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
    check(Day13Util.part2(sample1) == 400)
    val output2 = Day13Util.part2(input1)
    check(output2 == 23479)
    output2.println()
}

private object Day13Util {

    fun part1(input: List<String>): Int {
        val patterns = parseInput(input)
        return patterns.sumOf {
            calculateMirrorPattern(it, 0)
        }
    }

    fun part2(input: List<String>): Int {
        val patterns = parseInput(input)
        return patterns.sumOf {
            calculateMirrorPattern(it, 1)
        }
    }

    private fun calculateMirrorPattern(pattern: List<String>, smudgeFixAmount: Int): Int {
        findHorizontalMirror(
            pattern,
            smudgeFixAmount
        )?.let { return it } // Find horizontal mirror first due to more value (I guess)

        findVerticalMirror(
            pattern,
            smudgeFixAmount
        )?.let { return it }

        throw IllegalStateException("No mirror pattern found")
    }

    private fun findHorizontalMirror(pattern: List<String>, smudgeFixAmount: Int): Int? {
        val rowIndexRange = 0 until pattern.lastIndex
        return rowIndexRange.firstNotNullOfOrNull { startIdx ->
            val mirrorRange = createMirrorRanges(startIdx, pattern.lastIndex)
            val sumOfDifference = mirrorRange.sumOf { (up, down) ->
                pattern[up].difference(pattern[down])
            }
            // If the mirror range that already zip
            // When difference become zero - That whole pattern is mirror and able to calculate the distance
            // Based on part 2 - The smudge fix amount is 1 and only 1 on each pattern
            // Therefore, instead of 0, Find 1 sum of difference and calculate the distance when found it
            if (sumOfDifference == smudgeFixAmount) (startIdx + 1) * 100 else null
        }
    }

    private fun findVerticalMirror(pattern: List<String>, smudgeFixAmount: Int): Int? {
        val colIndexRange = 0 until pattern.first().lastIndex
        return colIndexRange.firstNotNullOfOrNull { start ->
            val mirrorRange = createMirrorRanges(start, pattern.first().lastIndex)
            val sumOfDifference = mirrorRange.sumOf { (left, right) ->
                pattern.columnToString(left).difference(pattern.columnToString(right))
            }
            // If the mirror range that already zip
            // When difference become zero - That whole pattern is mirror and able to calculate the distance
            // Based on part 2 - The smudge fix amount is 1 and only 1 on each pattern
            // Therefore, instead of 0, Find 1 sum of difference and calculate the distance when found it
            if (sumOfDifference == smudgeFixAmount) start + 1 else null
        }
    }

    private fun List<String>.columnToString(col: Int): String {
        return joinToString("") { it[col].toString() }
    }

    private fun String.difference(other: String): Int {
        return indices.count { this[it] != other[it] } + abs(length - other.length)
    }

    private fun createMirrorRanges(start: Int, max: Int): List<Pair<Int, Int>> {
        return (start downTo 0).zip(start + 1..max)
    }

    private fun parseInput(input: List<String>): List<List<String>> =
        input.joinToString("\n") // Join all lines after the first two
            .split("\n\n") // Split the line contain empty line
            .map {
                it.split("\n") // Split by line
            }
}