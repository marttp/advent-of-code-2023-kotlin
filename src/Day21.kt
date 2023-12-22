import java.util.LinkedList

fun main() {
    val input1 = readInput("input-21")
    // Sample
    val sample1 = readInput("sample1-21")
    // Part1
    check(Day21Util.part1(sample1, 6) == 16)
    val output1 = Day21Util.part1(input1)
    check(output1 == 3642)
    output1.println()

    // Part2
    val output2 = Day21Util.part2(input1)
    check(output2 == 608603023105276L)
    output2.println()
}

private object Day21Util {

    // Limit maximum 64 steps
    fun part1(input: List<String>, steps: Int = 64): Int {
        val grid = parseInputToGrid(input)
        val queue = ArrayDeque<Pair<Point2D, Int>>()
        val startPoint = findStartPoint(grid)
        queue.add(startPoint to 0)

        val visited = mutableSetOf<Point2D>()
        while (queue.isNotEmpty()) {
            val (position, step) = queue.removeFirst()
            if (step == steps + 1) {
                break
            }
            if (position !in visited) {
                if (step pm 2 == 0) {
                    visited.add(position)
                }
                queue.addAll(getNextPositions(grid, position).map { it to step + 1 })
            }
        }

        return visited.size
    }

    // Become infinite map
    fun part2(input: List<String>, steps: Long = 26501365L): Long {
        val grid = parseInputToGrid(input)
        val queue = ArrayDeque<Pair<Point2D, Long>>()
        val startPoint = findStartPoint(grid)
        queue.add(startPoint to 0)

        var delta = 0L
        var skip = 0L

        val visited = mutableSetOf<Point2D>()
        val size = Point2D(grid.size, grid[0].size)
        val cycle = size.column * 2

        var lastStep = 0L
        var previousPlots = 0L
        var delta1 = 0L
        var delta2 = 0L
        var plots = 0L
        while (queue.isNotEmpty()) {
            val (position, step) = queue.removeFirst()
            if (position in visited) {
                continue
            }
            if (step % 2 == 1L) {
                visited.add(position)
            }
            if (step % cycle == 66L && step > lastStep) {
                lastStep = step
                if (plots - previousPlots - delta1 == delta2) {
                    delta = plots - previousPlots + delta2
                    skip = step - 1
                    break
                }
                delta2 = (plots - previousPlots) - delta1
                delta1 = plots - previousPlots
                previousPlots = plots
            }
            plots = visited.size.toLong()
            queue.addAll(position.getCardinalNeighbors().filter { grid[it % size] != '#' }.map { it to step + 1 })
        }
        while (skip < steps) {
            skip += cycle
            plots += delta
            delta += delta2
        }
        return plots
    }

    private fun getNextPositions(grid: Array<CharArray>, position: Point2D): List<Point2D> {
        return position.getCardinalNeighbors()
            .filter { grid.isInBounds(it) && grid[it.row][it.column] != '#' }
    }

    private fun parseInputToGrid(input: List<String>): Array<CharArray> {
        return input.map { it.toCharArray() }.toTypedArray()
    }

    private fun findStartPoint(grid: Array<CharArray>): Point2D {
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] == 'S') {
                    return Point2D(i, j)
                }
            }
        }
        throw IllegalArgumentException("Cannot find start point")
    }
}