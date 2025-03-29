package org.exmaple.day5

import java.io.BufferedReader
import java.io.File

fun main() {

    val bufferedReader: BufferedReader = File("C:/Users/yassi/IdeaProjects/AoC24/resources/input.txt").bufferedReader()
    val fileText = bufferedReader.use { it.readText() }

    val lines = fileText.trim().lines()

    println(lines)

    val nodes: MutableMap<Int, Node> = mutableMapOf()

    var result = 0

    for (line in lines) {
        when {
            '|' in line -> processEdgeLine(line, nodes)
            line.isNotBlank() -> result += processNumberLine(line, nodes)
        }
    }

    println("middle numbers sum up to $result")
}

fun processNumberLine(line: String, nodes: MutableMap<Int, Node>): Int {
    var result = 0
    val nums = line.split(',')
    var valid = true
    val forbidden = mutableSetOf<Int>()
    for (j in nums.indices) {
        val num = nums[j].toInt()
        if (forbidden.contains(num)) {
            valid = false
            break
        }
        forbidden.addAll(nodes[num]?.dfs(mutableSetOf(), 1).orEmpty())
    }
    if (!valid) {
        // Construct topo order while only consider the current set of nodes (there may be loops otherwise)
        val seen = mutableSetOf<Int>()
        var topoOrder = mutableListOf<Int>()
        val curSet = nums.map { it.toInt() }.toSet()
        for (node in curSet) {
            if (!seen.contains(node)) {
                topoOrder.addAll(nodes[node]!!.dfs(seen, allowedNodes = curSet))
            }
        }
        require(topoOrder.size == nums.size) { "Lists much match size, ${topoOrder.size} vs ${nums.size}." }
        result += topoOrder[topoOrder.size/2]
    }
    return result
}

fun processEdgeLine(line: String, nodes: MutableMap<Int, Node>) {
    val nums = line.split('|')
    require(nums.size == 2) { "Each line with '|' should split into exactly 2 numbers." }

    val num1 = nums[0].toInt()
    val num2 = nums[1].toInt()
    if (!nodes.containsKey(num1)) {
        nodes[num1] = Node(num1, mutableListOf())
    }
    if (!nodes.containsKey(num2)) {
        nodes[num2] = Node(num2, mutableListOf())
    }

    require(nodes.contains(num1) && nodes.contains(num2)) { "Nodes $num1 and $num2 should exist in the map." }

    nodes[num2]!!.addEdge(nodes[num1]!!)
}
