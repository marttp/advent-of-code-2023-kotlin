import helper.point.Point2D
import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-18")
    // Sample
    val sample1 = readInput("sample1-18")
    // Part1
    check(Day18Util.part1(sample1) == 62L)
    val output1 = Day18Util.part1(input1)
    check(output1 == 49578L)
    output1.println()

    // Part2
    check(Day18Util.part2(sample1) == 952408144115L)
    val output2 = Day18Util.part2(input1)
    check(output2 == 52885384955882L)
    output2.println()
}

private object Day18Util {

    fun part1(input: List<String>): Long {
        return lavaCubicMeters(parseInput(input))
    }

    fun part2(input: List<String>): Long {
        return lavaCubicMeters(parseInput(input, 2))
    }

    private fun parseInput(input: List<String>, version: Int = 1): List<Dig> {
        return when (version) {
            1 -> input.map { Dig.from(it, isColor = false) }
            2 -> input.map { Dig.from(it, isColor = true) }
            else -> throw IllegalArgumentException("Invalid version: $version")
        }
    }

    private fun lavaCubicMeters(digs: List<Dig>): Long {
        val border = digs.sumOf { it.meters }
        val vertices = digs.runningFold(Point2D(0, 0)) { last, dig -> last + dig.dir * dig.meters }
        val interior = shoelaceArea(vertices) - (border / 2) + 1
        return interior + border
    }

    private fun shoelaceArea(vertices: List<Point2D>): Long {
        return vertices.indices.sumOf { i ->
            val (y1, x1) = vertices[i]
            val (y2, x2) = vertices[(i + 1) % vertices.size]
            x1.toLong() * y2 - y1.toLong() * x2
        } / 2
    }

    private data class Dig(val dir: Point2D, val meters: Int) {
        companion object {
            private val mapping = mapOf(
                'U' to Point2D.NORTH,
                'D' to Point2D.SOUTH,
                'L' to Point2D.WEST,
                'R' to Point2D.EAST,

                '0' to Point2D.EAST,
                '1' to Point2D.SOUTH,
                '2' to Point2D.WEST,
                '3' to Point2D.NORTH,
            )

            /*
            * Each hexadecimal code is six hexadecimal digits long.
            * The first five hexadecimal digits encode the distance in meters as a five-digit hexadecimal number.
            * The last hexadecimal digit encodes the direction to dig:
            * 0 means R, 1 means D, 2 means L, and 3 means U.
            *
            * Example: R 6 (#70c710) => # 70c71 0 = R 461937
            * */
            fun from(line: String, isColor: Boolean): Dig {
                val (dir, meters, color) = line.split(" ")
                return Dig(
                    dir = if (isColor) mapping.getValue(color[7]) else mapping.getValue(dir.single()),
                    // toInt(16) => hexadecimal to decimal
                    meters = if (isColor) color.substring(2..6).toInt(16) else meters.toInt(),
                )
            }
        }
    }
}