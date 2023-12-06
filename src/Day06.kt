import java.math.BigInteger

fun main() {
    val input1 = readInput("input-6")

    // starting speed of zero millimeters per millisecond
    // e.g. Hold the button for 4 milliseconds -> 4 millimeters/millisecond
    // if at least 7 milliseconds -> 4 for holding button + 3 * 4 distance in millimeters
    val sample1 = readInput("sample1-6")
    check(Day06Util.part1(sample1) == BigInteger.valueOf(288))
    val output1 = Day06Util.part1(input1)
    check(output1 == BigInteger.valueOf(1195150))
    output1.println()

    check(Day06Util.part2(sample1) == BigInteger.valueOf(71503))
    val output2 = Day06Util.part2(input1)
    check(output2 == BigInteger.valueOf(42550411))
    output2.println()
}

private object Day06Util {

    fun part1(input: List<String>): BigInteger {
        val maxRecordList = readInputToMaxRecordList(input)
        return maxRecordList.map { it.possibleWayToBeat() }
            .reduce { acc, unit -> acc * unit }
    }

    fun part2(input: List<String>): BigInteger {
        val maxRecord = readInputFixKerningRecord(input)
        return maxRecord.possibleWayToBeat()
    }

    private fun readInputToMaxRecordList(input: List<String>): List<MaxRaceRecord> {
        val timeInput = input[0].removePrefix("Time:")
            .split(" ")
            .filter { it.isNotBlank() }
        val distanceInput = input[1].removePrefix("Distance:")
            .split(" ")
            .filter { it.isNotBlank() }
        return timeInput.zip(distanceInput) { time, distance ->
            MaxRaceRecord(time.toBigInteger(), distance.toBigInteger())
        }
    }

    private fun readInputFixKerningRecord(input: List<String>): MaxRaceRecord {
        val timeInput = input[0].removePrefix("Time:")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(separator = "")
        val distanceInput = input[1].removePrefix("Distance:")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(separator = "")
        return MaxRaceRecord(timeInput.toBigInteger(), distanceInput.toBigInteger())
    }

    data class MaxRaceRecord(val time: BigInteger, val distance: BigInteger) {
        fun possibleWayToBeat(): BigInteger {
            var spendTime = BigInteger.valueOf(0)
            var speed = BigInteger.valueOf(0)
            var counter = BigInteger.valueOf(0)
            while (spendTime < time) {
                val calculateDistance = (time - spendTime) * speed
                if (distance < calculateDistance) {
                    counter++
                }
                spendTime++
                speed++
            }
            return counter
        }
    }
}
