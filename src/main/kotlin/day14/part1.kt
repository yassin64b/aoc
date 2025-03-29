package org.example.day14.part1

import java.io.BufferedReader
import java.io.File

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

    val finalMap = MutableList(DIM_Y) {
        MutableList(DIM_X) { 0 }
    }

    var quadrantUpLeft = 0L
    var quadrantUpRight = 0L
    var quadrantDownLeft = 0L
    var quadrantDownRight = 0L

    particles.forEachIndexed { index, particle ->
        println("Particle $index: (${particle.px}, ${particle.py}, ${particle.vx}, ${particle.vy})")

        val newParticle = particle.advanceBySeconds(100)

        println("New particle $newParticle")
        finalMap[newParticle.py][newParticle.px] += 1

        if (newParticle.px < DIM_X / 2) {
            if (newParticle.py < DIM_Y / 2) {
                quadrantUpLeft += 1
            } else if (newParticle.py > DIM_Y / 2) {
                quadrantDownLeft += 1
            }
        } else if (newParticle.px > DIM_X / 2) {
            if (newParticle.py < DIM_Y / 2) {
                quadrantUpRight += 1
            } else if (newParticle.py > DIM_Y / 2) {
                quadrantDownRight += 1
            }
        }
    }

    for (row in finalMap) {
        println(row.joinToString("") { if (it == 0) "." else it.toString() })
    }

    println("quadrant values $quadrantUpLeft, $quadrantDownLeft, $quadrantUpRight, $quadrantDownRight")
    println("multiplication: ${quadrantUpLeft * quadrantDownLeft * quadrantDownRight * quadrantUpRight}")
}