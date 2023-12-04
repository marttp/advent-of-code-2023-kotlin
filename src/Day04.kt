import kotlin.math.pow

fun main() {
    val input1 = readInput("input-4")

    val sample1 = readInput("sample1-4")
    check(Day04Util.part1(sample1) == 13)
    Day04Util.part1(input1).println()

    val sample2 = readInput("sample2-4")
    check(Day04Util.part2(sample2) == 30)
    Day04Util.part2(input1).println()
}

private object Day04Util {
    fun part1(input: List<String>): Int {
        val cardPointList = readToCardPoint(input)
        return cardPointList.sumOf { cardPoint -> cardPoint.points }
    }

    fun part2(input: List<String>): Int {
        val cardPointList = readToCardPoint(input)
        val scratchCardCounter = IntArray(cardPointList.size)
        cardPointList.forEachIndexed { index, cardPoint ->
            val matchingSize = cardPoint.intersectSize
            val isThisCardWin = matchingSize != 0
            scratchCardCounter[index] += 1 // Counter 1 by original
            if (isThisCardWin) {
                val next = index + matchingSize
                for (i in index + 1..next) {
                    if (i < scratchCardCounter.size) {
                        scratchCardCounter[i] += scratchCardCounter[index]
                    }
                }
            }
        }
        return scratchCardCounter.sum()
    }

    private fun readToCardPoint(input: List<String>): List<CardPoint> {
        val cardPointList = input.map { line ->
            val cards = line.split(":")[1].trim()
            val separatedCards = cards.split("|").map { it.trim() }
            val winningNumbers = separatedCards[0].split(" ").filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            val haveNumbers = separatedCards[1].split(" ").filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            val matchedSet = haveNumbers.intersect(winningNumbers)
            val matchedSize = matchedSet.size
            val points = when (matchedSize) {
                0 -> 0
                else -> {
                    2.0.pow((matchedSize - 1).toDouble()).toInt()
                }
            }
            CardPoint(
                winningNumberSet = winningNumbers,
                haveNumberSet = haveNumbers,
                points = points,
                intersectSize = matchedSize
            )
        }
        return cardPointList
    }

    data class CardPoint(
        val winningNumberSet: Set<Int>,
        val haveNumberSet: Set<Int>,
        val points: Int,
        val intersectSize: Int
    )
}
