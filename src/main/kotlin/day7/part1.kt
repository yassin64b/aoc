package org.exmaple.day7.part1

import java.io.BufferedReader
import java.io.File


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
    for (line in lines) {
        val equation = line.split(':')
        require(equation.size == 2) {"Size must be 2, but is ${equation.size}"}

        val desiredResult = equation[0].toLong()
        val values = equation[1].trim().split(' ').map { it.toLong() }
        println("Values is $values")

        require(values.isNotEmpty()) {"Size must be at least 1, but is ${values.size}"}
        var possibleValues = setOf(values[0])
        for (i in 1..<values.size) {
            val newPossibleValues: MutableSet<Long> = mutableSetOf()
            for (value in possibleValues) {
                newPossibleValues.add(value * values[i])
                newPossibleValues.add(value + values[i])
            }
            possibleValues = newPossibleValues
        }
        println("Possible values $possibleValues $desiredResult")

        if (possibleValues.contains(desiredResult)) {
            sum += desiredResult
        }
    }
    println("final sum $sum")
}