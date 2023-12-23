import helper.point.Point2D
import helper.point.Point2D.Companion.FOUR_DIRECTIONS
import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-23")
    // Sample
    val sample1 = readInput("sample1-23")
    // Part1
    check(Day23Util.part1(sample1) == 94L)
    val output1 = Day23Util.part1(input1)
    check(output1 == 2202L)
    output1.println()

    // Part2
    check(Day23Util.part2(sample1) == 154L)
    val output2 = Day23Util.part2(input1)
    check(output2 == 6226L)
    output2.println()
}

private object Day23Util {
    private const val PATH = '.'
    private const val FOREST = '#'

    // Other are slopes
    private val SLOPE = mapOf(
        '^' to Point2D.NORTH,
        '>' to Point2D.EAST,
        'v' to Point2D.SOUTH,
        '<' to Point2D.WEST
    )

    // You're currently on the single path tile in the top row; your goal is to reach the single path tile in the bottom row
    // if you step onto a slope tile, your next step must be downhill (in the direction the arrow is pointing)
    // How many steps long is the longest hike - Find the longest path from top to bottom
    fun part1(input: List<String>): Long {
        val grid = parseToGrid(input)
        val longestPath = arrayOf(0L)
        val start = findStartingPoint(grid)
        val end = findEndpoint(grid)
        dfs(grid, start, end, longestPath)
        return longestPath[0]
    }

    // Find the longest - Definitely Depth First Search
    // With All possible - Backtracking
    private fun dfs(
        grid: Array<CharArray>,
        current: Point2D,
        end: Point2D,
        longestPath: Array<Long>,
        length: Long = 0,
        visited: HashSet<Point2D> = hashSetOf()
    ) {
        if (current == end) {
            longestPath[0] = maxOf(longestPath[0], length)
            return
        }
        // Start backtracking point
        // Add element to make sample
        visited.add(current)
        // Try possible next moves
        val nextMoves = current.getNextMoves(grid)
        nextMoves.filter { it !in visited }
            .forEach {
                dfs(grid, it, end, longestPath, length + 1, visited)
            }
        // Remove element
        visited.remove(current)
    }

    private fun Point2D.getNextMoves(grid: Array<CharArray>): List<Point2D> {
        val moves = mutableListOf<Point2D>()
        val current = grid[this.row][this.column]
        for (dir in FOUR_DIRECTIONS) {
            val next = this + dir
            if (grid.isInBounds(next) && grid[next.row][next.column] != FOREST) {
                // Until here - PATH or SLOPE
                if (current == PATH || SLOPE[current] == dir) {
                    moves.add(next)
                }
            }
        }
        return moves
    }

    private fun findStartingPoint(grid: Array<CharArray>): Point2D {
        val col = grid.first().indexOf('.')
        return Point2D(0, col)
    }

    private fun findEndpoint(grid: Array<CharArray>): Point2D {
        val col = grid.last().indexOf('.')
        return Point2D(grid.size - 1, col)
    }

    private fun parseToGrid(input: List<String>): Array<CharArray> {
        return input.map { it.toCharArray() }.toTypedArray()
    }


    fun part2(input: List<String>): Long {
        val grid = parseToGrid(input)
        val start = findStartingPoint(grid)
        val end = findEndpoint(grid)
        val adjacency = makeAdjacency(grid)
        return dfsOptimized(adjacency, start, end, mutableMapOf())!!
    }

    private fun makeAdjacency(grid: Array<CharArray>): Map<Point2D, Map<Point2D, Long>> {
        val adjacency: MutableMap<Point2D, MutableMap<Point2D, Long>> = mutableMapOf()
        // Create adjacency
        // Default to 1 step
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] != FOREST) {
                    val neighbors = mutableMapOf<Point2D, Long>()
                    for (dir in FOUR_DIRECTIONS) {
                        val nr = r + dir.row
                        val nc = c + dir.column
                        if (nr in grid.indices && nc in grid[0].indices && grid[nr][nc] != FOREST) {
                            neighbors[Point2D(nr, nc)] = 1
                        }
                    }
                    adjacency[Point2D(r, c)] = neighbors
                }
            }
        }

        // Merge neighbors
        adjacency.keys.toList().forEach { key ->
            adjacency[key]?.takeIf { it.size == 2 }?.let { neighbors ->
                val left = neighbors.keys.first()
                val right = neighbors.keys.last()
                val totalSteps = neighbors[left]!! + neighbors[right]!!
                adjacency.getOrPut(left) { mutableMapOf() }.merge(right, totalSteps, ::maxOf)
                adjacency.getOrPut(right) { mutableMapOf() }.merge(left, totalSteps, ::maxOf)
                listOf(left, right).forEach { adjacency[it]?.remove(key) }
                adjacency.remove(key)
            }
        }
        return adjacency
    }

    private fun dfsOptimized(
        graph: Map<Point2D, Map<Point2D, Long>>,
        cur: Point2D,
        end: Point2D,
        seen: MutableMap<Point2D, Long>
    ): Long {
        if (cur == end) {
            return seen.values.sum()
        }
        var best: Long = 0
        val neighbors = graph[cur] ?: emptyMap()
        neighbors
            .filter { (neighbor, _) -> neighbor !in seen }
            .forEach { (neighbor, steps) ->
                seen[neighbor] = steps
                val res = dfsOptimized(graph, neighbor, end, seen)
                if (res > best) {
                    best = res
                }
                seen.remove(neighbor)
            }
        return best
    }
}