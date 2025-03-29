package org.example.day16.part1

import java.io.BufferedReader
import java.io.File
import kotlin.math.min

const val ROTATE_COST = 1000L
const val MOVE_COST = 1L
const val WALL = '#'
const val MAX_VAL = Int.MAX_VALUE.toLong()

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """###############
//#.......#....E#
//#.#.###.#.###.#
//#.....#.#...#.#
//#.###.#####.#.#
//#.#.#.......#.#
//#.#.#####.###.#
//#...........#.#
//###.#.#####.#.#
//#...#.....#.#.#
//#.#.#.###.#.#.#
//#.....#...#.#.#
//#.###.#.#.#.#.#
//#S..#.....#...#
//###############""".trim()

//    val input = """#################
//#...#...#...#..E#
//#.#.#.#.#.#.#.#.#
//#.#.#.#...#...#.#
//#.#.#.#.###.#.#.#
//#...#.#.#.....#.#
//#.#.#.#.#.#####.#
//#.#...#.#.#.....#
//#.#.#####.#.###.#
//#.#.#.......#...#
//#.#.###.#####.###
//#.#.#...#.....#.#
//#.#.#.#####.###.#
//#.#.#.........#.#
//#.#.#.#########.#
//#S#.............#
//#################""".trim()

    // Parse the map part (first part) into List<List<Char>>
    val mapOfChars: List<MutableList<Char>> = input.split("\n").map { line ->
        line.toMutableList()
    }

    val mapOfMinCost: List<MutableList<CostTuple>> = List(mapOfChars.size) {
        MutableList(mapOfChars[0].size) {
            CostTuple(MAX_VAL, MAX_VAL, MAX_VAL, MAX_VAL)
        }
    }

    mapOfChars.forEachIndexed rowLoop@ { x, row ->
        row.forEachIndexed { y, cell ->
            if (cell == 'S') {
                mapOfMinCost[x][y] = CostTuple(ROTATE_COST, ROTATE_COST, 0L, ROTATE_COST * 2)
                return@rowLoop
            }
        }
    }

    repeat(10000) {
        mapOfChars.forEachIndexed rowLoop@ { x, row ->
            row.forEachIndexed { y, cell ->
                // check up
                if (x - 1 >= 0 && mapOfChars[x - 1][y] != WALL) {
                    mapOfMinCost[x - 1][y].upCost = min(mapOfMinCost[x - 1][y].upCost, mapOfMinCost[x][y].upCost + MOVE_COST)
                    mapOfMinCost[x - 1][y].updateAllCosts()
                }

                // check down
                if (x + 1 < mapOfChars.size && mapOfChars[x + 1][y] != WALL) {
                    mapOfMinCost[x + 1][y].downCost = min(mapOfMinCost[x + 1][y].downCost, mapOfMinCost[x][y].downCost + MOVE_COST)
                    mapOfMinCost[x + 1][y].updateAllCosts()
                }

                // check left
                if (y - 1 >= 0 && mapOfChars[x][y - 1] != WALL) {
                    mapOfMinCost[x][y - 1].leftCost = min(mapOfMinCost[x][y - 1].leftCost, mapOfMinCost[x][y].leftCost + MOVE_COST)
                    mapOfMinCost[x][y - 1].updateAllCosts()
                }

                // check right
                if (y + 1 < mapOfChars[x].size && mapOfChars[x][y + 1] != WALL) {
                    mapOfMinCost[x][y + 1].rightCost = min(mapOfMinCost[x][y + 1].rightCost, mapOfMinCost[x][y].rightCost + MOVE_COST)
                    mapOfMinCost[x][y + 1].updateAllCosts()
                }
            }
        }
    }

    var finalResult: Long = -1
    mapOfChars.forEachIndexed rowLoop@ { x, row ->
        row.forEachIndexed { y, cell ->
            if (cell == 'E') {
                finalResult = min(
                    min(
                        min(mapOfMinCost[x][y].upCost, mapOfMinCost[x][y].downCost),
                        mapOfMinCost[x][y].leftCost
                    ),
                    mapOfMinCost[x][y].rightCost
                )
                return@rowLoop
            }
        }
    }

    println("finally $finalResult")


}

class CostTuple(var upCost: Long, var downCost: Long, var rightCost: Long, var leftCost: Long) {

    fun updateAllCosts() {
        upCost = min(upCost, rightCost + ROTATE_COST)
        upCost = min(upCost, leftCost + ROTATE_COST)
        upCost = min(upCost, downCost + 2 * ROTATE_COST)

        downCost = min(downCost, rightCost + ROTATE_COST)
        downCost = min(downCost, leftCost + ROTATE_COST)
        downCost = min(downCost, upCost + 2 * ROTATE_COST)

        rightCost = min(rightCost, upCost + ROTATE_COST)
        rightCost = min(rightCost, downCost + ROTATE_COST)
        rightCost = min(rightCost, leftCost + 2 * ROTATE_COST)

        leftCost = min(leftCost, upCost + ROTATE_COST)
        leftCost = min(leftCost, downCost + ROTATE_COST)
        leftCost = min(leftCost, rightCost + 2 * ROTATE_COST)
    }
}
