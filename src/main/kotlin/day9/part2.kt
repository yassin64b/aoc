package org.exmaple.day9.part1

import java.io.BufferedReader
import java.io.File


fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val input = fileText.trim()

//    val input = "2333133121414131402"

    val occupiedSpaces = mutableListOf<Triple<Int, Int, Int>>()
    var freeSpaces = mutableListOf<Pair<Int, Int>>()

    var nextId = 0
    var index = 0
    for (i in input.indices) {
        val times = input[i].digitToInt()
        if (i % 2 == 0) {
            occupiedSpaces.add(Triple(nextId, index, times))
            nextId++
        } else {
            freeSpaces.add(Pair(index, times))
        }
        index += times
    }

    println("starting occupied $occupiedSpaces")
    println("starting free $freeSpaces")

    for (i in occupiedSpaces.size - 1 downTo 0) {
        val (id, index, len) = occupiedSpaces[i]
        for (j in freeSpaces.indices) {
            val (freeIndex, freeLen) = freeSpaces[j]
            if (freeLen >= len && freeIndex < index) {
                // create a new space in the previous occupied space
                freeSpaces.add(Pair(index, len))
                // insert into the new position
                occupiedSpaces[i] = Triple(id, freeIndex, len)
                // remove the free space
                freeSpaces[j] = Pair(freeIndex + len, freeLen - len)
                freeSpaces = mergeFreeSpaces(freeSpaces)
                break
            }
        }
    }

    println("ending occupied $occupiedSpaces")
    println("ending free $freeSpaces")

    var result = 0L
    for (entry in occupiedSpaces) {
        val (id, index, len) = entry
        for (i in index until index+len) {
            result += i * id
        }
    }

    println("result $result")

}

// Not necessary actually, since we start from the back!
fun mergeFreeSpaces(freeSpaces: MutableList<Pair<Int, Int>>): MutableList<Pair<Int, Int>> {
    freeSpaces.sortBy { it.first }
    val newFreeSpaces: MutableList<Pair<Int, Int>> = mutableListOf(freeSpaces[0])
    for (i in 1..<freeSpaces.size) {
        val lastEntry = newFreeSpaces[newFreeSpaces.size-1]
        if (freeSpaces[i].first == lastEntry.first + lastEntry.second) {
            // merge into lastEntry instead
            newFreeSpaces[newFreeSpaces.size-1] = Pair(lastEntry.first, lastEntry.second + freeSpaces[i].second)
        } else {
            newFreeSpaces.add(freeSpaces[i])
        }
    }
    return newFreeSpaces
}

