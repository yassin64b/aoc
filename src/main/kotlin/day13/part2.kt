package org.example.day13.part1

import java.io.BufferedReader
import java.io.File
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

const val ADD = 10000000000000L


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split('\n')

//    val input: List<String> = """Button A: X+1, Y+1
//Button B: X+1, Y+1
//Prize: X=10, Y=10
//
//Button A: X+26, Y+66
//Button B: X+67, Y+21
//Prize: X=12748, Y=12176
//
//Button A: X+17, Y+86
//Button B: X+84, Y+37
//Prize: X=7870, Y=6450
//
//Button A: X+69, Y+23
//Button B: X+27, Y+71
//Prize: X=18641, Y=10279""".trim().split('\n')

    val regexForButtons = """X\+(\d+), Y\+(\d+)""".toRegex()
    val regexForPrice = """X=(\d+), Y=(\d+)""".toRegex()

    var total = 0L

    for (i in input.indices step 4) {
        assert(i + 3 < input.size)

        val buttonA = input[i]
        val buttonB = input[i + 1]
        val prizeLoc = input[i + 2]

        println("\n\nprocessing\n$buttonA\n$buttonB\npriceLoc")

        val matchResultA = regexForButtons.find(buttonA)
        assert(matchResultA != null)
        val (axValue, ayValue) = matchResultA!!.destructured
        val ax = axValue.toLong()
        val ay = ayValue.toLong()

        val matchResultB = regexForButtons.find(buttonB)
        assert(matchResultB != null)
        val (bxValue, byValue) = matchResultB!!.destructured
        val bx = bxValue.toLong()
        val by = byValue.toLong()

        val matchResultPrice = regexForPrice.find(prizeLoc)
        assert(matchResultPrice != null)
        val (pxValue, pyValue) = matchResultPrice!!.destructured
        val px = pxValue.toLong() + ADD
        val py = pyValue.toLong() + ADD

        val det = ax * by - ay * bx
        if (det == 0L) {
            // This code was not needed, the input seems to be missing a case
            // where both buttons are linearly dependent. However on a manually constructed test,
            // this was needed and worked.
            // Handle linear dependency with cost optimization
            var foundSolution = false

            // Check if buttons are proportional
            if (ax * by == ay * bx && ax != 0L && bx != 0L) {
                // Ratio between button movements
                val ratio = bx.toDouble() / ax.toDouble()

                // For cost optimization, prefer button B if ratio > 3
                if (ratio > 0.3 && px % bx == 0L) {
                    val b = px / bx
                    if (b >= 0 && b * by == py) {
                        println("use only b1 $b")
                        total += b
                        foundSolution = true
                    }
                } else if (px % ax == 0L) {
                    val a = px / ax
                    if (a >= 0 && a * ay == py) {
                        println("use only a1 $a")
                        total += 3 * a
                        foundSolution = true
                    }
                }
            }

            if (!foundSolution) {
                // Try individual buttons if proportional check failed
                if (bx != 0L && px % bx == 0L) {
                    val b = px / bx
                    if (b >= 0 && b * by == py) {
                        println("use only b2 $b")
                        total += b
                        continue
                    }
                }
                if (ax != 0L && px % ax == 0L) {
                    val a = px / ax
                    if (a >= 0 && a * ay == py) {
                        println("use only a2 $a")
                        total += 3 * a
                        continue
                    }
                }
            }
        } else {
            // Original solution for non-degenerate case
            val b = (ax * py - ay * px) / det
            val a = (px - b * bx) / ax
            if (a >= 0 && b >= 0 && a * ax + b * bx == px && a * ay + b * by == py) {
                total += 3 * a + b
            }
        }
    }

    println(total)

}