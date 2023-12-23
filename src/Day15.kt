import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-15")
    // Sample
    val sample1 = readInput("sample1-15")
    // Part1
    check(Day15Util.part1(sample1) == 1320)
    val output1 = Day15Util.part1(input1)
    check(output1 == 511215)
    output1.println()

    // Part2
    check(Day15Util.part2(sample1) == 145)
    val output2 = Day15Util.part2(input1)
    check(output2 == 236057)
    output2.println()
}

private object Day15Util {

    fun part1(input: List<String>): Int {
        val longText = input.first()
        return longText.split(",").sumOf(::hashValue)
    }

    fun part2(input: List<String>): Int {
        val longText = input.first()
        val operations = longText.split(",")
        return calculateFocusingPower(operations)
    }

    private fun calculateFocusingPower(operations: List<String>): Int {
        val labelToBox = mutableMapOf<String, Int>()
        val box = mutableMapOf<Int, LinkedHashSet<Lens>>()
        operations.forEach { ops ->
            val isDeleteOps = ops.last() == '-'
            if (isDeleteOps) {
                val label = ops.dropLast(1)
                labelToBox[label]?.let {
                    box[it]?.removeIf { lens -> lens.label == label }
                }
            } else {
                val (label, focusTxt) = ops.split("=")
                val hashLabel = hashValue(label)
                val orderLensInBox = box.getOrPut(hashLabel) { LinkedHashSet() }
                val existLens = orderLensInBox.find { it.label == label }
                if (existLens != null) {
                    existLens.focalLength = focusTxt.toInt()
                } else {
                    orderLensInBox.add(Lens(label, focusTxt.toInt()))
                }
                labelToBox[label] = hashLabel
            }
        }

        return box.entries.sumOf { (boxIndex, lenses) ->
            lenses.mapIndexed { idx, lens ->
                (boxIndex + 1) * (idx + 1) * lens.focalLength
            }
                .sum()
        }
    }

    private fun hashValue(s: String): Int {
        var currentValue = 0
        val charArray = s.toCharArray()
        for (c in charArray) {
            currentValue += c.code
            currentValue *= 17
            currentValue %= 256
        }
        return currentValue
    }

    data class Lens(val label: String, var focalLength: Int)
}