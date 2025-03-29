package org.example.day16.part2

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
    var eX = -1
    var eY = -1
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
                eX = x
                eY = y
                return@rowLoop
            }
        }
    }

    val queue = ArrayDeque<ReverseCost>()
    if (mapOfMinCost[eX][eY].upCost == finalResult)
        queue.addLast(ReverseCost(eX, eY, finalResult, dirX = -1, dirY = 0))

    if (mapOfMinCost[eX][eY].downCost == finalResult)
        queue.addLast(ReverseCost(eX, eY, finalResult, dirX = 1, dirY = 0))

    if (mapOfMinCost[eX][eY].leftCost == finalResult)
        queue.addLast(ReverseCost(eX, eY, finalResult, dirX = 0, dirY = -1))

    if (mapOfMinCost[eX][eY].rightCost == finalResult)
        queue.addLast(ReverseCost(eX, eY, finalResult, dirX = 0, dirY = 1))

    val setOfBestPathCoords : MutableSet<Pair<Int, Int>> = mutableSetOf()
    while (queue.isNotEmpty()) {
        val front = queue.removeFirst()
        setOfBestPathCoords.add(Pair(front.x, front.y))

        if (mapOfChars[front.x][front.y] == 'S') continue
        mapOfChars[front.x][front.y] = 'O' // just for visualization / debugging

        if (front.dirX == -1) {
            if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost == front.cost - MOVE_COST) {
                queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST, front.dirX, front.dirY))

                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 0, -1))
                }

                // Check if we can reach this position from moving up and then rotating right
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 0, 1))
                }

                // Check if we can reach this position from moving up and then rotating down
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost + 2 * ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - 2 * ROTATE_COST, 1, 0))
                }
            }
        } else if (front.dirX == 1) {
            if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost == front.cost - MOVE_COST) {
                queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST, front.dirX, front.dirY))

                // Check if we can reach this position from moving down and then rotating left
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 0, -1))
                }

                // Check if we can reach this position from moving down and then rotating right
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 0, 1))
                }

                // Check if we can reach this position from moving down and then rotating up
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost + 2 * ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - 2 * ROTATE_COST, -1, 0))
                }
            }
        } else if (front.dirY == -1) {
            if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost == front.cost - MOVE_COST) {
                queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST, front.dirX, front.dirY))

                // Check if we can reach this position from moving left and then rotating up
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, -1, 0))
                }

                // Check if we can reach this position from moving left and then rotating down
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 1, 0))
                }

                // Check if we can reach this position from moving left and then rotating right
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost + 2 * ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - 2 * ROTATE_COST, 0, 1))
                }
            }
        } else if (front.dirY == 1) {
            if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost == front.cost - MOVE_COST) {
                queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST, front.dirX, front.dirY))

                // Check if we can reach this position from moving right and then rotating up
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].upCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, -1, 0))
                }

                // Check if we can reach this position from moving right and then rotating down
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].downCost + ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - ROTATE_COST, 1, 0))
                }

                // Check if we can reach this position from moving right and then rotating left
                if (mapOfMinCost[front.x - front.dirX][front.y - front.dirY].rightCost == mapOfMinCost[front.x - front.dirX][front.y - front.dirY].leftCost + 2 * ROTATE_COST) {
                    queue.addLast(ReverseCost(front.x - front.dirX, front.y - front.dirY, front.cost - MOVE_COST - 2 * ROTATE_COST, 0, -1))
                }
            }
        }
    }

    println("final result size: ${setOfBestPathCoords.size} from ${setOfBestPathCoords}")

    println("Map:")
    mapOfChars.forEach { row ->
        println(row.joinToString(""))
    }
}

data class ReverseCost(var x: Int, var y: Int, var cost: Long, var dirX: Int, var dirY: Int) {

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
