import com.microsoft.z3.Context
import com.microsoft.z3.Status
import helper.util.println
import helper.util.readInput
import java.math.BigDecimal

fun main() {
    val input1 = readInput("input-24")
    // Sample
    val sample1 = readInput("sample1-24")
    // Part1
    check(Day24Util.part1(sample1, BigDecimal("7")..BigDecimal("27")) == 2)
    val output1 = Day24Util.part1(input1)
    check(output1 == 12740)
    output1.println()

    // Part2
    val output2 = Day24Util.part2(input1)
    check(output2 == 741991571910536L)
    output2.println()
}

private object Day24Util {

    fun part1(
        input: List<String>,
        range: ClosedRange<BigDecimal> = BigDecimal("200000000000000")..BigDecimal("400000000000000")
    ): Int {
        val hailstones = input.map {
            it.split(" @ ", ", ").map { BigDecimal(it.trim()) }
                .let { (sx, sy, _, vx, vy) -> Hailstone(sx, sy, vx, vy) }
        }

        return hailstones.zipWithAllUnique().count { (f, s) ->
            if (f.a * s.b != f.b * s.a) {
                val x = (f.c * s.b - s.c * f.b) / (f.a * s.b - s.a * f.b)
                val y = (s.c * f.a - f.c * s.a) / (f.a * s.b - s.a * f.b)
                x in range && y in range && listOf(
                    f,
                    s
                ).all { (x - it.x) * it.dx >= BigDecimal.ZERO && (y - it.y) * it.dy >= BigDecimal.ZERO }
            } else false
        }
    }

    private fun <T> List<T>.zipWithAllUnique(): List<Pair<T, T>> {
        val result = mutableListOf<Pair<T, T>>()
        val seenPairs = mutableSetOf<Pair<T, T>>()

        for (i in this.indices) {
            for (j in i + 1 until this.size) {
                val pair = Pair(this[i], this[j])
                if (pair !in seenPairs) {
                    result.add(pair)
                    seenPairs.add(pair)
                    seenPairs.add(pair.copy(first = pair.second, second = pair.first)) // Add reverse pair as well
                }
            }
        }
        return result
    }

    data class Hailstone(val x: BigDecimal, val y: BigDecimal, val dx: BigDecimal, val dy: BigDecimal) {
        val a = dy
        val b = -dx
        val c = dy * x - dx * y
    }

    fun part2(input: List<String>): Long {
        val hail = input.map { it.split(" @ ", ", ").map { it.trim().toLong() } }
        val ctx = Context()
        val solver = ctx.mkSolver()
        val mx = ctx.mkRealConst("mx")
        val m = ctx.mkRealConst("m")
        val mz = ctx.mkRealConst("mz")
        val mxv = ctx.mkRealConst("mxv")
        val mv = ctx.mkRealConst("mv")
        val mzv = ctx.mkRealConst("mzv")

        repeat(3) {
            val (sx, sy, sz, sxv, syv, szv) = hail[it]
            val t = ctx.mkRealConst("t$it")
            solver.add(ctx.mkEq(ctx.mkAdd(mx, ctx.mkMul(mxv, t)), ctx.mkAdd(ctx.mkReal(sx.toString()), ctx.mkMul(ctx.mkReal(sxv.toString()), t))))
            solver.add(ctx.mkEq(ctx.mkAdd(m, ctx.mkMul(mv, t)), ctx.mkAdd(ctx.mkReal(sy.toString()), ctx.mkMul(ctx.mkReal(syv.toString()), t))))
            solver.add(ctx.mkEq(ctx.mkAdd(mz, ctx.mkMul(mzv, t)), ctx.mkAdd(ctx.mkReal(sz.toString()), ctx.mkMul(ctx.mkReal(szv.toString()), t))))
        }

        if (solver.check() == Status.SATISFIABLE) {
            val model = solver.model
            val solution = listOf(mx, m, mz).sumOf { model.eval(it, false).toString().toDouble() }
            return solution.toLong()
        }

        return 0
    }

    operator fun <T> List<T>.component6(): T {
        return this[5]
    }
}