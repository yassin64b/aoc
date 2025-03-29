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

//    val input = """#######
//#...#.#
//#.....#
//#..OO@#
//#..O..#
//#.....#
//#######
//
//<vv<<^^<<^^""".trim()

    // Split the input by empty line to separate the map from the instructions
    val parts = input.split("\n\n")

    val transformedMap : List<MutableList<Char>> = run {
        // Parse the map part (first part) into List<List<Char>>
        val mapOfChars: List<MutableList<Char>> = parts[0].split("\n").map { line ->
            line.toMutableList()
        }

        transformMap(mapOfChars)
    }

    // Parse the instructions part (second part)
    // First, clean up by removing all whitespace
    val instructionsText = parts[1].replace("\n", "").trim()

    // Convert to list of characters
    val instructions = instructionsText.toList()

    // Print the map to verify
    println("Map of Characters:")
    transformedMap.forEach { row ->
        println(row.joinToString(""))
    }

    println("\nInstructions (first 50 chars):")
    println(instructions.take(50).joinToString(""))
    println("Total instruction length: ${instructions.size}")

    processMovement(transformedMap, instructions)
    val finalMap = transformedMap

    // Print the final map to verify
    println("Final Map of Characters:")
    finalMap.forEach { row ->
        println(row.joinToString(""))
    }

    val sum = finalMap.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (cell == '[') x + (y * 100) else null
        }
    }.sum()

    println("final sum: $sum")
}

// Function to transform the map to be twice as wide
fun transformMap(originalMap: List<List<Char>>): List<MutableList<Char>> {
    return originalMap.map { row ->
        row.flatMap { cell ->
            when (cell) {
                'O' -> listOf('[', ']')
                '@' -> listOf('@', '.')
                else -> listOf(cell, cell)
            }
        }.toMutableList()
    }
}

// Main function to process all movement instructions
fun processMovement(map: List<MutableList<Char>>, instructions: List<Char>) {
    // Find initial player position
    var playerX = -1
    var playerY = -1

    map.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            if (cell == '@') {
                playerX = x
                playerY = y
                return@forEachIndexed
            }
        }
        if (playerX != -1) return@forEachIndexed
    }

    if (playerX == -1 || playerY == -1) {
        println("Player not found!")
        return
    }

    // Process each instruction
    for (instruction in instructions) {
        // Calculate direction based on instruction
        val (dirX, dirY) = when (instruction) {
            '^' -> Pair(0, -1)
            'v' -> Pair(0, 1)
            '<' -> Pair(-1, 0)
            '>' -> Pair(1, 0)
            else -> {
                println("Unknown instruction: $instruction")
                continue
            }
        }

        // Calculate next position
        val nextX = playerX + dirX
        val nextY = playerY + dirY

        // Check bounds
        if (nextY < 0 || nextY >= map.size || nextX < 0 || nextX >= map[nextY].size) {
            println("Can't move out of bounds!")
            continue
        }

        // Check if there's a wall
        if (map[nextY][nextX] == '#') {
            println("Can't move into a wall!")
            continue
        }

        // Check if there's a box
        if (map[nextY][nextX] == '[' || map[nextY][nextX] == ']') {
            // Adjust to get the start of the box if we hit the right bracket
            val boxX = if (map[nextY][nextX] == ']') nextX - 1 else nextX

            // First, check if we can push the box(es) without actually moving them
            if (canPushBoxes(map, boxX, nextY, dirX, dirY, true)) {
                // Actually push the boxes
                canPushBoxes(map, boxX, nextY, dirX, dirY, false)

                // Move the player
                map[playerY][playerX] = '.' // Clear old position
                map[nextY][nextX] = '@'     // Place player at new position

                playerX = nextX
                playerY = nextY
                println("Pushed box(es) and moved $instruction")
            } else {
                println("Can't push box(es) in this direction $instruction!")
            }
        } else {
            // Simple move, no pushing
            map[playerY][playerX] = '.' // Clear old position
            map[nextY][nextX] = '@'     // Place player at new position

            playerX = nextX
            playerY = nextY
            println("Moved $instruction")
        }

//        // Print the final map to verify
//        println("Current Map of Characters:")
//        map.forEach { row ->
//            println(row.joinToString(""))
//        }
    }
}

// Recursive function to check if boxes can be pushed and optionally push them
fun canPushBoxes(
    map: List<MutableList<Char>>,
    boxX: Int,
    boxY: Int,
    dirX: Int,
    dirY: Int,
    dryRun: Boolean
): Boolean {
    // Check if this is a box
    if (boxY < 0 || boxY >= map.size || boxX < 0 || boxX >= map[boxY].size || map[boxY][boxX] != '[') {
        return true  // Not a box, so nothing to push
    }

    // Calculate the next position (where this box would move to)
    val nextX = boxX + dirX
    val nextY = boxY + dirY

    // Check bounds for the next position
    if (nextY < 0 || nextY >= map.size || nextX < 0 || nextX + 1 >= map[nextY].size) {
        return false  // Out of bounds
    }

    // Check if there's a wall
    if (map[nextY][nextX] == '#' || map[nextY][nextX + 1] == '#') {
        return false  // Can't push into a wall
    }

    // Check for the next box based on direction
    if (dirX > 0) {
        // When pushing right, we need to look 2 positions ahead
        val twoAheadX = boxX + 2
        val twoAheadY = boxY

        // Check if the position is within bounds and contains a box
        if (twoAheadX >= 0 && twoAheadX < map[twoAheadY].size && map[twoAheadY][twoAheadX] == '[') {
            if (!canPushBoxes(map, twoAheadX, twoAheadY, dirX, dirY, dryRun)) {
                return false
            }
        }
    } else if (dirX < 0) {
        // For other directions, check the immediate next position
        if (map[nextY][nextX] == '[' || map[nextY][nextX] == ']') {
            val checkX = if (map[nextY][nextX] == ']') nextX - 1 else nextX

            if (!canPushBoxes(map, checkX, nextY, dirX, dirY, dryRun)) {
                return false
            }
        }
    } else {
        // When pushing up or down, check both left and right positions
        if (map[nextY][nextX] == '[') {
            if (!canPushBoxes(map, nextX, nextY, dirX, dirY, dryRun)) {
                return false
            }
        } else if (map[nextY][nextX] == ']' && nextX > 0 && map[nextY][nextX - 1] == '[') {
            if (!canPushBoxes(map, nextX - 1, nextY, dirX, dirY, dryRun)) {
                return false
            }
        }

        if (map[nextY][nextX + 1] == '[') {
            if (!canPushBoxes(map, nextX + 1, nextY, dirX, dirY, dryRun)) {
                return false
            }
        } else if (map[nextY][nextX + 1] == ']' && map[nextY][nextX] == '[') {
            if (!canPushBoxes(map, nextX, nextY, dirX, dirY, dryRun)) {
                return false
            }
        }
    }

    // If we're not just checking (dryRun=false), actually push the box
    if (!dryRun) {
        // First clear the old position
        map[boxY][boxX] = '.'
        map[boxY][boxX + 1] = '.'

        // Then place the box in the new position
        map[nextY][nextX] = '['
        map[nextY][nextX + 1] = ']'
    }

    return true  // Box can be pushed
}