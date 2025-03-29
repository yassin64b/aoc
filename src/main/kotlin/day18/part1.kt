package org.example.day18.part1

import java.io.BufferedReader
import java.io.File

const val DIMENSIONS = 71
const val BYTE_LIMIT = 1024

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split("\n")

//    val input = """5,4
//4,2
//4,5
//3,0
//2,1
//6,3
//2,4
//1,5
//0,6
//3,3
//2,6
//5,1
//1,2
//5,5
//2,5
//6,5
//1,4
//0,4
//6,4
//1,1
//6,1
//1,0
//0,5
//1,6
//2,0""".trim().split("\n")

    val map = List(DIMENSIONS) {
        MutableList(DIMENSIONS) {
            1
        }
    }
    input.forEachIndexed { index, line ->
        val (x, y) = line.split(",").map { it.toInt() }
        println("$x, $y")
        if (index < BYTE_LIMIT) {
            map[y][x] = 0
        }
    }

    map.forEach { line ->
        println(line.joinToString(""))
    }


    val minPath = bfs(0, 0, map)
    println("minPath=$minPath")
}

fun bfs(x: Int, y: Int, map: List<List<Int>>): Int {
    val visited = List(DIMENSIONS) {
        MutableList(DIMENSIONS) {
            false
        }
    }
    visited[x][y] = true
    val queue = ArrayDeque<Triple<Int, Int, Int>>()
    queue.addLast(Triple(x, y, 0))

    while (queue.isNotEmpty()) {
        val (cx, cy, dist) = queue.removeFirst()
        if (cx == DIMENSIONS - 1 && cy == DIMENSIONS - 1) {
            return dist
        }
        for ((dx, dy) in listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))) {
            val nx = cx + dx
            val ny = cy + dy
            if (nx in 0..<DIMENSIONS && ny in 0..<DIMENSIONS && map[nx][ny] == 1 && !visited[nx][ny]) {
                queue.addLast(Triple(nx, ny, dist + 1))
                visited[nx][ny] = true
            }
        }
    }

    return -1
}
