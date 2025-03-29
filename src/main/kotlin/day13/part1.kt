package org.example.day13.part1

import java.io.BufferedReader
import java.io.File
import kotlin.math.min

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim().split('\n')

//    val input: List<String> = """Button A: X+94, Y+34
//Button B: X+22, Y+67
//Prize: X=8400, Y=5400
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

        val matchResultA = regexForButtons.find(buttonA)
        assert(matchResultA != null)
        val (axValue, ayValue) = matchResultA!!.destructured
        val ax = axValue.toInt()
        val ay = ayValue.toInt()

        val matchResultB = regexForButtons.find(buttonB)
        assert(matchResultB != null)
        val (bxValue, byValue) = matchResultB!!.destructured
        val bx = bxValue.toInt()
        val by = byValue.toInt()

        val matchResultPrice = regexForPrice.find(prizeLoc)
        assert(matchResultPrice != null)
        val (pxValue, pyValue) = matchResultPrice!!.destructured
        val px = pxValue.toInt()
        val py = pyValue.toInt()

        var cheapest = -1
        for (a in 0..100) {
            val cx = a * ax
            val cy = a * ay
            val missingX = px - cx
            val countNeededB = missingX / bx
            if (countNeededB in 0..100) {
                val actualX = cx + countNeededB * bx
                val actualY = cy + countNeededB * by
                if (actualY == py && actualX == px) {
                    val cost = a * 3 + countNeededB
                    cheapest = if (cheapest == -1) {
                        cost
                    } else {
                        min(cheapest, cost)
                    }
                }
            }
        }

        if (cheapest != -1) {
            total += cheapest
        }
    }

    println(total)

}