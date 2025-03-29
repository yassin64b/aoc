package org.example.day4

import java.io.File
import java.io.BufferedReader

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines()

    println(lines)

    val XMAS = "XMAS"

    var result = 0
    for (i in lines.indices) {
        val curLine = lines[i]
        for (j in curLine.indices) {

            if (j + 4 <= curLine.length) {

                if (curLine.substring(j, j+4) == XMAS) {
                    result++
                }

                if (curLine.substring(j, j+4).reversed() == XMAS) {
                    result++
                }
            }

            if (i + 4 <= lines.size) {
                val str = "" + lines[i][j] + lines[i+1][j] + lines[i+2][j] + lines[i+3][j]
                result += if (str == XMAS || str.reversed() == XMAS) 1 else 0
            }

            if (i + 4 <= lines.size && j + 4 <= lines[i + 3].length) {
                val str = "" + lines[i][j] + lines[i+1][j+1] + lines[i+2][j+2] + lines[i+3][j+3]
                result += if (str == XMAS || str.reversed() == XMAS) 1 else 0
            }

            if (i + 4 <= lines.size && j - 3 >= 0) {
                val str = "" + lines[i][j] + lines[i+1][j-1] + lines[i+2][j-2] + lines[i+3][j-3]
                result += if (str == XMAS || str.reversed() == XMAS) 1 else 0
            }
        }
    }

    println("found $result $XMAS occurrences")

}