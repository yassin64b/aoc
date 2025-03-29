package org.example.day17.part22

import java.io.BufferedReader
import java.io.File
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

// Too slow, cannot brute force a ~16-digit number
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//     val input = """Register A: 2024
//Register B: 0
//Register C: 0
//
//Program: 0,3,5,4,3,0""".trim()
    println("input: $input")

    val program = parseRegisterMachineInput(input)
    println("program $program")

    // Number of parallel workers (adjust based on your CPU)
    val numWorkers = Runtime.getRuntime().availableProcessors()
    println("Using $numWorkers parallel workers")

    // Use AtomicInteger for thread-safe result sharing
    val result = AtomicLong(-1)

    withContext(Dispatchers.Default.limitedParallelism(32)) {
        val timeInMillis = measureTimeMillis {
            // Create chunks of work for each worker
            val chunkSize = 2_000_000_000L // Adjust this based on your workload

            // Launch initial set of workers
            val jobs = List(numWorkers) { workerId ->
                val startRange = workerId * chunkSize
                val endRange = (workerId + 1) * chunkSize

                launch {
                    val foundValue = processRange(startRange, endRange, workerId, program)
                    if (foundValue >= 0 && result.compareAndSet(-1L, foundValue)) {
                        println("Worker $workerId found result: register a starting value $foundValue")
                    }
                }
            }

            // Create a supervisor job that continuously spawns new workers for higher ranges
            val supervisor = launch {
                var nextChunkStart = numWorkers * chunkSize

                while (result.get() < 0 && isActive) {
                    val chunkStart = nextChunkStart
                    val chunkEnd = chunkStart + chunkSize
                    nextChunkStart = chunkEnd

                    launch {
                        println("Spawning additional worker for range $chunkStart-$chunkEnd")
                        val foundValue = processRange(chunkStart, chunkEnd, -1, program)
                        if (foundValue >= 0 && result.compareAndSet(-1, foundValue)) {
                            println("Additional worker found result: register a starting value $foundValue")
                        }
                    }

                    delay(500) // Small delay to prevent excessive job creation
                }
            }

            // Wait for all initial jobs to complete
            jobs.forEach { it.join() }

            // If we found a result, cancel the supervisor
            if (result.get() >= 0) {
                supervisor.cancel()
            } else {
                // Wait for the supervisor to find the result
                supervisor.join()
            }
        }

        val finalResult = result.get()
        if (finalResult >= 0) {
            println("Final result: register a starting value $finalResult")
            println("Computation took ${timeInMillis}ms")
        } else {
            println("No solution found within the search range")
        }
    }

}

/**
 * Process a range of starting values for register A
 *
 * @param startRange The start of the range (inclusive)
 * @param endRange The end of the range (exclusive)
 * @param workerId The ID of the worker (-1 for dynamic workers)
 * @param baseProgram The base program state to copy
 * @return The found value, or -1 if no solution found in this range
 */
suspend fun processRange(
    startRange: Long,
    endRange: Long,
    workerId: Int,
    baseProgram: RegisterMachineState
): Long {
    for (aValue in startRange until endRange) {
        // Create a copy of the program state for this worker
        val workerProgram = baseProgram.copy(
            registerA = aValue,
            registerB = baseProgram.registerB,
            registerC = baseProgram.registerC,
            instructions = baseProgram.instructions
        )

        var instructionPointer = 0
        val output: MutableList<Int> = mutableListOf()

        // Execute the program with current register A value
        while (instructionPointer < workerProgram.instructions.size) {
            val instruction = workerProgram.instructions[instructionPointer]
            val operand = workerProgram.instructions[instructionPointer + 1].toLong()

            when (instruction) {
                0 -> workerProgram.registerA /= (1 shl workerProgram.combo(operand).toInt())
                1 -> workerProgram.registerB = workerProgram.registerB xor operand
                2 -> workerProgram.registerB = workerProgram.combo(operand) % 8
                3 -> {
                    if (workerProgram.registerA != 0L) {
                        instructionPointer = operand.toInt()
                        continue
                    }
                }
                4 -> workerProgram.registerB = workerProgram.registerB xor workerProgram.registerC
                5 -> output.add((workerProgram.combo(operand) % 8).toInt())
                6 -> workerProgram.registerB = workerProgram.registerA / (1 shl workerProgram.combo(operand).toInt())
                7 -> workerProgram.registerC = workerProgram.registerA / (1 shl workerProgram.combo(operand).toInt())
            }
            instructionPointer += 2
        }

        // Check if this is the solution we're looking for
        if (output == workerProgram.instructions) {
            println("Output $output vs ${workerProgram.instructions} matches")
            return aValue
        }

        // Status update every N iterations
        if (workerId >= 0 && aValue % 10_000_000 == 0L) {
            println("Worker $workerId processed up to $aValue")
        }

        // Occasionally yield to allow other coroutines to run
        if (aValue % 1000000 == 0L) {
            yield()
        }
    }
    println("worker $workerId done")

    return -1 // No solution found in this range
}

fun RegisterMachineState.combo(operand: Long): Long {
    return when {
        operand <= 3 -> operand
        operand == 4L -> this.registerA
        operand == 5L -> this.registerB
        operand == 6L -> this.registerC
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
    var instructions = listOf<Int>()

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
            line.startsWith("Program:") -> {
                instructions = line.substringAfter(":").trim()
                    .split(",")
                    .map { it.trim().toInt() }
            }
        }
    }

    return RegisterMachineState(registerA, registerB, registerC, instructions)
}