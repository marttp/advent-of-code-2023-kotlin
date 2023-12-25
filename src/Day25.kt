import helper.util.println
import helper.util.readInput
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import java.math.BigDecimal

fun main() {
    val input1 = readInput("input-25")
    // Sample
    val sample1 = readInput("sample1-25")
    // Part1
    check(Day25Util.part1(sample1) == 54)
    val output1 = Day25Util.part1(input1)
    check(output1 == 548960)
    output1.println()
}

private object Day25Util {

    fun part1(input: List<String>): Int {
        // rgl: hfp vxk
        val undirectedGraph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        for (line in input) {
            val (root, neighbors) = line.split(":")
            undirectedGraph.addVertex(root)
            for (neighbor in neighbors.trim().split(" ")) {
                undirectedGraph.addVertex(neighbor)
                undirectedGraph.addEdge(root, neighbor)
            }
        }
        // Cut the graph into two parts
        val minimumCut = StoerWagnerMinimumCut(undirectedGraph).minCut()
        // Disconnect vertices
        undirectedGraph.removeAllVertices(minimumCut)
        return undirectedGraph.vertexSet().size * minimumCut.size
    }

    fun part2(input: List<String>): Int {
        return 0
    }

}