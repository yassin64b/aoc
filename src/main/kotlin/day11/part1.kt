package org.exmaple.day11.part1

import java.io.BufferedReader
import java.io.File


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split(' ').map { it.toLong() }

//    val input: List<Long> = """125 17""".trim().split(' ').map { it.toLong() }
    var curList = input

    val iterations = 25
    repeat (iterations) {
        val nextList = mutableListOf<Long>()
        for (value in curList) {
            if (value == 0L) {
                nextList.add(1L)
            } else if (numDigits(value) % 2 == 0) {
                val strValue = value.toString()
                nextList.add(strValue.substring(0, strValue.length/2).toLong())
                nextList.add(strValue.substring(strValue.length/2, strValue.length).toLong())
            } else {
                nextList.add(value * 2024L)
            }
        }
        curList = nextList
    }

    println("result ${curList.size}")

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
