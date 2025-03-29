package org.exmaple.day6.part1

import java.io.BufferedReader
import java.io.File


private const val VISITED = 'X'

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines().map { it.toCharArray() }

    var curPos: Position = findStartPos(lines)
    println("starting pos $curPos")

    while (true) {
        lines[curPos.x][curPos.y] = VISITED

        var nextPos = curPos.move()
        var turnCount = 1
        while (insideBounds(nextPos, lines) && lines[nextPos.x][nextPos.y] == '#') {
            nextPos = curPos.turnRight(turnCount).move()
            turnCount++
        }

        curPos = nextPos
        if (insideBounds(curPos, lines)) {
            require(lines[curPos.x][curPos.y] == '.' || lines[curPos.x][curPos.y] == VISITED) { "invalid previous pos"}
        } else {
            break
        }
    }

    println("final pos $curPos")
    println("final map")
    printGrid(lines)

    val visitedCount = countVisited(lines)
    println("visited $visitedCount")
}

fun countVisited(map: List<CharArray>): Int {
    return map.sumOf { row -> row.count { it == VISITED } }
}

fun insideBounds(nextPos: Position, lines: List<CharArray>): Boolean {
    return nextPos.x >= 0 && nextPos.x < lines.size
            && nextPos.y >= 0 && nextPos.y < lines[nextPos.x].size
}

fun findStartPos(map: List<CharArray>): Position {
    for (i in map.indices) {
        for (j in map[i].indices) {
            when (map[i][j]) {
                '>' -> return Position(i, j, 0, 1)
                '<' -> return Position(i, j, 0, -1)
                '^' -> return Position(i, j, -1, 0)
                'v' -> return Position(i, j, 1, 0)
                else -> {}
            }
        }
    }
    throw IllegalArgumentException("no start found")
}

data class Position(val x: Int, val y: Int, val dirX: Int, val dirY: Int) {

    fun move(): Position {
        return Position(x + dirX, y + dirY, dirX, dirY)
    }

    fun turnRight(numTimes: Int): Position {
        if (numTimes <= 0) {
            return this
        }
        if (dirX == 0 && dirY == 1) {
            return Position(x, y, 1, 0).turnRight(numTimes - 1)
        } else if (dirX == 0 && dirY == -1) {
            return Position(x, y, -1, 0).turnRight(numTimes - 1)
        } else if (dirX == -1 && dirY == 0) {
            return Position(x, y, 0, 1).turnRight(numTimes - 1)
        } else if (dirX == 1 && dirY == 0) {
            return Position(x, y, 0, -1).turnRight(numTimes - 1)
        }
        throw IllegalArgumentException("directions are invalid, dirX $dirX, dirY $dirY")
    }
}

fun printGrid(grid: List<CharArray>) {
    grid.forEach { row ->
        println(row.joinToString(""))
    }
}