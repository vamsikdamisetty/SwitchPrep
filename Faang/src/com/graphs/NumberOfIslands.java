package com.graphs;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Number of Islands - LeetCode 200
 *
 * Problem: Given an m x n 2D binary grid of '1's (land) and '0's (water),
 * return the number of islands.
 *
 * An island is surrounded by water and is formed by connecting adjacent
 * lands horizontally or vertically.
 *
 * Approach: BFS (Breadth-First Search)
 * - Iterate over every cell in the grid.
 * - When an unvisited land cell ('1') is found, increment the island count
 *   and trigger a BFS to mark all connected land cells as visited.
 * - This ensures each island is counted exactly once.
 *
 * Time Complexity : O(N * M) — every cell is visited at most once.
 * Space Complexity: O(N * M) — visited array + BFS queue in the worst case
 *                              (entire grid is one island).
 */
public class NumberOfIslands {

    /**
     * Returns the number of islands in the given grid.
     *
     * @param grid 2D char array where '1' = land, '0' = water
     * @return total number of distinct islands
     */
    public int numIslands(char[][] grid) {
        int n = grid.length;       // number of rows
        int m = grid[0].length;    // number of columns
        boolean[][] visited = new boolean[n][m]; // tracks which cells have been explored

        int islands = 0;

        // Scan every cell in the grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // Start a new BFS only if the cell is unvisited land
                if (!visited[i][j] && grid[i][j] == '1') {
                    islands++;                      // discovered a new island
                    bfs(i, j, visited, grid, n, m); // flood-fill to mark entire island
                }
            }
        }
        return islands;
    }

    /**
     * BFS flood-fill starting from (row, col).
     * Marks all land cells reachable (4-directionally) from the start as visited.
     *
     * @param row     starting row
     * @param col     starting column
     * @param visited boolean matrix tracking explored cells
     * @param grid    the input grid
     * @param n       total rows
     * @param m       total columns
     */
    /*
    For Exploring diagonal directions
        for(int deltaRow = -1; deltaRow <= 1; deltaRow++){
                for(int deltaCol = -1; deltaCol <= 1; deltaCol++){
                    int neighbourRow = cRow + deltaRow;
                    int neighbourCol = cCol + deltaCol;
                    if(neighbourRow >= 0 && neighbourRow < n && neighbourCol >= 0 && neighbourCol < m
                    && grid[neighbourRow][neighbourCol] == '1' && !visited[neighbourRow][neighbourCol]){
                        visited[neighbourRow][neighbourCol] = true;
                        q.offer(new Pair(neighbourRow,neighbourCol));
                    }
                }
            }
     */
    void bfs(int row, int col, boolean[][] visited, char[][] grid, int n, int m) {
        visited[row][col] = true; // mark the starting cell visited immediately

        Queue<Pair> q = new LinkedList<>();
        q.offer(new Pair(row, col)); // seed the queue with the starting cell

        // Direction arrays for up, right, down, left movements
        // Delta Row and Delta Col
        int[] dRow = {-1, 0, 1,  0};
        int[] dCol = { 0, 1, 0, -1};

        while (!q.isEmpty()) {
            Pair p = q.poll();
            int cRow = p.first;
            int cCol = p.second;

            // Explore all 4 neighbours of the current cell
            for (int i = 0; i < 4; i++) {
                int neighbourRow = cRow + dRow[i];
                int neighbourCol = cCol + dCol[i];

                // Add neighbour to queue only if it is:
                //  1. Within grid bounds
                //  2. A land cell ('1')
                //  3. Not yet visited
                if (neighbourRow >= 0 && neighbourRow < n &&
                        neighbourCol >= 0 && neighbourCol < m &&
                        grid[neighbourRow][neighbourCol] == '1' &&
                        !visited[neighbourRow][neighbourCol]) {

                    visited[neighbourRow][neighbourCol] = true; // mark before enqueuing to avoid duplicates
                    q.offer(new Pair(neighbourRow, neighbourCol));
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Main — quick manual test
    // -----------------------------------------------------------------------
    public static void main(String[] args) {
        NumberOfIslands sol = new NumberOfIslands();

        // Grid 1: expected output = 3
        char[][] grid1 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("Number of islands (grid1): " + sol.numIslands(grid1)); // 3

        // Grid 2: expected output = 1
        char[][] grid2 = {
            {'1','1','1'},
            {'0','1','0'},
            {'1','1','1'}
        };
        System.out.println("Number of islands (grid2): " + sol.numIslands(grid2)); // 1
    }
}


/**
 * Simple pair (row, col) used as a BFS queue element.
 */
class Pair {
    int first;   // row index
    int second;  // column index

    public Pair(int f, int s) {
        first  = f;
        second = s;
    }
}