import helper.point.Point2D
import helper.util.*

fun main() {
    val input1 = readInput("input-14")
    // Sample
    val sample1 = readInput("sample1-14")
    // Part1
    check(Day14Util.part1(sample1) == 136)
    val output1 = Day14Util.part1(input1)
    check(output1 == 110090)
    output1.println()

    // Part2
    check(Day14Util.part2(sample1) == 64)
    val output2 = Day14Util.part2(input1)
    check(output2 == 95254)
    output2.println()
}

private object Day14Util {

    private const val ROCK = 'O'
    private const val BOX = '#'
    private const val EMPTY = '.'

    fun part1(input: List<String>): Int {
        val grid = createGrid(input)
        grid.gridTilt(Point2D.NORTH)
        return grid.getTotalLoad()
    }

    fun part2(input: List<String>, cycleTime: Int = 1_000_000_000): Int {
        val grid = createGrid(input)
        var counter = 1
        // Store seen state with cycle value, if the state is already exists, skip the round
        val seen = mutableMapOf<Int, Int>()
        while (counter <= cycleTime) {
            grid.cycleTilt()
            // Turn in to unique value for each round
            val state = grid.sumOf { it.joinToString("").hashCode() }
            if (state in seen) {
                // Found the cycle
                // But how many cycles are there to become this state again?
                val cycleLength = counter - seen[state]!!
                val remaining = (cycleTime - counter) % cycleLength
                repeat(remaining) {
                    grid.cycleTilt()
                }
                break
            } else {
                seen[state] = counter
                counter++
            }
        }
        return grid.getTotalLoad()
    }

    private fun createGrid(input: List<String>): Array<CharArray> {
        return input.map { it.toCharArray() }.toTypedArray()
    }

    private fun Array<CharArray>.gridTilt(direction: Point2D) {
        val steps = when (direction) {
            Point2D.NORTH -> {
                // Each column -> Move up all the rocks in order
                this.indices.flatMap { rowIdx ->
                    this.first().indices.map { colIdx ->
                        Point2D(rowIdx, colIdx)
                    }
                }
            }
            Point2D.EAST -> {
                // Each row -> Move right all the rocks in order
                this.first().indices.reversed().flatMap { colIdx ->
                    this.indices.map { rowIdx ->
                        Point2D(rowIdx, colIdx)
                    }
                }

            }
            Point2D.SOUTH -> {
                // Each column -> Move down all the rocks in order
                this.indices.reversed().flatMap { rowIdx ->
                    this.first().indices.map { colIdx ->
                        Point2D(rowIdx, colIdx)
                    }
                }
            }
            Point2D.WEST -> {
                // Each row -> Move left all the rocks in order
                this.first().indices.flatMap { colIdx ->
                    this.indices.map { rowIdx ->
                        Point2D(rowIdx, colIdx)
                    }
                }
            }
            else -> throw IllegalArgumentException("Invalid direction: $direction")
        }
        steps
            // Move only rock
            .filter { this[it] == ROCK }
            .forEach { cellTilt(it, direction) }
    }

    private fun Array<CharArray>.cycleTilt() {
        // roll north, then west, then south, then east
        gridTilt(Point2D.NORTH)
        gridTilt(Point2D.WEST)
        gridTilt(Point2D.SOUTH)
        gridTilt(Point2D.EAST)
    }

    private fun Array<CharArray>.cellTilt(place: Point2D, direction: Point2D) {
        var current = place
        while (isInBounds(current + direction) && this[current + direction] == EMPTY) {
            swap(current, current + direction)
            current += direction
        }
    }

    private fun Array<CharArray>.getTotalLoad() =
        mapIndexed { rowIdx, row ->
            row.sumOf { column ->
                if (column == ROCK) {
                    this.size - rowIdx
                } else {
                    0
                }
            }
        }.sum()
}