package org.example.day12.part2

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split('\n')

//    val input: List<String> = """OOOOO
//OXOXO
//OOOOO
//OXOXO
//OOOOO
//""".trim().split('\n')

//    val input: List<String> = """RRRRIICCFF
//RRRRIICCCF
//VVRRRCCFFF
//VVRCCCJFFF
//VVVVCJJCFE
//VVIVCCJJEE
//VVIIICJJEE
//MIIIIIJJEE
//MIIISIJEEE
//MMMISSJEEE""".trim().split('\n')

    val visited = mutableSetOf<Pair<Int, Int>>()
    var result = 0L
    input.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            if (Pair(i, j) !in visited) {
                val regionMetrics = dfs(i, j, input, visited)
                result += regionMetrics.cost()
                println("$i $j --> $regionMetrics --> ${regionMetrics.cost()}")
            }
        }
    }

    println(result)
}

val directions = listOf(
    Pair(-1, 0), // Up
    Pair(0, 1),  // Right
    Pair(1, 0),  // Down
    Pair(0, -1)  // Left
)


/**
 * Returns true if (x, y) is out-of-bounds or its cell does not match [ch].
 */
fun isDiffOrOutOfBounds(x: Int, y: Int, input: List<String>, ch: Char): Boolean {
    return if (x !in input.indices || y !in input[x].indices) true
    else input[x][y] != ch
}

fun dfs(x: Int, y: Int, input: List<String>, visited: MutableSet<Pair<Int, Int>>): RegionMetrics {
    visited.add(x to y)
    val ch = input[x][y]
    var regionMetrics = RegionMetrics(area = 1, perimeter = 0)

    // Orthogonal checks: if both neighbors for a given corner are either out-of-bounds or different,
    // the edge is part of the perimeter.
    if (isDiffOrOutOfBounds(x - 1, y, input, ch) && isDiffOrOutOfBounds(x, y - 1, input, ch)) regionMetrics.perimeter++  // Top-left
    if (isDiffOrOutOfBounds(x - 1, y, input, ch) && isDiffOrOutOfBounds(x, y + 1, input, ch)) regionMetrics.perimeter++  // Top-right
    if (isDiffOrOutOfBounds(x + 1, y, input, ch) && isDiffOrOutOfBounds(x, y + 1, input, ch)) regionMetrics.perimeter++  // Bottom-right
    if (isDiffOrOutOfBounds(x + 1, y, input, ch) && isDiffOrOutOfBounds(x, y - 1, input, ch)) regionMetrics.perimeter++  // Bottom-left

    // Diagonal checks: if both adjacent orthogonal neighbors are the same,
    // but the diagonal neighbor is different (or out-of-bounds), add to the perimeter.
    if (!isDiffOrOutOfBounds(x - 1, y, input, ch) &&
        !isDiffOrOutOfBounds(x, y - 1, input, ch) &&
        isDiffOrOutOfBounds(x - 1, y - 1, input, ch)) regionMetrics.perimeter++  // Top-left diagonal

    if (!isDiffOrOutOfBounds(x - 1, y, input, ch) &&
        !isDiffOrOutOfBounds(x, y + 1, input, ch) &&
        isDiffOrOutOfBounds(x - 1, y + 1, input, ch)) regionMetrics.perimeter++  // Top-right diagonal

    if (!isDiffOrOutOfBounds(x + 1, y, input, ch) &&
        !isDiffOrOutOfBounds(x, y + 1, input, ch) &&
        isDiffOrOutOfBounds(x + 1, y + 1, input, ch)) regionMetrics.perimeter++  // Bottom-right diagonal

    if (!isDiffOrOutOfBounds(x + 1, y, input, ch) &&
        !isDiffOrOutOfBounds(x, y - 1, input, ch) &&
        isDiffOrOutOfBounds(x + 1, y - 1, input, ch)) regionMetrics.perimeter++  // Bottom-left diagonal

    // Explore orthogonal neighbors using DFS.
    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if (newX in input.indices && newY in input[newX].indices && input[newX][newY] == ch) {
            if ((newX to newY) !in visited) {
                regionMetrics += dfs(newX, newY, input, visited)
            }
        }
    }
    return regionMetrics
}

data class RegionMetrics(var area: Int, var perimeter: Int) {
    operator fun plusAssign(rhs: RegionMetrics) {
        this.area += rhs.area
        this.perimeter += rhs.perimeter
    }}

fun RegionMetrics.cost() = this.area * this.perimeter