package com.graphs;

/**
 * Problem: Number of Provinces (LeetCode 547)
 *
 * Given an n x n matrix `isConnected` where isConnected[i][j] = 1
 * means city i and city j are directly connected, return the total
 * number of provinces (groups of directly or indirectly connected cities).
 *
 * Approach: DFS on adjacency matrix
 * - Treat each city as a node in a graph
 * - For each unvisited city, start a DFS to mark all cities in the same province
 * - Each DFS call = one new province found
 *
 * Time Complexity : O(V²)
 *   - V = number of cities (isConnected.length)
 *   - No adjacency list here; we use an adjacency MATRIX
 *   - For each node, we scan the entire row → O(V) per node → O(V²) total
 *   - (Unlike adjacency list DFS which is O(V + 2E), matrix forces O(V²))
 *
 * Space Complexity: O(V)
 *   - visited[] array        → O(V)
 *   - Recursive call stack   → O(V) worst case (linear chain of cities)
 */
public class NumberOfProvinces {

    /**
     * Counts the number of provinces using DFS.
     *
     * @param isConnected n x n adjacency matrix where isConnected[i][j] = 1
     *                    means city i and city j are directly connected
     * @return total number of provinces (connected components)
     */
    public int findCircleNum(int[][] isConnected) {
        boolean[] visited = new boolean[isConnected.length];

        int provinces = 0;
        for (int i = 0; i < isConnected.length; i++) {
            if (!visited[i]) {
                provinces++;              // new unvisited city = new province
                dfs(visited, isConnected, i);
            }
        }
        return provinces;
    }

    void dfs(boolean[] visited, int[][] isConnected, int i) {
        visited[i] = true;

        for (int j = 0; j < isConnected.length; j++) {
            if (isConnected[i][j] == 1 && !visited[j]) {
                dfs(visited, isConnected, j);
            }
        }
    }

    public static void main(String[] args) {
        NumberOfProvinces solution = new NumberOfProvinces();

        // Example 1:
        // Cities: 0, 1, 2
        // 0 <-> 1 connected, 2 is isolated
        // Expected output: 2 provinces
        int[][] isConnected1 = {
            {1, 1, 0},
            {1, 1, 0},
            {0, 0, 1}
        };
        System.out.println("Example 1 - Provinces: " + solution.findCircleNum(isConnected1)); // 2

        // Example 2:
        // Cities: 0, 1, 2 — all isolated from each other
        // Expected output: 3 provinces
        int[][] isConnected2 = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };
        System.out.println("Example 2 - Provinces: " + solution.findCircleNum(isConnected2)); // 3

        // Example 3:
        // Cities: 0, 1, 2 — all connected to each other
        // Expected output: 1 province
        int[][] isConnected3 = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };
        System.out.println("Example 3 - Provinces: " + solution.findCircleNum(isConnected3)); // 1
    }
}

