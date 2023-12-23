import helper.util.MathUtils
import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-20")
    // Sample
    val sample1 = readInput("sample1-20")
    // Part1
    check(Day20(sample1).part1() == 32000000)
    val output1 = Day20(input1).part1()
    check(output1 == 818649769)
    output1.println()

    // Part2
    val output2 = Day20(input1).part2()
    check(output2 == 246313604784977L)
    output2.println()
}

class Day20(input: List<String>) {

    private val modules: Map<String, Module> = parseModules(input)

    fun part1(): Int {
        val transmitter = Part1Transmitter()
        repeat(1000) {
            button(transmitter)
        }
        return transmitter.high * transmitter.low
    }

    fun part2(): Long {
        val sourceForRx = modules.values.first { "rx" in it.destinations }
        val lookFor = modules.values
            .filter { sourceForRx.name in it.destinations }.toMutableSet()
            .associate { it.name to 0L }
            .toMutableMap()
        val transmitter = Part2Transmitter(lookFor.keys)
        var count = 0

        while (lookFor.values.any { it == 0L }) {
            count++
            button(transmitter)
            transmitter.seen.forEach { name ->
                if (lookFor.getValue(name) == 0L) {
                    lookFor[name] = count.toLong()
                }
            }
            transmitter.seen.clear()
        }

        return lookFor.values.reduce(MathUtils::lcm)
    }

    private fun button(transmitter: Transmitter? = null) {
        val messages = ArrayDeque<Pulse>().apply {
            add(Pulse(false, "button", "broadcaster"))
        }
        while (messages.isNotEmpty()) {
            with(messages.removeFirst()) {
                transmitter?.pulse(this)
                modules[destination]?.receive(this)?.forEach { messages.add(it) }
            }
        }
    }

    private fun parseModules(input: List<String>): Map<String, Module> {
        val modules = input.associate { row ->
            val type = row.first()
            val name = row.substring(1).substringBefore(" ")
            val destinations = row.substringAfter(">").split(",").map { it.trim() }.filter { it.isNotEmpty() }

            when (type) {
                'b' -> "broadcaster" to Broadcaster(destinations)
                '&' -> name to Conjunction(name, destinations)
                '%' -> name to FlipFlop(name, destinations)
                else -> throw IllegalArgumentException("No such module: $type from $row")
            }
        }

        val conjunctions = modules.values.filterIsInstance<Conjunction>().associateBy { it.name }
        modules.values.forEach { module ->
            module.destinations.forEach { destination ->
                conjunctions[destination]?.addSource(module.name)
            }
        }
        return modules
    }

    data class Pulse(val high: Boolean, val source: String, val destination: String)

    private abstract class Module(val name: String, val destinations: List<String>) {
        abstract fun receive(pulse: Pulse): List<Pulse>
        fun send(high: Boolean): List<Pulse> = destinations.map { Pulse(high, name, it) }
    }

    private class Broadcaster(destinations: List<String>) : Module("broadcaster", destinations) {
        override fun receive(pulse: Pulse): List<Pulse> = send(pulse.high)
    }

    private class FlipFlop(name: String, destinations: List<String>) : Module(name, destinations) {
        private var on = false

        override fun receive(pulse: Pulse): List<Pulse> =
            if (pulse.high) {
                emptyList()
            } else {
                on = !on
                send(on)
            }
    }

    private class Conjunction(name: String, destinations: List<String>) : Module(name, destinations) {
        private val memory = mutableMapOf<String, Boolean>()

        fun addSource(source: String) {
            if (source !in memory) memory[source] = false
        }

        override fun receive(pulse: Pulse): List<Pulse> {
            memory[pulse.source] = pulse.high
            return send(!memory.values.all { it })
        }
    }

    private interface Transmitter {
        fun pulse(pulse: Pulse)
    }

    private class Part1Transmitter : Transmitter {
        var high = 0
        var low = 0
        override fun pulse(pulse: Pulse) {
            if (pulse.high) {
                high++
            } else {
                low++
            }
        }
    }

    private class Part2Transmitter(val sources: Set<String>, val seen: MutableSet<String> = mutableSetOf()) :
        Transmitter {
        override fun pulse(pulse: Pulse) {
            if (pulse.high && pulse.source in sources) {
                seen.add(pulse.source)
            }
        }
    }
}