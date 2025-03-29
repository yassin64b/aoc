package org.exmaple.day6.part2

import java.io.BufferedReader
import java.io.File

private const val LIMIT = 130 * 130

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines().map { it.toCharArray() }

    var result = 0
    for (i in lines.indices) {
        for (j in lines[i].indices) {
            if (lines[i][j] == '.') {
                lines[i][j] = '#'
                result += if (isLoop(deepCopy(lines))) 1 else 0
                lines[i][j] = '.'
            }
        }
    }

    println("Found $result ways")
}

fun deepCopy(grid: List<CharArray>): List<CharArray> {
    return grid.map { it.copyOf() }
}

fun isLoop(lines: List<CharArray>): Boolean {
    var curPos: Position = findStartPos(lines)

    var numMoves = 0
    while (numMoves < LIMIT) {
        numMoves++

        var nextPos = curPos.move()
        var turnCount = 1
        while (insideBounds(nextPos, lines) && lines[nextPos.x][nextPos.y] == '#') {
            nextPos = curPos.turnRight(turnCount).move()
            turnCount++
        }

        curPos = nextPos
        if (!insideBounds(curPos, lines)) {
            break
        }
    }

    return numMoves >= LIMIT
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