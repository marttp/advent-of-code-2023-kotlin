fun main() {
    val input1 = readInput("input-19")
    // Sample
    val sample1 = readInput("sample1-19")
    // Part1
    check(Day19Util.part1(sample1) == 19114L)
    val output1 = Day19Util.part1(input1)
    check(output1 == 418498L)
    output1.println()

    // Part2
    check(Day19Util.part2(sample1) == 167409079868000L)
    val output2 = Day19Util.part2(input1)
    check(output2 == 123331556462603L)
    output2.println()
}

private object Day19Util {

    fun part1(input: List<String>): Long {
        val (workflowInput, ratingInput) = splitWorkflowAndRating(input)
        val workflows = workflowInput.map { Workflow.from(it) }.associateBy { it.name }
        val ratings = ratingInput.map { Rating.from(it) }
        return ratings.sumOf { it.score(workflows, workflows.getValue("in")) }.toLong()
    }

    fun part2(input: List<String>): Long {
        val (workflowInput) = splitWorkflowAndRating(input)
        val workflows = workflowInput.map { Workflow.from(it) }.associateBy { it.name }
        return combinations(
            workflows = workflows,
            result = "in", // starting point
            ranges = mapOf(
                'x' to (1..4000),
                'm' to (1..4000),
                'a' to (1..4000),
                's' to (1..4000)
            )
        )
    }

    private fun splitWorkflowAndRating(input: List<String>): Pair<List<String>, List<String>> {
        val splitInput = input.joinToString("\n")
            .split("\n\n\n")[0] // Split the line contain 2 empty lines and get first element
            .split("\n\n") // Split each workflow and rating
            .map { it.split("\n") } // Split each workflow and rating into list of string
        return splitInput[0] to splitInput[1]
    }

    // DFS until reach the end of workflow R or A
    private fun Rating.score(workflows: Map<String, Workflow>, workflow: Workflow): Int {
        val rule = workflow.rules.first { it.matches(this) }
        return when (rule.result) {
            "R" -> 0
            "A" -> categories.values.sum()
            else -> score(workflows, workflows.getValue(rule.result))
        }
    }

    private fun combinations(workflows: Map<String, Workflow>, result: String, ranges: Map<Char, IntRange>): Long {
        return when (result) {
            "R" -> 0
            "A" -> ranges.values.map { it.size().toLong() }.reduce(Long::times)
            else -> {
                val newRanges = ranges.toMutableMap()

                workflows.getValue(result).rules.sumOf { rule ->
                    when (rule) {
                        is Rule.Unconditional -> combinations(workflows, rule.result, newRanges)
                        is Rule.Conditional -> {
                            val newRange = newRanges.getValue(rule.lhs).merge(rule.range())
                            val newReversed = newRanges.getValue(rule.lhs).merge(rule.reversedRange())

                            newRanges[rule.lhs] = newRange
                            combinations(workflows, rule.result, newRanges).also { newRanges[rule.lhs] = newReversed }
                        }
                    }
                }
            }
        }
    }

    private data class Workflow(val name: String, val rules: List<Rule>) {
        companion object {
            fun from(str: String): Workflow {
                val name = str.substringBefore("{")
                val rules = str.substringAfter("{").substringBefore("}").split(",").map { Rule.from(it) }
                return Workflow(name, rules)
            }
        }
    }

    private sealed class Rule {

        abstract val result: String

        data class Conditional(val lhs: Char, val op: Char, val rhs: Int, override val result: String) : Rule() {
            fun range(): IntRange = if (op == '<') (1..<rhs) else (rhs + 1..4000)
            fun reversedRange(): IntRange = if (op == '<') (rhs..4000) else (1..rhs)
        }

        data class Unconditional(override val result: String) : Rule()

        fun matches(rating: Rating): Boolean {
            return when (this) {
                is Unconditional -> true
                is Conditional -> {
                    when (op) {
                        '>' -> rating.categories.getValue(lhs) > rhs
                        '<' -> rating.categories.getValue(lhs) < rhs
                        else -> error("Unsupported operation: $op")
                    }
                }
            }
        }

        companion object {
            fun from(str: String): Rule {
                return if (':' in str) {
                    val condition = str.substringBefore(":")
                    val result = str.substringAfter(":")
                    Conditional(condition[0], condition[1], condition.substring(2).toInt(), result)
                } else {
                    Unconditional(str)
                }
            }
        }
    }

    private data class Rating(val categories: Map<Char, Int>) {
        companion object {
            fun from(str: String): Rating {
                val categories = str.drop(1).dropLast(1).split(",").associate {
                    it.substringBefore("=").single() to it.substringAfter("=").toInt()
                }
                return Rating(categories)
            }
        }
    }

    private fun IntRange.size() = last - start + 1

    private fun IntRange.merge(other: IntRange) = (maxOf(first, other.first)..minOf(last, other.last))
}