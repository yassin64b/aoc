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

    val parts = input.split("\n\n")
    assert(parts.size == 2)

    val availableTowels = parts[0].split(", ")
    println("availableTowels=$availableTowels")

    val requestedPatterns = parts[1].split("\n")
    println("requestedPatterns=$requestedPatterns")

    var possiblePatternCounter = 0
    for (pattern in requestedPatterns) {
        val reachable = MutableList(pattern.length + 1) { false }
        reachable[0] = true

        for (idx in pattern.indices) {
            if (reachable[idx]) {
                for (towel in availableTowels) {
                    if (idx + towel.length <= pattern.length && towel == pattern.substring(idx, idx + towel.length)) {
                        reachable[idx + towel.length] = true
                    }
                }
            }
        }
        if (reachable[pattern.length]) {
            ++possiblePatternCounter
            println("able to construct $pattern")
        }
    }
    println("possible num = $possiblePatternCounter")
}
