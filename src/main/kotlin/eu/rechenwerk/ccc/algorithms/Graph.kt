package eu.rechenwerk.ccc.algorithms

class Graph<V>(
    private val vertecies: List<V>,
    edges: List<Pair<V, V>>
) {
    private val adjecencyMatrix: MutableMap<V, MutableList<V>> = mutableMapOf()
    init {
        vertecies.forEach {
            adjecencyMatrix[it] = mutableListOf()
        }
        edges.forEach { (v1, v2) ->
            if(adjecencyMatrix.containsKey(v1) && adjecencyMatrix.containsKey(v2)) {
                adjecencyMatrix[v1]?.add(v2)
                adjecencyMatrix[v2]?.add(v1)
            }
        }
    }
    constructor(vertecieGenerator: () -> List<V>, edgeGenerator: () -> List<Pair<V,V>>) :
            this(vertecieGenerator.invoke(), edgeGenerator.invoke())

    fun addVertex(vertex: V) {
        adjecencyMatrix.putIfAbsent(vertex, mutableListOf())
    }

    fun addEdge(vertex1: V, vertex2: V) {
        adjecencyMatrix[vertex1]?.add(vertex2)
        adjecencyMatrix[vertex2]?.add(vertex1) // For undirected graph
    }

    fun getVertices(): Set<V> {
        return adjecencyMatrix.keys.toSet()
    }

    fun getNeighbors(vertex: V): List<V>? {
        return adjecencyMatrix[vertex]?.toList()
    }

    fun getAdjecentyMatrix(): Map<V, List<V>> {
        return adjecencyMatrix
            .map{ it.key to adjecencyMatrix[it.key]!!.toList() }.toMap()
    }
}

class Node<V>(value: V) {
}
