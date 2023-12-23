import helper.util.Point3D
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input1 = readInput("input-22")
    // Sample
    val sample1 = readInput("sample1-22")
    // Part1
    check(Day22Util.part1(sample1) == 5)
    val output1 = Day22Util.part1(input1)
    check(output1 == 386)
    output1.println()

    // Part2
    val output2 = Day22Util.part2(input1)
    check(output2 == 39933)
    output2.println()
}

private object Day22Util {

    fun part1(input: List<String>): Int {
        val bricks = settleBricks(parseBricks(input))
        val (supports, supportedBy) = calculateSupports(bricks)
        return bricks.indices.count { i ->
            supports[i]?.all { j -> (supportedBy[j]?.size ?: 3) >= 2 } ?: true
        }
    }

    fun part2(input: List<String>): Int {
        val bricks = settleBricks(parseBricks(input))
        val (supports, supportedBy) = calculateSupports(bricks)
        return bricks.indices.sumOf { i -> findFalling(supports, supportedBy, i).size - 1 }
    }

    private fun parseBricks(input: List<String>): List<Pair<Point3D, Point3D>> =
        input.map {
            val (a, b) = it.split("~")
            Point3D.of(a) to Point3D.of(b)
        }.sortedBy { it.first.z }

    private fun settleBricks(bricks: List<Pair<Point3D, Point3D>>): List<Pair<Point3D, Point3D>> {
        val settledBricks = bricks.toMutableList()
        var changed: Boolean
        do {
            changed = false
            settledBricks.indices.forEach {
                val brick = settledBricks[it]
                if (settledBricks.none {
                        max(brick.first.x, it.first.x) <= min(
                            brick.second.x,
                            it.second.x
                        ) && max(brick.first.y, it.first.y) <= min(
                            brick.second.y,
                            it.second.y
                        ) && brick.first.z == it.second.z + 1
                    } && brick.first.z != 1) {
                    settledBricks[it] = brick.first.down to brick.second.down
                    changed = true
                }
            }
        } while (changed)
        return settledBricks
    }

    private fun calculateSupports(bricks: List<Pair<Point3D, Point3D>>): Pair<Map<Int, Set<Int>>, Map<Int, Set<Int>>> {
        val supports = hashMapOf<Int, HashSet<Int>>()
        val supportedBy = hashMapOf<Int, HashSet<Int>>()
        bricks.indices.forEach { j ->
            bricks.take(j).indices.forEach { i ->
                if ((bricks[i].first.x..bricks[i].second.x).intersect(bricks[j].first.x..bricks[j].second.x)
                        .isNotEmpty() &&
                    (bricks[i].first.y..bricks[i].second.y).intersect(bricks[j].first.y..bricks[j].second.y)
                        .isNotEmpty() &&
                    bricks[j].first.z == bricks[i].second.z + 1
                ) {
                    supports.computeIfAbsent(i) { hashSetOf() }.add(j)
                    supportedBy.computeIfAbsent(j) { hashSetOf() }.add(i)
                }
            }
        }
        return supports to supportedBy
    }

    private fun findFalling(supports: Map<Int, Set<Int>>, supportedBy: Map<Int, Set<Int>>, index: Int): Set<Int> {
        val queue = ArrayDeque(supports[index]?.filter { j -> supportedBy[j]?.size == 1 } ?: listOf())
        val falling = mutableSetOf(index).apply { addAll(queue) }
        while (queue.isNotEmpty()) {
            val j = queue.removeFirst()
            supports[j]?.filterNot { it in falling }?.forEach { k ->
                if (supportedBy[k]?.all { it in falling } == true) {
                    queue.addLast(k)
                    falling.add(k)
                }
            }
        }
        return falling
    }
}