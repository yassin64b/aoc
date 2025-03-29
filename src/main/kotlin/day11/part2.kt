package org.exmaple.day11.part2

import java.io.BufferedReader
import java.io.File

val mem: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split(' ').map { it.toLong() }

//    val input: List<Long> = """125 17""".trim().split(' ').map { it.toLong() }
    val iterations = 75
    var result = 0L
    for (value in input) {
        result += dfs(value, iterations)
    }
    println("result $result")

}

fun dfs(value: Long, iterationsLeft: Int): Long {
    val key = value to iterationsLeft
    mem[key]?.let { return it }

    mem[key] = when {
        iterationsLeft == 0 -> 1L
        value == 0L -> dfs(1L, iterationsLeft - 1)
        numDigits(value) % 2 == 0 -> {
            val strValue = value.toString()
            dfs(strValue.take(strValue.length / 2).toLong(), iterationsLeft - 1) +
                    dfs(strValue.drop(strValue.length / 2).toLong(), iterationsLeft - 1)
        }
        else -> dfs(value * 2024L, iterationsLeft - 1)
    }

    return mem[key]!!
}

fun numDigits(value: Long): Int {
    var numDigits = 0
    var mutValue = value
    while (mutValue > 0) {
        numDigits++
        mutValue /= 10
    }
    return numDigits
}
