package org.example.day17.part2

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """Register A: 2024
//Register B: 0
//Register C: 0
//
//Program: 0,3,5,4,3,0""".trim()

    val program = parseRegisterMachineInput(input)

    // Start with initial value 0
    val finalAValues = (0 until program.instructions.size).fold(listOf(0L)) { cumulativeValues, _ ->
        cumulativeValues.flatMap { cumulativeValue ->
            // Try all possible 3-bit values (0-7)
            (0..7).mapNotNull { aValue ->
                // Shift the cumulative value left by 3 bits and add the new value
                val newAValue = aValue.toLong() or (cumulativeValue shl 3)
                val output = checkProgram(program, newAValue)

                // Get the expected output - the last N instructions where N is the size of the output
                val expectedOutput = program.instructions.takeLast(output.size)

                // Log results
                println("a=$aValue and cum=$cumulativeValue produced $output")

                // Return the value if it matches the expected output, otherwise null
                if (output == expectedOutput) {
                    println("MATCHED --> added a candidate $newAValue")
                    newAValue
                } else null
            }
        }.also { println() } // Print an empty line after each instruction iteration
    }
    println("Final A values: $finalAValues")
    finalAValues.minOrNull()?.let { println("Minimum value: $it") }
}

private fun checkProgram(program: RegisterMachineState, aValue: Long): List<Int> {
    program.registerA = aValue
    var instructionPointer = 0
    val output: MutableList<Int> = mutableListOf()
    while (instructionPointer < program.instructions.size) {
        val instruction = program.instructions[instructionPointer]
        val operand = program.instructions[instructionPointer + 1]

        // My program
        // (2, 4) --> registerB = registerA % 8
        // (1, 5) --> registerB = registerB XOR 5
        // (7, 5) --> registerC = registerA / (2 ** registerB)
        // (1, 6) --> registerB = registerB XOR 6
        // (0, 3) --> registerA = registerA / (2 ** 3)
        // (4, 6) --> registerB = registerB XOR registerC
        // (5, 5) --> out.add(registerB % 8)
        // (3, 0) --> if registerA != 0 --> goto instruction 0
        when (instruction) {
            0 -> program.registerA /= (1L shl program.combo(operand).toInt())
            1 -> program.registerB = program.registerB xor operand.toLong()
            2 -> program.registerB = program.combo(operand) % 8
            3 -> if (program.registerA != 0L) {
                    instructionPointer = operand
                    continue
                }
            4 -> program.registerB = program.registerB xor program.registerC
            5 -> output.add((program.combo(operand) % 8).toInt())
            6 -> program.registerB = program.registerA / (1 shl program.combo(operand).toInt())
            7 -> program.registerC = program.registerA / (1 shl program.combo(operand).toInt())
        }
        instructionPointer += 2
    }

    return output
}

fun RegisterMachineState.combo(operand: Int): Long {
    return when {
        operand <= 3 -> operand.toLong()
        operand == 4 -> this.registerA
        operand == 5 -> this.registerB
        operand == 6 -> this.registerC
        else -> {
            throw IllegalArgumentException("Operand $operand is invalid")
        }
    }
}

data class RegisterMachineState(
    var registerA: Long,
    var registerB: Long,
    var registerC: Long,
    val instructions: List<Int>
)

fun parseRegisterMachineInput(input: String): RegisterMachineState {
    val lines = input.trim().lines().filter { it.isNotEmpty() }

    var registerA = 0L
    var registerB = 0L
    var registerC = 0L
    var instructions = emptyList<Int>()
    for (line in lines) {
        when {
            line.startsWith("Register A:") -> {
                registerA = line.substringAfter(":").trim().toLong()
            }
            line.startsWith("Register B:") -> {
                registerB = line.substringAfter(":").trim().toLong()
            }
            line.startsWith("Register C:") -> {
                registerC = line.substringAfter(":").trim().toLong()
            }
            line.startsWith( "Program:") -> {
                instructions = line.substringAfter(":").trim()
                    .split(",")
                    .map { it.trim().toInt() }
            }
        }
    }

    return RegisterMachineState(registerA, registerB, registerC, instructions)
}
