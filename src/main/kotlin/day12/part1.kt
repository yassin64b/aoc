package org.example.day12.part1

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split('\n')

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

fun dfs(x: Int, y: Int, input: List<String>, visited: MutableSet<Pair<Int, Int>>): RegionMetrics {
    visited.add(Pair(x, y))

    var regionMetrics = RegionMetrics(1, 0)
    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if (newX !in input.indices || newY !in input[newX].indices) {
            regionMetrics.perimeter++
        } else if (input[newX][newY] != input[x][y]) {
            regionMetrics.perimeter++
        } else if (!visited.contains(Pair(newX, newY))) {
            val childRegionMetrics = dfs(newX, newY, input, visited)
            regionMetrics += childRegionMetrics
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