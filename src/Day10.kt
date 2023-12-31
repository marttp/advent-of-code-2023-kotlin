import helper.util.EIGHT_DIRECTIONS
import helper.util.isInBounds
import helper.util.println
import helper.util.readInput
import kotlin.math.max

fun main() {
    val input1 = readInput("input-10")
    // Sample
    val sample1 = readInput("sample1-10")
    val sample2 = readInput("sample2-10")
    // Concept - Find the loop and get the farthest point
    // Part1
    check(Day10Util.part1(sample1) == 4L)
    check(Day10Util.part1(sample2) == 8L)
    val output1 = Day10Util.part1(input1)
    check(output1 == 6820L)
    output1.println()

    // Part2
    check(Day10Util.part2(readInput("sample3-10")) == 4L)
    check(Day10Util.part2(readInput("sample4-10")) == 8L)
    check(Day10Util.part2(readInput("sample5-10")) == 10L)
    val output2 = Day10Util.part2(input1)
    check(output2 == 337L)
    output2.println()
}

private object Day10Util {
    /*
        | is a vertical pipe connecting north and south.
        - is a horizontal pipe connecting east and west.
        L is a 90-degree bend connecting north and east.
        J is a 90-degree bend connecting north and west.
        7 is a 90-degree bend connecting south and west.
        F is a 90-degree bend connecting south and east.
        . is ground; there is no pipe in this tile.
        S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
    * */
    const val VERTICAL_PIPE = '|' // UP and DOWN
    const val HORIZONTAL_PIPE = '-' // LEFT and RIGHT
    const val NORTH_EAST_BEND = 'L' // UP and RIGHT
    const val NORTH_WEST_BEND = 'J' // UP and LEFT
    const val SOUTH_WEST_BEND = '7' // DOWN and LEFT
    const val SOUTH_EAST_BEND = 'F' // DOWN and RIGHT
    const val GROUND = '.'
    const val STARTING_POINT = 'S'
    const val MARKER = 'O'
    const val ENCLOSED = 'I'

    val VALID_NEXT_MOVES = setOf(
        VERTICAL_PIPE,
        HORIZONTAL_PIPE,
        NORTH_EAST_BEND,
        NORTH_WEST_BEND,
        SOUTH_WEST_BEND,
        SOUTH_EAST_BEND,
    )

    val POSSIBLE_MOVES = mapOf(
        STARTING_POINT to listOf(
            Pair(-1, 0), // Up
            Pair(0, 1), // Right
            Pair(1, 0), // Down
            Pair(0, -1), // Left
        ),
        VERTICAL_PIPE to listOf(
            Pair(-1, 0), // Up
            Pair(1, 0), // Down
        ),
        HORIZONTAL_PIPE to listOf(
            Pair(0, 1), // Right
            Pair(0, -1), // Left
        ),
        NORTH_EAST_BEND to listOf(
            Pair(-1, 0), // Up
            Pair(0, 1), // Right
        ),
        NORTH_WEST_BEND to listOf(
            Pair(-1, 0), // Up
            Pair(0, -1), // Left
        ),
        SOUTH_WEST_BEND to listOf(
            Pair(1, 0), // Down
            Pair(0, -1), // Left
        ),
        SOUTH_EAST_BEND to listOf(
            Pair(1, 0), // Down
            Pair(0, 1), // Right
        ),
    )

    fun part1(input: List<String>): Long {
        val matrix = createMatrix(input)
        val visitedMatrix = createVisitedMatrix(input)

        val startingPoint = getStartingPoint(matrix)
        val queue = ArrayDeque<Point>()
        queue.add(startingPoint)
        visitedMatrix[startingPoint.row][startingPoint.column] = true

        var answer = 0L

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            answer = max(answer, current.distance)
            val nextMoves = current.nextMoves(matrix)
                .filter { !visitedMatrix[it.row][it.column] }
            for (move in nextMoves) {
                visitedMatrix[move.row][move.column] = true
                queue.add(move)
            }
        }

