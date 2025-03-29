package org.exmaple.day7.part2

import java.io.BufferedReader
import java.io.File
import kotlin.time.measureTime


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines()

//    val lines = """190: 10 19
//3267: 81 40 27
//83: 17 5
//156: 15 6
//7290: 6 8 6 15
//161011: 16 10 13
//192: 17 8 14
//21037: 9 7 18 13
//292: 11 6 16 20""".lines()
    var sum = 0L

    val duration = measureTime {

        for (line in lines) {
            val equation = line.split(':')
            require(equation.size == 2) { "Size must be 2, but is ${equation.size}" }

            val desiredResult = equation[0].toLong()
            val values = equation[1].trim().split(' ').map { it.toLong() }

            require(values.isNotEmpty()) { "Size must be at least 1, but is ${values.size}" }
            var possibleValues = setOf(values[0])
            for (i in 1..<values.size) {
                val newPossibleValues: MutableSet<Long> = mutableSetOf()
                for (value in possibleValues) {
                    newPossibleValues.add(value * values[i])
                    newPossibleValues.add(value + values[i])
                    newPossibleValues.add(appendNumsFaster(value, values[i]))
                }
                possibleValues = newPossibleValues
            }

            if (possibleValues.contains(desiredResult)) {
                sum += desiredResult
            }
        }
    }

    println("final sum $sum, execution took $duration")
}

fun appendNums(left: Long, right: Long): Long =
    (left.toString() + right.toString()).toLong()

fun appendNumsFaster(left: Long, right: Long): Long {
    var result = left
    var tmpRight = right
    while (tmpRight > 0) {
        result *= 10
        tmpRight /= 10
    }
    return result + right
}
