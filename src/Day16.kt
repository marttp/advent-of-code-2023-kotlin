fun main() {
    val input1 = readInput("input-16")
    // Sample
    val sample1 = readInput("sample1-16")
    // Part1
    check(Day16Util.part1(sample1) == 46)
    val output1 = Day16Util.part1(input1)
    check(output1 == 6816)
    output1.println()

    // Part2
    check(Day16Util.part2(sample1) == 51)
    val output2 = Day16Util.part2(input1)
    check(output2 == 8163)
    output2.println()
}

private object Day16Util {

    private const val EMPTY = '.'
    private const val ENERGY = '#'
    private const val MIRROR_LEFT = '/'
    private const val MIRROR_RIGHT = '\\'
    private const val SPLITTER_VERTICAL = '|'
    private const val SPLITTER_HORIZONTAL = '-'

    private val DIRECTION_MAP = mapOf(
        MIRROR_LEFT to mapOf(
            Point2D.NORTH to listOf(Point2D.EAST),
            Point2D.EAST to listOf(Point2D.NORTH),
            Point2D.SOUTH to listOf(Point2D.WEST),
            Point2D.WEST to listOf(Point2D.SOUTH),
        ),
        MIRROR_RIGHT to mapOf(
            Point2D.NORTH to listOf(Point2D.WEST),
            Point2D.EAST to listOf(Point2D.SOUTH),
            Point2D.SOUTH to listOf(Point2D.EAST),
            Point2D.WEST to listOf(Point2D.NORTH),
        ),
        SPLITTER_VERTICAL to mapOf(
            Point2D.NORTH to listOf(Point2D.NORTH),
            Point2D.EAST to listOf(Point2D.NORTH, Point2D.SOUTH),
            Point2D.SOUTH to listOf(Point2D.SOUTH),
            Point2D.WEST to listOf(Point2D.NORTH, Point2D.SOUTH),
        ),
        SPLITTER_HORIZONTAL to mapOf(
            Point2D.NORTH to listOf(Point2D.EAST, Point2D.WEST),
            Point2D.EAST to listOf(Point2D.EAST),
            Point2D.SOUTH to listOf(Point2D.EAST, Point2D.WEST),
            Point2D.WEST to listOf(Point2D.WEST),
        ),
    )

    fun part1(input: List<String>, startingPoint: Point2D = Point2D(0, 0), initDirection: Point2D = Point2D.EAST): Int {
        val grid = createGrid(input)
        return solve(grid, startingPoint, initDirection)
    }

    private fun solve(grid: Array<CharArray>, startingPoint: Point2D, initDirection: Point2D): Int {
        val energized = grid.createEnergizedTable()
        val seenObstacle = grid.createObstacleTable()
        dfs(startingPoint, initDirection, grid, energized, seenObstacle)
        return energized.sumOf { it.count { c -> c == ENERGY } }
    }

    // Start from any edge tile
    // All top row -> SOUTH
    // All bottom row -> NORTH
    // All left column -> EAST
    // All right column -> WEST
    fun part2(input: List<String>): Int {
        val grid = createGrid(input)
        val startingList = Point2D.FOUR_DIRECTIONS.map {
            val startingPoint = when (it) {
                Point2D.SOUTH -> grid.first().indices.map { colIdx -> Point2D(0, colIdx) }
                Point2D.NORTH -> grid.last().indices.map { colIdx -> Point2D(grid.lastIndex, colIdx) }
                Point2D.EAST -> grid.indices.map { rowIdx -> Point2D(rowIdx, 0) }
                Point2D.WEST -> grid.indices.map { rowIdx -> Point2D(rowIdx, grid.first().lastIndex) }
                else -> throw IllegalArgumentException("Invalid direction: $it")
            }
            startingPoint.map { point2D -> Part2Payload(point2D, it) }
        }.flatten()

        return startingList.maxOf {
            solve(grid, it.point2D, it.direction)
        }
    }

    private fun dfs(
        point2D: Point2D,
        direction: Point2D,
        grid: Array<CharArray>,
        energized: Array<CharArray>,
        seenObstacle: MutableMap<Point2D, Set<Point2D>>
    ) {
        energized[point2D.row][point2D.column] = ENERGY
        val nextMoves = point2D.move(grid, direction)
        for (move in nextMoves) {
            val nextPoint = point2D + move
            if (grid.isInBounds(nextPoint) && grid[point2D] == EMPTY) {
                dfs(nextPoint, move, grid, energized, seenObstacle)
            } else if (grid.isInBounds(nextPoint) && grid[point2D] != EMPTY && move !in seenObstacle[point2D]!!) {
                seenObstacle[point2D] = seenObstacle[point2D]!! + move
                dfs(nextPoint, move, grid, energized, seenObstacle)
            }
        }
    }

    private fun Point2D.move(grid: Array<CharArray>, currentDirection: Point2D): List<Point2D> {
        val currentEncounter = grid[this]
        if (DIRECTION_MAP.containsKey(currentEncounter)) {
            return DIRECTION_MAP[currentEncounter]!![currentDirection]!!
        }
        return listOf(currentDirection)
    }

    private fun createGrid(input: List<String>): Array<CharArray> {
        return input.map { it.toCharArray() }.toTypedArray()
    }

    private fun Array<CharArray>.createEnergizedTable(): Array<CharArray> {
        val energized = Array(size) { CharArray(size) { EMPTY } }
        return energized
    }

    private fun Array<CharArray>.createObstacleTable(): MutableMap<Point2D, Set<Point2D>> {
        val obstacles = mutableMapOf<Point2D, Set<Point2D>>()
        for (row in this.indices) {
            for (col in this[0].indices) {
                if (this[row][col] != EMPTY) {
                    obstacles[Point2D(row, col)] = mutableSetOf()
                }
            }
        }
        return obstacles
    }

    data class Part2Payload(val point2D: Point2D, val direction: Point2D)
}