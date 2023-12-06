import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun main() {
    val input1 = readInput("input-5")

    val sample1 = readInput("sample1-5")
//    check(Day05Util.part1(sample1) == 35L)
//    val result1 = Day05Util.part1(input1)
//    result1.println()
//    check(result1 == 107430936L)
//
//    check(Day05Util.part2(sample1) == 46L)
//    val result2 = Day05Util.part2(input1)
//    result2.println()
//    check(result2 == 23738616L)

    check(Day05Alternative.part1(sample1) == 35L)
    val result1 = Day05Alternative.part1(input1)
    result1.println()
    check(result1 == 107430936L)

    check(Day05Alternative.part2(sample1) == 46L)
    val result2 = Day05Alternative.part2(input1)
    result2.println()
    check(result2 == 23738616L)
}

private object Day05Util {
    fun part1(input: List<String>): Long {
        val addressMap = AddressMap.createAddressMap(input, 1)
        return getLocation(addressMap)
    }

    fun part2(input: List<String>): Long {
        val addressMap = AddressMap.createAddressMap(input, 2)
        return getLocation(addressMap)
    }

    private fun getLocation(addressMap: AddressMap): Long {
        return runBlocking {
            val locationResultAsync = addressMap.seedInput.map { seedInput ->
                async(Dispatchers.Default) {
                    var minLocation = Long.MAX_VALUE
                    var seed = seedInput.seed
                    val maxSeedRange = seedInput.seed + seedInput.range
                    while (seed < maxSeedRange) {
                        val soil = addressMap.seedToSoilRange.mapRange(seed)
                        val fertilizer = addressMap.soilToFertilizerRange.mapRange(soil)
                        val water = addressMap.fertilizerToWaterRange.mapRange(fertilizer)
                        val light = addressMap.waterToLightRange.mapRange(water)
                        val temperature = addressMap.lightToTemperatureRange.mapRange(light)
                        val humidity = addressMap.temperatureToHumidityRange.mapRange(temperature)
                        val location = addressMap.humidityToLocationRange.mapRange(humidity)
                        minLocation = min(minLocation, location)
                        seed++
                    }
                    minLocation
                }
            }
            var minLocation = Long.MAX_VALUE
            locationResultAsync.forEach { deferred ->
                val location = deferred.await()
                minLocation = min(minLocation, location)
            }
            minLocation
        }
    }

    data class SeedInput(val seed: Long, val range: Long = 1)

    data class Range(val source: Long, val destination: Long, val range: Long) {
        fun destination(value: Long) = if (value in source until source + range) {
            val diff = value - source
            destination + diff
        } else {
            null
        }
    }

    fun List<Range>.mapRange(value: Long): Long {
        return firstNotNullOfOrNull { it.destination(value) } ?: value
    }

