package org.exmaple.day9.part1

import java.io.BufferedReader
import java.io.File


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = "2333133121414131402"

    val fileSystem = mutableListOf<Int>()

    var nextId = 0
    for (i in input.indices) {
        val times = input[i].digitToInt()
        if (i % 2 == 0) {
            repeat(times) {
                fileSystem.add(nextId)
            }
            nextId++
        } else {
            repeat(times) {
                fileSystem.add(-1)
            }
        }
    }

    println("starting filesystem $fileSystem")

    var result = 0L
    var endIndex = fileSystem.size - 1
    for (i in fileSystem.indices) {
        if (endIndex >= i) {
            if (fileSystem[i] == -1) {
                fileSystem[i] = fileSystem[endIndex]
                fileSystem[endIndex] = -1
                do {
                    endIndex--
                } while (endIndex > i && fileSystem[endIndex] == -1)
            }
            result += i * fileSystem[i]
        } else {
            break
        }
    }

    println("compressed filesystem $fileSystem")
    println("result $result")

}
