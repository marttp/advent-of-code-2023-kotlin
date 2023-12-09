fun main() {
    val input1 = readInput("input-9")
    // Sample
    val sample1 = readInput("sample1-9")
    // Part1
    check(Day09Util.part1(sample1) == 114L)
    val output1 = Day09Util.part1(input1)
    check(output1 == 1980437560L)
    output1.println()
    // Part2
    check(Day09Util.part2(sample1) == 2L)
    val output2 = Day09Util.part2(input1)
    check(output2 == 977L)
    output2.println()
}

private object Day09Util {
    fun part1(input: List<String>): Long {
        // Sum of all input lines
        return input.sumOf {
            // Extract line to sequence of list of longs
            // Furthermore, If one of them not equal to 0, then return the new zip sequence
            val generator = generateSequence(it.split(" ").map { it.toLong() }) { seq ->
                seq.zipWithNext { a, b -> b - a }
                    .takeIf { it.any { diff -> diff != 0L } }
            }
                .toList()
            generator.fold(0L) { acc, seq ->
                val result = acc + seq.last()
                result
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            val generator = generateSequence(it.split(" ").map { it.toLong() }) { seq ->
                seq.zipWithNext { a, b -> b - a }
                    .takeIf { it.any { diff -> diff != 0L } }
            }
                .toList()
                .reversed()
            generator.fold(0L) { acc, seq -> seq.first() - acc }
        }
    }
}