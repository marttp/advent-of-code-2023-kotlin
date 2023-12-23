import helper.util.EIGHT_DIRECTIONS
import helper.util.println
import helper.util.readInput

fun main() {
    val input1 = readInput("input-3")

    val sample1 = readInput("sample1-3")
    check(Day03Util.part1(sample1) == 4361)
    Day03Util.part1(input1).println()
    check(Day03Util.part2(sample1) == 467835)
    Day03Util.part2(input1).println()
}

private object Day03Util {

    fun part1(input: List<String>): Int {
        val maxRow = input.size
        val maxCol = input[0].length
        val symbolPositions = collectSymbol(input)
        val numberPositions = collectNumber(input)
        val numberInRow = numberPositions.groupBy { it.row }
        return processingEngineSchematic(EngineSchematicInfo(maxRow, maxCol, numberInRow, symbolPositions))
    }

    fun part2(input: List<String>): Int {
        val maxRow = input.size
        val maxCol = input[0].length
        val symbolPositions = collectSymbol(input)
        val numberPositions = collectNumber(input)
        val numberInRow = numberPositions.groupBy { it.row }
        return processGearRatio(EngineSchematicInfo(maxRow, maxCol, numberInRow, symbolPositions))
    }

    private fun processingEngineSchematic(engineSchematicInfo: EngineSchematicInfo): Int {
        val usedSet = mutableSetOf<NumberPosition>()
        var sum = 0
        for (sp in engineSchematicInfo.symbolPosition) {
            for (dir in EIGHT_DIRECTIONS) {
                val nextPossibleRow = sp.row + dir.first
                val nextPossibleCol = sp.col + dir.second
                val isInBound = isInBound(
                    nextPossibleRow,
                    nextPossibleCol,
                    engineSchematicInfo.maxRow,
                    engineSchematicInfo.maxCol
                )
                if (isInBound && engineSchematicInfo.numberInRow.containsKey(nextPossibleRow)) {
                    val nextPossibleNumberPositions = engineSchematicInfo.numberInRow[nextPossibleRow]!!
                    for (npp in nextPossibleNumberPositions) {
                        if (usedSet.contains(npp).not() && nextPossibleCol in npp.startCol..npp.endCol) {
                            sum += npp.number
                            usedSet.add(npp)
                        }
                    }
                }
            }
        }
        return sum
    }

    private fun processGearRatio(engineSchematicInfo: EngineSchematicInfo): Int {
        val usedSet = mutableSetOf<NumberPosition>()
        val starSymbols = engineSchematicInfo.symbolPosition.filter { it.symbol == '*' }
        var sum = 0
        for (ss in starSymbols) {
            val preProcessorNumberPositions = mutableListOf<NumberPosition>()
            for (dir in EIGHT_DIRECTIONS) {
                val nextPossibleRow = ss.row + dir.first
                val nextPossibleCol = ss.col + dir.second
                val isInBound = isInBound(
                    nextPossibleRow,
                    nextPossibleCol,
                    engineSchematicInfo.maxRow,
                    engineSchematicInfo.maxCol
                )
                if (isInBound && engineSchematicInfo.numberInRow.containsKey(nextPossibleRow)) {
                    val nextPossibleNumberPositions = engineSchematicInfo.numberInRow[nextPossibleRow]!!
                    for (npp in nextPossibleNumberPositions) {
                        if (usedSet.contains(npp).not() && nextPossibleCol in npp.startCol..npp.endCol) {
                            usedSet.add(npp)
                            preProcessorNumberPositions.add(npp)
                        }
                    }
                }
            }
            val produceResult = if (preProcessorNumberPositions.size == 2) {
                preProcessorNumberPositions[0].number * preProcessorNumberPositions[1].number
            } else {
                0
            }
            sum += produceResult
        }
        return sum
    }

    fun isInBound(row: Int, col: Int, maxRow: Int, maxCol: Int) =
        row in 0 until maxRow && col in 0 until maxCol

    fun collectNumber(input: List<String>): List<NumberPosition> {
        val result = mutableListOf<NumberPosition>()
        for (row in input.indices) {
            val maxCol = input[row].length
            var col = 0
            while (col < maxCol) {
                val c = input[row][col]
                col += if (isNotDot(c) && c.isDigit()) {
                    val startCol = col
                    var endCol = col
                    while (endCol < maxCol && isNotDot(input[row][endCol]) && input[row][endCol].isDigit()) {
                        endCol++
                    }
                    val number = input[row].substring(startCol, endCol).toInt()
                    result.add(NumberPosition(number, row, startCol, endCol - 1))
                    endCol - startCol
                } else {
                    1
                }
            }
        }
        return result
    }

    fun collectSymbol(input: List<String>): List<SymbolPosition> {
        val result = mutableListOf<SymbolPosition>()
        for (row in input.indices) {
            for (col in input[row].indices) {
                val c = input[row][col]
                if (isNotDotAndNotDigit(c)) {
                    result.add(SymbolPosition(c, row, col))
                }
            }
        }
        return result
    }

    fun isNotDotAndNotDigit(c: Char) = isNotDot(c) && isNotDigit(c)
    fun isDot(c: Char) = c == '.'
    fun isNotDot(c: Char) = isDot(c).not()
    fun isNotDigit(c: Char) = !c.isDigit()

    data class EngineSchematicInfo(
        val maxRow: Int,
        val maxCol: Int,
        val numberInRow: Map<Int, List<NumberPosition>>,
        val symbolPosition: List<SymbolPosition>
    )

    data class NumberPosition(val number: Int, val row: Int, val startCol: Int, val endCol: Int)

    data class SymbolPosition(val symbol: Char, val row: Int, val col: Int)
}
