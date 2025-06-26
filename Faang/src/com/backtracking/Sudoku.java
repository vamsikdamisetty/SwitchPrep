package com.backtracking;

public class Sudoku {

	/*
	 * Remember we are finding solution only once that's why no exponential Time
	 * Complexity: O(9(n ^ 2)), in the worst case, for each cell in the n2 board, we
	 * have 9 possible numbers.
	 * 
	 * Space Complexity: O(1), since we are refilling the given board itself, there
	 * is no extra space required, so constant space complexity.
	 */
	public static void main(String[] args) {
		char[][] board = { { '9', '5', '7', '.', '1', '3', '.', '8', '4' },
				{ '4', '8', '3', '.', '5', '7', '1', '.', '6' }, { '.', '1', '2', '.', '4', '9', '5', '3', '7' },
				{ '1', '7', '.', '3', '.', '4', '9', '.', '2' }, { '5', '.', '4', '9', '7', '.', '3', '6', '.' },
				{ '3', '.', '9', '5', '.', '8', '7', '.', '1' }, { '8', '4', '5', '7', '9', '.', '6', '1', '3' },
				{ '.', '9', '1', '.', '3', '6', '.', '7', '5' }, { '7', '.', '6', '1', '8', '5', '4', '.', '9' } };

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(board[i][j] + " ");
			System.out.println();
		}

		Sudoku sudoku = new Sudoku();
		sudoku.solve(board);

		System.out.println("\n\n Solved");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(board[i][j] + " ");
			System.out.println();
		}
	}

	/**
	 * Solves the given Sudoku board using backtracking.
	 *
	 * @param board A 9x9 char matrix representing the Sudoku puzzle. Empty cells
	 *              are denoted by '.'.
	 * @return True if the board can be solved, false otherwise.
	 */
	boolean solve(char[][] board) {

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {

				// If the cell is empty, try filling it
				if (board[i][j] == '.') {

					// Try placing digits from '1' to '9'
					for (char c = '1'; c <= '9'; c++) {

						// Check if placing 'c' is valid
						if (isValid(board, c, i, j)) {
							board[i][j] = c; // Place the digit

							// Recurse: if it leads to a solution, return true
							if (solve(board)) {
								return true;
							} else {
								board[i][j] = '.'; // Backtrack
							}
						}
					}

					// No valid digit found for this cell → backtrack
					return false;
				}
			}
		}

		// All cells are filled correctly
		return true;
	}

	/**
	 * Checks whether placing character 'c' at board[row][col] is valid.
	 *
	 * @param board The Sudoku board.
	 * @param c     The digit being placed.
	 * @param row   The row index.
	 * @param col   The column index.
	 * @return True if it's valid to place 'c' in that position.
	 */
	boolean isValid(char[][] board, char c, int row, int col) {

		for (int i = 0; i < 9; i++) {
			// Check the current column
			if (board[i][col] == c) {
				return false;
			}

			// Check the current row
			if (board[row][i] == c) {
				return false;
			}

			// Check the 3x3 subgrid
			/*
			 * 3 * (row / 3) -> Gives starting row of the subgrid
			 * i/3 gives which row in the subgrid 
			 * ex: i=5  5/3 -> 1  for subgrid starting from 3 row current row would be 4
			 *   
			 * 3 * (col / 3) -> Gives starting column of the subgrid
			 * i%3 give which col in the subgrid
			 * ex: i=5  5%3 -> 2  for subgrid starting from 3 col current row would be 5
			 */
			int boxRow = 3 * (row / 3) + i / 3;
			int boxCol = 3 * (col / 3) + i % 3;
			if (board[boxRow][boxCol] == c) {
				return false;
			}
		}

		return true;
	}

}
