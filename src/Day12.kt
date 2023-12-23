import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-12")
    // Sample
    val sample1 = readInput("sample1-12")
    // Part1
    check(Day12Util.part1(sample1) == 21L)
    val output1 = Day12Util.part1(input1)
    check(output1 == 7402L)
    output1.println()

    // Part2
    check(Day12Util.part2(sample1) == 525152L)
    val output2 = Day12Util.part2(input1)
    check(output2 == 3384337640277L)
    output2.println()
}

private object Day12Util {

    fun part1(input: List<String>): Long {
        val mappingInputPair = transformToInputPair(input)
        // Find sum of arrangement pattern
        return mappingInputPair.sumOf {
            getArrangementPattern(it.first, it.second)
        }
    }

    // 5 copies per each
    fun part2(input: List<String>): Long {
        val mappingInputPair = transformToInputPair(input)
        return mappingInputPair.sumOf {
            val (spring, numPattern) = it
            val spring5Time = buildList {
                repeat(5) {
                    add(spring)
                }
            }
            val numAllPattern = buildList {
                repeat(5) {
                    addAll(numPattern)
                }
            }
            getArrangementPattern(spring5Time.joinToString("?"), numAllPattern)
        }
    }

    // Using memoization for explore the possible arrangement pattern
    private fun getArrangementPattern(spring: String, numPattern: List<Int>): Long {
        val memo = IntArray(spring.length) { i ->
            // Drop first i elements
            // Then find the distance to the next dot - Store in length number format
            spring.drop(i).takeWhile { it != '.' }.length
        }
        val dp = mutableMapOf<Pair<Int, Int>, Long>()

        fun canTake(idx: Int, targetNumber: Int) =
            memo[idx] >= targetNumber && (idx + targetNumber == spring.length || spring[idx + targetNumber] != '#')

        fun helper(springIdx: Int, numPatternIdx: Int): Long {
            return when {
                numPatternIdx == numPattern.size -> if (spring.drop(springIdx).none { it == '#' }) 1L else 0L
                springIdx >= spring.length -> 0L // No more pattern to match
                else -> {
                    // Memoization
                    if (dp[springIdx to numPatternIdx] == null) {
                        val take = if (canTake(springIdx, numPattern[numPatternIdx])) {
                            helper(springIdx + numPattern[numPatternIdx] + 1, numPatternIdx + 1)
                        } else 0L
                        val dontTake = if (spring[springIdx] != '#') {
                            helper(springIdx + 1, numPatternIdx)
                        } else 0L
                        dp[springIdx to numPatternIdx] = take + dontTake
                    }
                    // Get from existing memo
                    dp[springIdx to numPatternIdx]!!
                }
            }
        }

        return helper(0, 0)
    }

    private fun transformToInputPair(input: List<String>): List<Pair<String, List<Int>>> {
        return input
            .map { line ->
                line.split(" ")
                    .let { (left, right) -> left to right.split(",").map { it.toInt() } }
            }
    }
}