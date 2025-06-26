package com.backtracking;

import java.util.ArrayList;

public class RatInAMaze {

	/*
	 * Time Complexity: O(4^(m*n)), because on every cell we need to try 4 different directions.
	 * Space Complexity:  O(m*n), Maximum Depth of the recursion tree(auxiliary space).
	 */
	public static void main(String[] args) {

		int n = 4;
		int[][] a = { { 1, 0, 0, 0 }, { 1, 1, 0, 1 }, { 1, 1, 0, 0 }, { 0, 1, 1, 1 } };

		ArrayList<String> res = RatInAMaze.findPath(a, n);
		if (res.size() > 0) {
			for (int i = 0; i < res.size(); i++)
				System.out.print(res.get(i) + " ");
			System.out.println();
		} else {
			System.out.println(-1);
		}
	}

	/**
	 * Finds all possible paths from top-left to bottom-right in an n x n maze. The
	 * rat can move in four directions: Down, Left, Right, Up.
	 *
	 * @param m The maze grid, where 1 is a path and 0 is blocked.
	 * @param n The size of the maze (n x n).
	 * @return A list of strings, where each string represents a valid path.
	 */
	public static ArrayList<String> findPath(int[][] m, int n) {
		ArrayList<String> res = new ArrayList<>();
		boolean[][] visited = new boolean[n][n];

		// Direction arrays: Down, Left, Right, Up
		int[] di = { 1, 0, 0, -1 }; // row movement
		int[] dj = { 0, -1, 1, 0 }; // column movement
		
		// If starting cell is open, start exploring
		if (m[0][0] == 1)
			solve(0, 0, m, n, di, dj, visited, "", res);

		return res;
	}
	
	
	/**
	 * Recursive backtracking method to explore the maze.
	 *
	 * @param i        Current row index
	 * @param j        Current column index
	 * @param m        The maze matrix
	 * @param n        Size of the maze
	 * @param di       Array of row movements for D, L, R, U
	 * @param dj       Array of column movements for D, L, R, U
	 * @param visited  Tracks the visited cells
	 * @param path     The path string built so far
	 * @param res      The result list storing all valid paths
	 */
	static void solve(int i, int j, int[][] m, int n, int[] di, int[] dj, boolean[][] visited, String path,
			ArrayList<String> res) {

		// Base Case: reached destination (bottom-right corner)
		if (i == n - 1 && j == n - 1) {
			res.add(path);
			return;
		}

		String moves = "DLRU";
		for (int k = 0; k < 4; k++) {
			int nextI = i + di[k];
			int nextJ = j + dj[k];

			
			/*
			 * Ensure below things
			 * Row index is within the maze boundary
			 * Column index is within the maze boundary
			 * The next cell has not been visited yet in the current path
			 * The next cell is a valid path, not a 0
			 */
	        if (nextI >= 0 && nextI < n && nextJ >= 0 && nextJ < n &&
	            !visited[nextI][nextJ] && m[nextI][nextJ] == 1) {

	            // Mark the current cell as visited
	            visited[i][j] = true;

	            // Explore the next cell and build the path
	            solve(nextI, nextJ, m, n, di, dj, visited,
	                  path + moves.charAt(k), res);

	            // Backtrack — unmark the current cell
	            visited[i][j] = false;
	        }
		}
	}
}
