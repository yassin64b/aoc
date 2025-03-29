package org.example.day16.part22

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
        mapOfChars.forEachIndexed { x, row ->
            row.forEachIndexed { y, cell ->
                // Check all four directions using the Direction enum
                for (dir in Direction.entries) {
                    if (isValidMove(mapOfChars, x, y, dir)) {
                        val (newX, newY) = getNextPosition(x, y, dir)
                        val currentCost = dir.getCost(mapOfMinCost[x][y])
                        val newCost = min(dir.getCost(mapOfMinCost[newX][newY]), currentCost + MOVE_COST)
                        dir.setCost(mapOfMinCost[newX][newY], newCost)
                        mapOfMinCost[newX][newY].updateAllCosts()
                    }
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
    // Initialize the queue with the ending position
    Direction.entries.forEach { dir ->
        if (dir.getCost(mapOfMinCost[eX][eY]) == finalResult) {
            queue.addLast(ReverseCost(eX, eY, finalResult, dir))
        }
    }

    val setOfBestPathCoords : MutableSet<Pair<Int, Int>> = mutableSetOf()
    while (queue.isNotEmpty()) {
        val front = queue.removeFirst()
        setOfBestPathCoords.add(Pair(front.x, front.y))

        if (mapOfChars[front.x][front.y] == 'S') continue
        mapOfChars[front.x][front.y] = 'O' // just for visualization / debugging

        // Calculate previous position (where we came from)
        val prevDir = front.direction.opposite()
        val prevX = front.x + prevDir.dx
        val prevY = front.y + prevDir.dy

        if (front.direction.getCost(mapOfMinCost[prevX][prevY]) == front.cost - MOVE_COST) {
            // Continue in the same direction
            queue.addLast(ReverseCost(prevX, prevY, front.cost - MOVE_COST, front.direction))

            // Check all possible rotations to this position
            for (sourceDir in Direction.entries) {
                if (sourceDir == front.direction) continue

                val rotationCost = sourceDir.rotationCost(front.direction)
                if (front.direction.getCost(mapOfMinCost[prevX][prevY]) ==
                    sourceDir.getCost(mapOfMinCost[prevX][prevY]) + rotationCost) {
                    queue.addLast(ReverseCost(
                        prevX, prevY,
                        front.cost - MOVE_COST - rotationCost,
                        sourceDir
                    ))
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

data class ReverseCost(val x: Int, val y: Int, val cost: Long, val direction: Direction) {
    constructor(x: Int, y: Int, cost: Long, dirX: Int, dirY: Int) :
            this(x, y, cost, Direction.fromDeltas(dirX, dirY) ?: throw IllegalArgumentException("Invalid direction: $dirX, $dirY"))
}

class CostTuple(var upCost: Long, var downCost: Long, var rightCost: Long, var leftCost: Long) {
    fun updateAllCosts() {
        // More concise way to update all costs by considering all possible rotations
        for (targetDir in Direction.entries) {
            for (sourceDir in Direction.entries) {
                if (targetDir == sourceDir) continue

                val rotationCost = sourceDir.rotationCost(targetDir)
                val newCost = sourceDir.getCost(this) + rotationCost
                if (newCost < targetDir.getCost(this)) {
                    targetDir.setCost(this, newCost)
                }
            }
        }
    }
}


enum class Direction(val dx: Int, val dy: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    fun opposite(): Direction = when(this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun getCost(tuple: CostTuple): Long = when(this) {
        UP -> tuple.upCost
        DOWN -> tuple.downCost
        LEFT -> tuple.leftCost
        RIGHT -> tuple.rightCost
    }

    fun setCost(tuple: CostTuple, value: Long) {
        when(this) {
            UP -> tuple.upCost = value
            DOWN -> tuple.downCost = value
            LEFT -> tuple.leftCost = value
            RIGHT -> tuple.rightCost = value
        }
    }

    fun rotationCost(target: Direction): Long = when {
        this == target -> 0
        this == target.opposite() -> 2 * ROTATE_COST
        else -> ROTATE_COST
    }

    companion object {
        fun fromDeltas(dx: Int, dy: Int): Direction? {
            return values().find { it.dx == dx && it.dy == dy }
        }
    }
}

// Check if a move in a direction is valid
fun isValidMove(grid: List<List<Char>>, x: Int, y: Int, dir: Direction): Boolean {
    val newX = x + dir.dx
    val newY = y + dir.dy
    return newX in grid.indices &&
            newY in grid[newX].indices &&
            grid[newX][newY] != WALL
}

// Get the next position based on a direction
fun getNextPosition(x: Int, y: Int, dir: Direction): Pair<Int, Int> {
    return Pair(x + dir.dx, y + dir.dy)
}