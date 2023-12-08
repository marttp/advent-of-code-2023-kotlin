fun main() {
    val input1 = readInput("input-8")
    // Sample-Part1
    val sample1 = readInput("sample1-8")
    check(Day08Util.part1(sample1) == 2)
    val sample2 = readInput("sample2-8")
    check(Day08Util.part1(sample2) == 6)
    // Output-Part1
    val output1 = Day08Util.part1(input1)
    check(output1 == 20777)
    output1.println()

    // Sample-Part2
    val sample3 = readInput("sample3-8")
    check(Day08Util.part2(sample3) == 6L)
    // Output-Part2
    val output2 = Day08Util.part2(input1)
    check(output2 == 13289612809129)
    output2.println()
}

private object Day08Util {

    private const val START_LOCATION = "AAA"
    private const val END_LOCATION = "ZZZ"
    private const val SUFFIX_START = 'A'
    private const val SUFFIX_END = 'Z'

    fun part1(input: List<String>): Int {
        val directions = getDirections(input)
        val start = getStartPosition(input)
        var current = start
        var steps = 0
        var currentDirectionIdx = 0
        while (current.position != END_LOCATION) {
            val directionOrder = directions[currentDirectionIdx]
            current = moveToNode(directionOrder, current)
            steps++
            // It will reset to 0 when out of range
            currentDirectionIdx = (currentDirectionIdx + 1) % directions.size
        }
        return steps
    }

    fun part2(input: List<String>): Long {
        val directions = getDirections(input)
        val currentMultiverseNodes = getStartPositionMultiverse(input)
        return currentMultiverseNodes.map { node ->
            var steps = 0L
            var currentDirectionIdx = 0
            var current = node
            while (!current.position.endsWith(SUFFIX_END)) {
                val directionOrder = directions[currentDirectionIdx]
                current = moveToNode(directionOrder, current)
                steps++
                // It will reset to 0 when out of range
                currentDirectionIdx = (currentDirectionIdx + 1) % directions.size
            }
            steps
        }
            .reduce(MathUtils::lcm)
    }

    private fun getDirections(input: List<String>): List<Char> {
        return input.first().toList()
    }

    private fun buildGraph(input: List<String>): Map<String, Node> {
        val graph = mutableMapOf<String, Node>()
        input.drop(2).forEach {
            val (name, neighbors) = it.split("=").map { it.trim() }
            val (left, right) = neighbors.substring(1, neighbors.length - 1).split(",").map { it.trim() }
            graph.putIfAbsent(name, Node(name)) // Current position
            graph.putIfAbsent(left, Node(left)) // Left direction
            graph.putIfAbsent(right, Node(right)) // Right direction
            // Connect network
            graph[name]!!.left = graph[left]
            graph[name]!!.right = graph[right]
        }
        return graph
    }

    private fun getStartPosition(input: List<String>): Node {
        val graph = buildGraph(input)
        return graph[START_LOCATION]!!
    }

    private fun getStartPositionMultiverse(input: List<String>): List<Node> {
        val graph = buildGraph(input)
        return graph.keys.filter { it.endsWith(SUFFIX_START) }.map { graph[it]!! }
    }

    private fun moveToNode(directionOrder: Char, current: Node) = when (directionOrder) {
        'L' -> current.left!!
        'R' -> current.right!!
        else -> throw IllegalArgumentException("Invalid direction: $directionOrder")
    }

    data class Node(val position: String, var left: Node? = null, var right: Node? = null)
}