    data class AddressMap(
        val seedInput: List<SeedInput>,
        val seedToSoilRange: List<Range>,
        val soilToFertilizerRange: List<Range>,
        val fertilizerToWaterRange: List<Range>,
        val waterToLightRange: List<Range>,
        val lightToTemperatureRange: List<Range>,
        val temperatureToHumidityRange: List<Range>,
        val humidityToLocationRange: List<Range>,
    ) {
        companion object {
            fun createAddressMap(listInput: List<String>, version: Int = 1): AddressMap {
                val input = listInput.iterator()
                val resourceIterator = Resource.entries.iterator()

                val seedToSoilRange = mutableListOf<Range>()
                val soilToFertilizerRange = mutableListOf<Range>()
                val fertilizerToWaterRange = mutableListOf<Range>()
                val waterToLightRange = mutableListOf<Range>()
                val lightToTemperatureRange = mutableListOf<Range>()
                val temperatureToHumidityRange = mutableListOf<Range>()
                val humidityToLocationRange = mutableListOf<Range>()

                val firstLine = input.next()
                var line: String

                val inputList: List<Long> = firstLine.split(":")
                    .map { it.trim() }[1]
                    .split(" ")
                    .map { it.toLong() }

                val seedInput = if (version != 1) {
                    inputList.chunked(2).map { SeedInput(it[0], it[1]) }
                } else {
                    inputList.map { SeedInput(it) }
                }

                var currentResourcePointer: Resource = Resource.SOIL
                while (input.hasNext()) {
                    line = input.next()
                    if (line.isNotBlank()) {
                        if (line.last() == ':') {
                            continue
                        }
                        val rangeList = mutableListOf<Range>()
                        val (startDestination, startSource, range) = line.split(" ")
                            .map { it.trim() }
                            .map { it.toLong() }
                        rangeList.add(Range(startSource, startDestination, range))
                        when (currentResourcePointer) {
                            Resource.SOIL -> seedToSoilRange.addAll(rangeList)
                            Resource.FERTILIZER -> soilToFertilizerRange.addAll(rangeList)
                            Resource.WATER -> fertilizerToWaterRange.addAll(rangeList)
                            Resource.LIGHT -> waterToLightRange.addAll(rangeList)
                            Resource.TEMPERATURE -> lightToTemperatureRange.addAll(rangeList)
                            Resource.HUMIDITY -> temperatureToHumidityRange.addAll(rangeList)
                            Resource.LOCATION -> humidityToLocationRange.addAll(rangeList)
                        }
                    } else {
                        currentResourcePointer = resourceIterator.next()
                    }
                }

                return AddressMap(
                    seedInput = seedInput,
                    seedToSoilRange = seedToSoilRange,
                    soilToFertilizerRange = soilToFertilizerRange,
                    fertilizerToWaterRange = fertilizerToWaterRange,
                    waterToLightRange = waterToLightRange,
                    lightToTemperatureRange = lightToTemperatureRange,
                    temperatureToHumidityRange = temperatureToHumidityRange,
                    humidityToLocationRange = humidityToLocationRange
                )
            }
        }
    }

    enum class Resource {
        SOIL,
        FERTILIZER,
        WATER,
        LIGHT,
        TEMPERATURE,
        HUMIDITY,
        LOCATION
    }
}

private object Day05Alternative {
    fun part1(input: List<String>): Long {
        val seeds = parseSeedsForPart1(input)
        val mapperResources = parseMapperResources(input)
        // Finding the minimum location based on seed
        return seeds.minOf { seed ->
            mapperResources.fold(seed) { currentState, ranges ->
                ranges.firstOrNull { currentState in it }?.sourceToDestination(currentState) ?: currentState
            }
        }
    }

    fun part2(input: List<String>): Long {
        // location -> humidity -> temperature -> light -> water -> fertilizer -> soil -> seed
        val mapperReverse = parseMapperResources(input)
            .map { it.map { it.flip() } }.reversed()
        val possibleSeedSet = parseSeedsForPart2(input)
        return generateSequence(0L, Long::inc).first { location ->
            val targetSeed = mapperReverse.fold(location) { currentState, ranges ->
                ranges.firstOrNull { currentState in it }?.sourceToDestination(currentState) ?: currentState
            }
            possibleSeedSet.any { targetSeed in it }
        }
    }

    /**
     * Concept: Threat all mapper as layers, and each layer has a set of ranges.
     * The input will transform the output on every layer.
    */
    private fun parseMapperResources(input: List<String>): List<Set<Range>> =
        input.drop(2).joinToString("\n") // Join all lines after the first two
            .split("\n\n") // Split the line contain empty line
            .map {
                it.split("\n") // Split by line
                    .drop(1) // Drop label line
                    .map { line -> Range.of(line) }.toSet()
            }

    private fun parseSeedsForPart1(input: List<String>): List<Long> =
        // Split data after this delimiter
        input.first().removePrefix("seeds:")
            .trim().split(" ").map { it.toLong() }

    private fun parseSeedsForPart2(input: List<String>): Set<LongRange> =
        // Split data after this delimiter
        input.first().removePrefix("seeds:")
            .trim().split(" ").map { it.toLong() }
            // Prepare for combine range
            // 0 - start
            // 1 - start + range = max range
            .chunked(2)
            .map { it.first()..<(it.first() + it.last()) }
            .toSet()

    data class Range(val source: LongRange, val destination: LongRange) {
        fun flip(): Range = Range(destination, source)

        fun sourceToDestination(value: Long): Long = destination.first + (value - source.first)

        operator fun contains(num: Long): Boolean = num in source

        companion object {
            fun of(row: String): Range {
                val (dest, source, length) = row.split(" ").map { it.toLong() }
                return Range(
                    source..<(source + length),
                    dest..<(dest + length)
                )
            }
        }
    }
}
