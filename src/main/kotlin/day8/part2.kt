package org.exmaple.day8.part2

import java.io.BufferedReader
import java.io.File


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines()

//    val lines = """............
//........0...
//.....0......
//.......0....
//....0.......
//......A.....
//............
//............
//........A...
//.........A..
//............
//............""".lines()

    val antennaToLocation: MutableMap<Char, MutableList<Pair<Int, Int>>> = mutableMapOf()
    for (i in lines.indices) {
        for (j in lines[i].indices) {
            if (lines[i][j] != '.') {
                val antenna = lines[i][j]
                antennaToLocation.getOrPut(antenna) { mutableListOf() }.add(Pair(i, j))
            }
        }
    }
    println("resulting antenna set $antennaToLocation")

    var resultSet = mutableSetOf<Pair<Int, Int>>()
    antennaToLocation.values.forEach { list ->
        for (i in list.indices) {
            for (j in i+1 until list.size) {
                val dist = getDist(list[i], list[j])
                (-50..50).forEach { multiplier ->
                    val pos = Pair(list[i].first - multiplier * dist.first, list[i].second - multiplier * dist.second)
                    if (pos.first in lines.indices && pos.second in lines[0].indices)
                        resultSet.add(pos)
                }
            }
        }
    }
    println("resultSet $resultSet")
    println("finally ${resultSet.size}")
}

fun getDist(a: Pair<Int, Int>, b: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(b.first - a.first, b.second - a.second)
}