        return answer
    }

    fun part2(input: List<String>): Long {
        val matrix = createMatrix(input)
        val visitedMatrix = createVisitedMatrix(input)

        val startingPoint = getStartingPoint(matrix)
        val queue = ArrayDeque<Point>()
        queue.add(startingPoint)
        visitedMatrix[startingPoint.row][startingPoint.column] = true

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val nextMoves = current.nextMoves(matrix)
                .filter { !visitedMatrix[it.row][it.column] }
            for (move in nextMoves) {
                visitedMatrix[move.row][move.column] = true
                queue.add(move)
            }
        }

        floodVisitedFill(matrix, visitedMatrix)


        var answer = 0L
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] == GROUND || !visitedMatrix[r][c]) {
                    matrix[r][c] = ENCLOSED
                    answer++
                }
            }
        }

        return answer
    }

    private fun floodVisitedFill(matrix: Array<Array<Char>>, visitedMatrix: Array<Array<Boolean>>) {
        // Most Top Row
        for (c in matrix[0].indices) {
            if (matrix[0][c] == GROUND) {
                dfs(matrix, visitedMatrix, 0, c)
            }
        }
        // Most Bottom Row
        for (c in matrix[0].indices) {
            if (matrix[matrix.size - 1][c] == GROUND) {
                dfs(matrix, visitedMatrix, matrix.size - 1, c)
            }
        }
        // Most Left Column
        for (r in matrix.indices) {
            if (matrix[r][0] == GROUND) {
                dfs(matrix, visitedMatrix, r, 0)
            }
        }
        // Most Right Column
        for (r in matrix.indices) {
            if (matrix[r][matrix[0].size - 1] == GROUND) {
                dfs(matrix, visitedMatrix, r, matrix[0].size - 1)
            }
        }
    }

    private fun dfs(matrix: Array<Array<Char>>, visitedMatrix: Array<Array<Boolean>>, r: Int, c: Int) {
        if (isInBounds(matrix, r, c) && !visitedMatrix[r][c] && matrix[r][c] == GROUND) {
            visitedMatrix[r][c] = true
            matrix[r][c] = MARKER
            for (dir in EIGHT_DIRECTIONS) {
                dfs(matrix, visitedMatrix, r + dir.first, c + dir.second)
            }
        }
    }

    fun createMatrix(input: List<String>): Array<Array<Char>> {
        // Initialize ground
        val matrix = Array(input.size) {
            Array(input[0].length) { GROUND }
        }
        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix[i][j] = input[i][j]
            }
        }
        return matrix
    }

    fun createVisitedMatrix(input: List<String>): Array<Array<Boolean>> =
        Array(input.size) {
            Array(input[0].length) { false }
        }

    fun getStartingPoint(matrix: Array<Array<Char>>): Point {
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == STARTING_POINT) {
                    return Point(STARTING_POINT, i, j)
                }
            }
        }
        throw Exception("No starting point found")
    }

    data class Point(val pipe: Char, val row: Int, val column: Int, val distance: Long = 0) {
        fun nextMoves(matrix: Array<Array<Char>>): List<Point> {
            val possibleMoves = POSSIBLE_MOVES[pipe] ?: emptyList()
            return possibleMoves.filter { isInBounds(matrix, row + it.first, column + it.second) }
                .filter { isConnecting(pipe, matrix[row + it.first][column + it.second], it.first, it.second) }
                .map {
                    Point(
                        matrix[row + it.first][column + it.second],
                        row + it.first,
                        column + it.second,
                        distance + 1
                    )
                }
                .filter { matrix[it.row][it.column] in VALID_NEXT_MOVES }
        }

        private fun isConnecting(c: Char, n: Char, dx: Int, dy: Int): Boolean {
            val up = dx == -1 && dy == 0
            val down = dx == 1 && dy == 0
            val left = dx == 0 && dy == -1
            val right = dx == 0 && dy == 1
            return when (c) {
                'S' -> (n in "|7F" && up) || (n in "|LJ" && down) || (n in "-J7" && right) || (n in "-FL" && left)
                '|' -> (n in "LJ" && down) || (n in "F7" && up) || (n == '|' && dy == 0)
                '-' -> (n in "FL" && left) || (n in "J7" && right) || (n == '-' && dx == 0)
                'J' -> (n in "7F|" && up) || (n in "LF-" && left)
                'F' -> (n in "J|L" && down) || (n in "7-J" && right)
                '7' -> (n in "FL-" && left) || (n in "J|L" && down)
                'L' -> (n in "7F|" && up) || (n in "J-7" && right)
                else -> false
            }
        }
    }
}