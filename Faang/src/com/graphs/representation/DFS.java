package com.graphs.representation;

import java.util.ArrayList;
import java.util.List;

/*
    DFS Time and Space Complexity

    Time Complexity: O(V + 2E)
    V = number of vertices
    E = number of edges
    Each vertex is visited once → O(V)

    For each vertex, we iterate over all its neighbors (degrees) → O(2E)

    At max all degrees would be 2 * E
    if all nodes are connected with each other
    (undirected graph — each edge stored twice)

    Total = O(V + 2E) → simplified to O(V + E)

    Space Complexity: O(V)
    visited[] array → O(V)
    dfs result list → O(V)
    Recursive call stack → O(V) in worst case (linear chain graph)
    Total auxiliary space = O(V)


    Summary Table

    Complexity
    Time
    O(V + 2E) → O(V + E)
    Space
    O(V)
    Key Difference vs BFS Space:
    Algorithm
    Space for traversal
    BFS
    O(V) — explicit Queue
    DFS
    O(V) — implicit call stack
    Note: DFS worst case stack depth = O(V)
    when graph is a linear chain (e.g., 0→1→2→3→...→V).
    For a balanced tree-like graph, stack depth = O(log V).
    Unlike BFS, DFS uses the system call stack instead of an explicit queue,
    but both are O(V) space.
 */
public class DFS {
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

        List<Integer> dfs = new ArrayList<>();
        g.dfsOfGraph(dfs,0,new boolean[10]);

        g.printGraph();
        System.out.println(STR."DFS :: \{dfs}");
        System.out.println(STR."BFS :: \{g.bfsOfGraph()}");
    }
}
