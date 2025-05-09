package org.exmaple.day10.part1

import java.io.BufferedReader
import java.io.File


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().lines()

//    val input = """89010123
//78121874
//87430965
//96549874
//45678903
//32019012
//01329801
//10456732
//""".trim().lines()

    var result = 0L

    for (i in input.indices) {
        for (j in input[i].indices) {
            if (input[i][j] == '0') {
                result += trailheadScore(input, i, j)
            }
        }
    }

    println("result $result")

}

fun trailheadScore(input: List<String>, i: Int, j: Int): Int {
    val reachableNines = mutableSetOf<Pair<Int,Int>>()
    trailheadScore(input, i, j, 1, reachableNines)
    return reachableNines.count()
}

val directions = listOf(
    Pair(-1, 0), // Up
    Pair(0, 1),  // Right
    Pair(1, 0),  // Down
    Pair(0, -1)  // Left
)

fun trailheadScore(input: List<String>, x: Int, y: Int, nextLevel: Int, reachableNines: MutableSet<Pair<Int, Int>>) {
    if (input[x][y].digitToInt() == 9) {
        reachableNines.add(Pair(x, y))
        return
    }

    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if (newX in input.indices && newY in input[newX].indices && input[newX][newY].digitToInt() == nextLevel) {
            trailheadScore(input, newX, newY, nextLevel + 1, reachableNines)
        }
    }
}
