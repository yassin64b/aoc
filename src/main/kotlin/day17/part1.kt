package org.example.day17.part1

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """Register A: 729
//Register B: 0
//Register C: 0
//
//Program: 0,1,5,4,3,0""".trim()

    val program = parseRegisterMachineInput(input)
    var instructionPointer = 0
    val output : MutableList<Int> = mutableListOf()
    while (instructionPointer < program.instructions.size) {
        val instruction = program.instructions[instructionPointer]
        val operand = program.instructions[instructionPointer + 1]

        when (instruction) {
            0 -> {
                program.registerA /= (1 shl program.combo(operand))
            }
            1 -> {
                program.registerB = program.registerB xor operand
            }
            2 -> {
                program.registerB = program.combo(operand) % 8
            }
            3 -> {
                if (program.registerA != 0) {
                    instructionPointer = operand
                    continue
                }
            }
            4 -> {
                program.registerB = program.registerB xor program.registerC
            }
            5 -> {
                output.add(program.combo(operand) % 8)
            }
            6 -> {
                program.registerB = program.registerA / (1 shl program.combo(operand))
            }
            7 -> {
                program.registerC = program.registerA / (1 shl program.combo(operand))
            }
        }
        instructionPointer += 2
        println("now $program and next $instructionPointer")
    }

    println("input $program")
    println("output ${output.joinToString(",")}")
}

fun RegisterMachineState.combo(operand: Int): Int {
    return when {
        operand <= 3 -> operand
        operand == 4 -> this.registerA
        operand == 5 -> this.registerB
        operand == 6 -> this.registerC
        else -> {
            throw IllegalArgumentException("Operand $operand is invalid")
        }
    }
}

data class RegisterMachineState(
    var registerA: Int,
    var registerB: Int,
    var registerC: Int,
    val instructions: List<Int>
)

fun parseRegisterMachineInput(input: String): RegisterMachineState {
    val lines = input.trim().lines().filter { it.isNotEmpty() }

    var registerA = 0
    var registerB = 0
    var registerC = 0
    var instructions = listOf(-1)
    for (line in lines) {
        when {
            line.startsWith("Register A:") -> {
                registerA = line.substringAfter(":").trim().toInt()
            }
            line.startsWith("Register B:") -> {
                registerB = line.substringAfter(":").trim().toInt()
            }
            line.startsWith("Register C:") -> {
                registerC = line.substringAfter(":").trim().toInt()
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
