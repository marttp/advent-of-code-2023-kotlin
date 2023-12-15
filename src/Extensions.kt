// Point2D support for Array<CharArray>

fun Array<CharArray>.show() {
    this.forEach { println(it) }
}

fun Array<CharArray>.isInBounds(point: Point2D) =
    point.row in indices &&
            point.column in 0 until this[0].size

fun Array<CharArray>.swap(a: Point2D, b: Point2D) {
    val tmp = this[a]
    this[a] = this[b]
    this[b] = tmp
}

operator fun Array<CharArray>.set(at: Point2D, c: Char) {
    this[at.row][at.column] = c
}

operator fun Array<CharArray>.get(at: Point2D): Char = this[at.row][at.column]