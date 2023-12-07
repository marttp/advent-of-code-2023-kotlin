import java.util.PriorityQueue

// Camel Cards - Like Poker
// But if the same type of win on both hand, start comparing from first card to last card until one card is bigger than the other
// Bid and rank - will be based on number of hands in game
fun main() {
    val input1 = readInput("input-7")
    val sample1 = readInput("sample1-7")

    val rule1Sample = Day07Rule1(sample1)
    check(WinnerUtil.totalWinnings(rule1Sample.hands) == 6440L)
    val rule1Output = WinnerUtil.totalWinnings(Day07Rule1(input1).hands)
    check(rule1Output == 250951660L)
    rule1Output.println()


    val rule2Sample = Day07Rule2(sample1)
    check(WinnerUtil.totalWinnings(rule2Sample.hands) == 5905L)
    val rule2Output = WinnerUtil.totalWinnings(Day07Rule2(input1).hands)
    check(rule2Output == 251481660L)
    rule2Output.println()

}

object WinnerUtil {

    fun totalWinnings(hands: List<Hands>): Long {
        // Max Heap based
        val leaderboard = PriorityQueue<Hands> { h1, h2 ->
            h2.compareTo(h1)
        }
        leaderboard.addAll(hands)
        var size = leaderboard.size
        var result = 0L
        while (size > 0) {
            val hand = leaderboard.poll()
            result += hand.bid * size
            size--
        }
        return result
    }
}

data class CamelCard(val label: Char, val strength: Int)
data class Hands(val camelCards: List<CamelCard>, val bid: Int, val camelCardWinType: CamelCardWinType) :
    Comparable<Hands> {
    override fun compareTo(other: Hands): Int {
        if (camelCardWinType.score == other.camelCardWinType.score) {
            // Compare from first card to last card
            for (i in 0 until other.camelCards.size) {
                if (other.camelCards[i].strength != this.camelCards[i].strength) {
                    return this.camelCards[i].strength - other.camelCards[i].strength
                }
            }
            return this.bid - other.bid
        }
        return this.camelCardWinType.score - other.camelCardWinType.score
    }
}

class Day07Rule1(input: List<String>) {

    private val possibleLabels = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
    private val cardStrength = listOf(14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    val hands = parseInput(input)

    private fun parseInput(input: List<String>): List<Hands> {
        return input.map { line ->
            val (labels, bidAmount) = line.split(" ")
            val camelCards = labels.map { label ->
                val index = possibleLabels.indexOf(label)
                val strength = cardStrength[index]
                CamelCard(label, strength)
            }
            val bid = bidAmount.toInt()
            Hands(camelCards, bid, CamelCardWinType.rule1(camelCards))
        }
    }
}

class Day07Rule2(input: List<String>) {

    private val possibleLabels = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
    private val cardStrength = listOf(14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    val hands = parseInput(input)

    private fun parseInput(input: List<String>): List<Hands> {
        return input.map { line ->
            val (labels, bidAmount) = line.split(" ")
            val camelCards = labels.map { label ->
                val strength = cardStrength[possibleLabels.indexOf(label)]
                CamelCard(label, strength)
            }
            val bid = bidAmount.toInt()
            Hands(camelCards, bid, CamelCardWinType.rule2(camelCards))
        }
    }
}

enum class CamelCardWinType(val score: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6), // FOUR_OF_A_KIND
    FIVE_OF_A_KIND(7); // STRAIGHT_FLUSH

    companion object {
        fun rule1(camelCards: List<CamelCard>): CamelCardWinType {
            val labelCount = camelCards.groupBy { it.label }
                .mapValues { it.value.size }
            val values = labelCount.values.toSet()
            when (labelCount.size) {
                1 -> return FIVE_OF_A_KIND
                2 -> {
                    if (values.contains(4)) {
                        return FOUR_OF_A_KIND
                    }
                    return FULL_HOUSE
                }
                3 -> {
                    if (values.contains(3)) {
                        return THREE_OF_A_KIND
                    }
                    return TWO_PAIR
                }
                4 -> return ONE_PAIR // That's mean at least 2 out of 5 cards are same
                else -> return HIGH_CARD // All of them different
            }
        }

        fun rule2(camelCards: List<CamelCard>): CamelCardWinType {
            val labelCount = camelCards.groupBy { it.label }
                .mapValues { it.value.size }
                .toMutableMap()
            val jokerCount = labelCount['J'] ?: 0
            if (jokerCount == 0) {
                return rule1(camelCards)
            }
            if (jokerCount == 5) {
                return FIVE_OF_A_KIND
            }
            val values = labelCount.values.toSet()
            // Remove Joker from considering and using count instead
            labelCount.remove('J')
            // Which mean at least 1 joker and 1 - 4 jokers possible
            when (labelCount.size) {
                // 1 Label and the rest are joker -> FIVE_OF_A_KIND
                1 -> return FIVE_OF_A_KIND
                2 -> {
                    // 1 Joker + 3 + 1 -> FOUR_OF_A_KIND
                    // 2 Jokers + 2 + 1 -> FOUR_OF_A_KIND
                    if (values.contains(3) || (jokerCount == 2 && values.contains(2))) {
                        return FOUR_OF_A_KIND
                    }
                    // 1 Joker + 2 + 2 -> FULL_HOUSE
                    return FULL_HOUSE
                }
                3 -> return THREE_OF_A_KIND
                else -> return ONE_PAIR
            }
        }
    }
}
