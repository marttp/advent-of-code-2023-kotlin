import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun main() {
    val input1 = readInput("input-5")

    val sample1 = readInput("sample1-5")
    check(Day05Util.part1(sample1) == 35L)
    val result1 = Day05Util.part1(input1)
    result1.println()
    check(result1 == 107430936L)

    check(Day05Util.part2(sample1) == 46L)
    val result2 = Day05Util.part2(input1)
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
