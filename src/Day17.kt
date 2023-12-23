import helper.graph.findShortestPathByPredicate
import helper.point.Point2D
import helper.util.isInBounds
import helper.util.println
import helper.util.readInput

typealias Rule = Int

fun main() {
    val input1 = readInput("input-17")
    // Sample
    val sample1 = readInput("sample1-17")
    // Part1
    check(Day17Util.part1(sample1) == 102)
    val output1 = Day17Util.part1(input1)
    check(output1 == 953)
    output1.println()

    // Part2
    val sample2 = readInput("sample2-17")
    check(Day17Util.part2(sample1) == 94)
    check(Day17Util.part2(sample2) == 71)
    val output2 = Day17Util.part2(input1)
    check(output2 == 1180)
    output2.println()
}

private object Day17Util {

    private val ALTERNATIVE_DIRECTION_MAP = mapOf(
        Point2D.NORTH to listOf(Point2D.WEST, Point2D.EAST),
        Point2D.SOUTH to listOf(Point2D.WEST, Point2D.EAST),
        Point2D.WEST to listOf(Point2D.NORTH, Point2D.SOUTH),
        Point2D.EAST to listOf(Point2D.NORTH, Point2D.SOUTH)
    )

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        val start = PointWithDirection(Point2D(0, 0), Point2D.EAST, 0)
        val end = Point2D(map.lastIndex, map[0].lastIndex)

        val path = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.findNextPoints(1).filter { map.isInBounds(it.point) } },
            { _, (point) -> map[point.row][point.column] })
        return path.getScore()
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)
        val start = PointWithDirection(Point2D(0, 0), Point2D.EAST, 0)
        val end = Point2D(map.lastIndex, map[0].lastIndex)

        val path = findShortestPathByPredicate(
            start,
            // It needs to move a minimum of four blocks in that direction before it can turn (or even before it can stop at the end).
            { (p, _, count) -> p == end && count >= 4 },
            { it.findNextPoints(2).filter { map.isInBounds(it.point) } },
            { _, (point) -> map[point.row][point.column] })

        return path.getScore()
    }

    private fun parseInput(input: List<String>): List<List<Int>> {
        return input.map { it.map { it.digitToInt() } }
    }

    data class PointWithDirection(
        val point: Point2D,
        val direction: Point2D,
        var counter: Int = 0
    ) {
        fun findNextPoints(selectedRule: Rule): List<PointWithDirection> {
            return when (selectedRule) {
                1 -> {
                    val nextPoints = mutableListOf<PointWithDirection>()
                    // It possible to keep current direction for 3 times
                    if (counter < 3) {
                        val sameDirection = this.copy(point = point + direction, counter = counter + 1)
                        nextPoints.add(sameDirection)
                    }
                    for (dir in ALTERNATIVE_DIRECTION_MAP[direction]!!) {
                        val nextPoint = this.copy(
                            point = point + dir,
                            direction = dir,
                            counter = 1
                        )
                        nextPoints.add(nextPoint)
                    }
                    nextPoints
                }
                2 -> {
                    /*
                    * Once an ultra crucible starts moving in a direction,
                    * it needs to move a minimum of four blocks in that direction before it can turn (or even before it can stop at the end).
                    * However, it will eventually start to get wobbly: an ultra crucible can move a maximum of ten consecutive blocks without turning.
                    * */
                    val nextPoints = mutableListOf<PointWithDirection>()
                    // It possible to keep current direction for 3 times
                    if (counter < 10) {
                        val sameDirection = this.copy(point = point + direction, counter = counter + 1)
                        nextPoints.add(sameDirection)
                    }
                    if (counter >= 4) {
                        for (dir in ALTERNATIVE_DIRECTION_MAP[direction]!!) {
                            val nextPoint = this.copy(
                                point = point + dir,
                                direction = dir,
                                counter = 1
                            )
                            nextPoints.add(nextPoint)
                        }
                    }
                    nextPoints
                }
                else -> throw IllegalArgumentException("Unknown rule $selectedRule")
            }
        }
    }
}