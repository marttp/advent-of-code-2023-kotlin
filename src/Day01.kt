fun main() {

    fun getFirstAndLastDigit(inputLine: String): Pair<Digit, Digit> {
        val firstDigit = Digit(0, inputLine.length)
        val lastDigit = Digit(0, -1)

        inputLine.forEachIndexed { index, c ->
            val isDigit = c.isDigit()
            if (isDigit) {
                if (firstDigit.value == 0) {
                    firstDigit.value = c.digitToInt()
                    firstDigit.index = index
                }
                lastDigit.value = c.digitToInt()
                lastDigit.index = index
            }
        }

        return Pair(firstDigit, lastDigit)
    }

    fun getFirstAndLastByText(inputLine: String): Pair<Digit, Digit> {
        val keys = digitMap.keys
        val digitStore = arrayOf(
            // First digit - lowest index
            Digit(0, inputLine.length),
            // Last digit - highest index
            Digit(0, -1)
        )
        for (k in keys) {
            val firstFoundIndex = inputLine.indexOf(k)
            // Found digit
            if (firstFoundIndex != -1) {
                val digit = digitMap[k]!!
                if (firstFoundIndex < digitStore[0].index) {
                    digitStore[0] = Digit(digit, firstFoundIndex)
                }
                val lastFoundIndex = inputLine.lastIndexOf(k)
                if (lastFoundIndex > digitStore[1].index) {
                    digitStore[1] = Digit(digit, lastFoundIndex)
                }
            }
        }
        return Pair(digitStore[0], digitStore[1])
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        for (inputLine in input) {
            val (firstDigit, lastDigit) = getFirstAndLastDigit(inputLine)
            sum = sum.plus(firstDigit.value * 10 + lastDigit.value)
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        for (inputLine in input) {
            val (firstDigit, lastDigit) = getFirstAndLastDigit(inputLine)
            val (firstDigitByText, lastDigitByText) = getFirstAndLastByText(inputLine)

            val finalFirstDigit = if (firstDigit.index < firstDigitByText.index) firstDigit.value else firstDigitByText.value
            val finalLastDigit = if (lastDigit.index > lastDigitByText.index) lastDigit.value else lastDigitByText.value

            sum = sum.plus(finalFirstDigit * 10 + finalLastDigit)
        }
        return sum
    }

    val input1 = readInput("input-1")

    val sample1 = readInput("sample1-1")
    check(part1(sample1) == 142)
    part1(input1).println()

    val sample2 = readInput("sample2-1")
    check(part2(sample2) == 281)
    part2(input1).println()
}

val digitMap = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)

data class Digit(var value: Int, var index: Int)