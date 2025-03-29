package org.example.day15.part1

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = """##########
//#..O..O.O#
//#......O.#
//#.OO..O.O#
//#..O@..O.#
//#O#..O...#
//#O..O..O.#
//#.OO.O.OO#
//#....O...#
//##########
//
//<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
//vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
//><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
//<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
//^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
//^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
//>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
//<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
//^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
//v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""".trim()

    // Split the input by empty line to separate the map from the instructions
    val parts = input.split("\n\n")

    // Parse the map part (first part) into List<List<Char>>
    val mapOfChars: List<MutableList<Char>> = parts[0].split("\n").map { line ->
        line.toMutableList()
    }

    // Parse the instructions part (second part)
    // First, clean up by removing all whitespace
    val instructionsText = parts[1].replace("\n", "").trim()

    // Convert to list of characters
    val instructions = instructionsText.toList()

    // Print the map to verify
    println("Map of Characters:")
    mapOfChars.forEach { row ->
        println(row.joinToString(""))
    }

    println("\nInstructions (first 50 chars):")
    println(instructions.take(50).joinToString(""))
    println("Total instruction length: ${instructions.size}")

    // Find the position of the '@' character
    var playerX = -1
    var playerY = -1

    for (y in mapOfChars.indices) {
        for (x in mapOfChars[y].indices) {
            if (mapOfChars[y][x] == '@') {
                playerX = x
                playerY = y
                break
            }
        }
        if (playerX != -1) break  // Exit outer loop if found
    }

    if (playerX != -1 && playerY != -1) {
        println("Found @ at position: x=$playerX, y=$playerY")
    } else {
        println("@ character not found in the map")
    }

    // Process each instruction
    for (i in instructions.indices) {
        val instruction = instructions[i]

        // Store current position
        val currentX = playerX
        val currentY = playerY

        // Calculate new position based on instruction
        when (instruction) {
            '^' -> playerY--  // Move up
            'v' -> playerY++  // Move down
            '<' -> playerX--  // Move left
            '>' -> playerX++  // Move right
            else -> {
                println("Unknown instruction: $instruction")
                continue
            }
        }

        // Check if the new position is valid
        if (playerY >= 0 && playerY < mapOfChars.size &&
            playerX >= 0 && playerX < mapOfChars[playerY].size) {

            // Check if the new position is a wall
            if (mapOfChars[playerY][playerX] == '#') {
                // Can't move into a wall, revert position
                playerX = currentX
                playerY = currentY
                println("Can't move into a wall! Instruction: $instruction")
            } else if (mapOfChars[playerY][playerX] == 'O') {
                // Check if there is any free space to push all the consecutive O's into this direction
                var boxCount = 0
                var canPush = false

                // Calculate the direction vector based on the current instruction
                val dirX = playerX - currentX
                val dirY = playerY - currentY

                // Count consecutive boxes in the push direction
                var checkX = playerX
                var checkY = playerY
                while (checkY >= 0 && checkY < mapOfChars.size &&
                    checkX >= 0 && checkX < mapOfChars[checkY].size &&
                    mapOfChars[checkY][checkX] == 'O') {
                    boxCount++
                    checkX += dirX
                    checkY += dirY
                }

                // Check if the space after the last box is free
                if (checkY >= 0 && checkY < mapOfChars.size &&
                    checkX >= 0 && checkX < mapOfChars[checkY].size &&
                    mapOfChars[checkY][checkX] == '.') {
                    canPush = true
                }

                if (canPush) {
                    // Place a box at the final free position
                    mapOfChars[checkY][checkX] = 'O'

                    // Move the player to the first box's position
                    mapOfChars[playerY][playerX] = '@'

                    // Clear the player's previous position
                    mapOfChars[currentY][currentX] = '.'

                    println("Pushed $boxCount box(es) and moved $instruction to x=$playerX, y=$playerY")
                } else {
                    // Can't push boxes, revert position
                    playerX = currentX
                    playerY = currentY
                    println("Can't push boxes in this direction! Instruction: $instruction")
                }
            } else {
                // Update the map - remove @ from old position
                mapOfChars[currentY][currentX] = '.'

                // Place @ at new position
                mapOfChars[playerY][playerX] = '@'

                println("Moved $instruction to x=$playerX, y=$playerY")
            }
        } else {
            // Out of bounds, revert position
            playerX = currentX
            playerY = currentY
            println("Can't move out of bounds! Instruction: $instruction")
        }
    }

    // Print the final map to verify
    println("Final Map of Characters:")
    mapOfChars.forEach { row ->
        println(row.joinToString(""))
    }

    var sum = 0

    for (y in mapOfChars.indices) {
        for (x in mapOfChars[y].indices) {
            if (mapOfChars[y][x] == 'O') {
                // Add x + (y * 100) to the sum
                sum += x + (y * 100)
            }
        }
    }

    println("final sum: $sum")
}