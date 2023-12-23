import helper.util.println
import helper.util.readInput
import kotlin.math.max

fun main() {
    val input1 = readInput("input-2")

    val sample1 = readInput("sample1-2")
    check(Day02Util.part1(sample1) == 8)
    Day02Util.part1(input1).println()

    check(Day02Util.part2(sample1) == 2286)
    Day02Util.part2(input1).println()
}

private object Day02Util {

    // The Elf would first like to know
    // which games would have been possible
    // if the bag contained only 12 red cubes, 13 green cubes, and 14 blue cubes?
    val MAX_RULE = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14
    )

    fun part1(input: List<String>): Int {
        var sum = 0
        for (inputLine in input) {
            val gameInfo = collectStone(inputLine)
            if (gameInfo.stoneBag.maxRed <= MAX_RULE["red"]!! &&
                gameInfo.stoneBag.maxGreen <= MAX_RULE["green"]!! &&
                gameInfo.stoneBag.maxBlue <= MAX_RULE["blue"]!!
            ) {
                sum += gameInfo.id
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        for (inputLine in input) {
            val stoneBag = collectStone(inputLine).stoneBag
            sum += stoneBag.maxRed * stoneBag.maxGreen * stoneBag.maxBlue
        }
        return sum
    }

    fun collectStone(inputLine: String): Game {
        val gameIdStoneSplit = inputLine.split(":")
        val gameId = gameIdStoneSplit[0].trim().split(" ")[1].toInt()
        val stones = gameIdStoneSplit[1].trim().split(";")
        val stoneBag = StoneBag(
            maxRed = 0,
            maxGreen = 0,
            maxBlue = 0
        )
        for (stonePickCount in stones) {
            val stoneCountByColorList = stonePickCount.trim().split(",")
            for (stoneWithColor in stoneCountByColorList) {
                val s = stoneWithColor.trim().split(" ")
                val amount = s[0].trim().toInt()
                val color = s[1].trim()
                when (color) {
                    "red" -> stoneBag.maxRed = max(stoneBag.maxRed, amount)
                    "green" -> stoneBag.maxGreen = max(stoneBag.maxGreen, amount)
                    "blue" -> stoneBag.maxBlue = max(stoneBag.maxBlue, amount)
                }
            }
        }
        return Game(
            id = gameId,
            stoneBag = stoneBag
        )
    }

    data class Game(val id: Int, val stoneBag: StoneBag)

    data class StoneBag(var maxRed: Int, var maxGreen: Int, var maxBlue: Int)
}
