package org.example.day14.part2

import java.io.BufferedReader
import java.io.File
import java.lang.Thread.sleep
import java.time.Duration

const val DIM_X = 101
const val DIM_Y = 103

data class Particle(val px: Int, val py: Int, val vx: Int, val vy: Int) {

    fun advanceBySeconds(numSeconds: Int): Particle {
        return Particle(
            ((px + vx * numSeconds) % DIM_X + DIM_X) % DIM_X,
            ((py + vy * numSeconds) % DIM_Y + DIM_Y) % DIM_Y,
            vx,
            vy
        )
    }
}

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """p=0,4 v=3,-3
//p=6,3 v=-1,-3
//p=10,3 v=-1,2
//p=2,0 v=2,-1
//p=0,0 v=1,3
//p=3,0 v=-2,-2
//p=7,6 v=-1,-3
//p=3,0 v=-1,-2
//p=9,3 v=2,3
//p=7,3 v=-1,2
//p=2,4 v=2,-3
//p=9,5 v=-3,-3""".trim()

    // Parse each line
    val particles = input.lines().map { line ->
        // Use regex to extract the values
        val regex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
        val matchResult = regex.find(line)

        if (matchResult != null) {
            val (px, py, vx, vy) = matchResult.destructured
            Particle(px.toInt(), py.toInt(), vx.toInt(), vy.toInt())
        } else {
            throw IllegalArgumentException("Invalid input format: $line")
        }
    }

    var counter = 0
    while(counter <= 100000) {
        val finalMap = MutableList(DIM_Y) {
            MutableList(DIM_X) { 0 }
        }

        particles.forEachIndexed { index, particle ->
            val newParticle = particle.advanceBySeconds(counter)
            finalMap[newParticle.py][newParticle.px] += 1
        }

        // Consider only maps where at least 8 robots are in a row
        var isCandidate = false
        for (i in finalMap.indices) {
            for (j in 0..DIM_X-8) {
                if (finalMap[i][j] > 0
                    && finalMap[i][j+1] > 0
                    && finalMap[i][j+2] > 0
                    && finalMap[i][j+3] > 0
                    && finalMap[i][j+4] > 0
                    && finalMap[i][j+5] > 0
                    && finalMap[i][j+6] > 0
                    && finalMap[i][j+7] > 0
                ) {
                    isCandidate = true
                    break
                }
            }
        }
        if (isCandidate) {
            for (row in finalMap) {
                println(row.joinToString("") { if (it == 0) "." else it.toString() })
            }

            sleep(Duration.ofSeconds(10).toMillis())
            print("This was second $counter")
            println("Continue---------------------------------------------------------------------\n\n\n")
        }

        counter += 1
    }
}