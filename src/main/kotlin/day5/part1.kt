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
    for (i in lines.indices) {
        if (lines[i].contains('|')) {
            val nums = lines[i].split('|')
            assert(nums.size == 2)

            val num1 = nums[0].toInt()
            val num2 = nums[1].toInt()
            if (!nodes.containsKey(num1)) {
                nodes[num1] = Node(num1, mutableListOf())
            }
            if (!nodes.containsKey(num2)) {
                nodes[num2] = Node(num2, mutableListOf())
            }

            assert(nodes.contains(num1))
            assert(nodes.contains(num2))

            nodes[num2]!!.addEdge(nodes[num1]!!)

            println("insert $num2 -> $num1")

        } else if (lines[i].isNotEmpty()) {

            val nums = lines[i].split(',')
            var valid = true
            val forbidden = mutableSetOf<Int>()
            for (j in nums.indices) {
                val num = nums[j].toInt()
                if (forbidden.contains(num)) {
                    println("line $i is invalid because of $num being already forbidden $forbidden")
                    valid = false
                    break
                }
                forbidden.addAll(nodes[num]?.dfs(mutableSetOf(), 1).orEmpty())
            }
            if (valid) {
                result += nums[nums.size/2].toInt()
            }
        }
    }

    println("middle numbers sum up to $result")

}

data class Node(val value: Int, var adj: MutableList<Node>) {

    fun addEdge(toNode: Node) {
        adj.add(toNode)
    }

    fun dfs(seen: MutableSet<Int>, depth: Int = Int.MAX_VALUE, allowedNodes: Set<Int>? = null): List<Int> {
        seen.add(value)
        val topoOrder = mutableListOf<Int>()
        for (node in adj) {
            if ((allowedNodes == null || allowedNodes.contains(node.value)) && !seen.contains(node.value) && depth > 0) {
                topoOrder.addAll(node.dfs(seen, depth - 1, allowedNodes))
            }
        }
        return topoOrder.plus(value)
    }
}