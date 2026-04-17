package com.graphs;

import com.graphs.representation.GraphList;

import java.util.List;

public class BFS {


    /*
    BFS Time and Space Complexity

    Time Complexity: O(V) + O(2E)
    V = number of vertices
    E = number of edges
    Each vertex is visited once → O(V)
    For each vertex, we iterate over all its neighbors (degrees) → O(2E)
    At max all degrees would be 2 * E if all the nodes are connected with each other
    Total = O(V + E)

    Space Complexity: O(V)
    visited[] array → O(V)
    bfs result list → O(V)
    queue holds at most V nodes → O(V)
    The adjacency list itself (adjList) is O(V + E)
    but it's pre-existing, not created by this method
    Total auxiliary space = O(V)

    Summary Table

    Complexity
    Time
    O(V + E)
    Space
    O(V)

 */
    public static void main(String[] args) {
        GraphList g = new GraphList(10);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(1, 6);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.addEdge(4, 5);
        g.addEdge(5, 8);
        g.addEdge(6, 7);
        g.addEdge(6, 9);
        g.addEdge(7, 8);




        g.printGraph();
        System.out.println(STR."BFS :: \{g.bfsOfGraph()}");
    }
}
