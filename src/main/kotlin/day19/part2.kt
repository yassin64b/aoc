package org.example.day19.part1

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """r, wr, b, g, bwu, rb, gb, br
//
//brwrr
//bggr
//gbbr
//rrbgbr
//ubwu
//bwurrg
//brgr
//bbrgwb""".trim()

    val (availableTowels, requestedPatterns) = parseInput(input)

    var totalCombinations = 0L
    for (pattern in requestedPatterns) {
        val combinations = countCombinations(pattern, availableTowels)
        println("$pattern can be constructed in $combinations ways")
        totalCombinations += combinations
    }
    println("possible num = $totalCombinations")
}

private fun countCombinations(
    pattern: String,
    availableTowels: List<String>
): Long {
    val reachablePossibilities = LongArray(pattern.length + 1) { if (it == 0) 1L else 0L }

    for (idx in pattern.indices) {
        if (reachablePossibilities[idx] > 0) {
            for (towel in availableTowels) {
                val endIdx = idx + towel.length
                if (endIdx <= pattern.length && towel == pattern.substring(idx, endIdx)) {
                    reachablePossibilities[endIdx] += reachablePossibilities[idx]
                }
            }
        }
    }
    return reachablePossibilities[pattern.length]
}

fun parseInput(input: String): Pair<List<String>, List<String>> {
    val parts = input.split("\n\n")
    require(parts.size == 2) { "Input should contain two parts separated by an empty line" }

    val availableTowels = parts[0].split(", ")
    val requestedPatterns = parts[1].split("\n")

    return Pair(availableTowels, requestedPatterns)
}