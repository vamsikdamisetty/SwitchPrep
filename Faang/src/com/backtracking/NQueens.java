package com.backtracking;

import java.util.ArrayList;
import java.util.List;

public class NQueens {

	public static void main(String[] args) {
        /*
         * Time Complexity:
         * Time Complexity: Exponential in nature since we are trying out all ways, to be precise its O(N! * N).
         * N! because for 4*4 if we place one Q in a column (4 ways) 
         * then only 3 ways then only 2 ways then 1 way for last Q
         * 
         * Space Complexity: O( N^2 )
         */
		NQueens nq = new NQueens();
		int N = 5;
        List < List < String >> queen = nq.solveNQueens(N);
        int i = 1;
        for (List < String > it: queen) {
            System.out.println("Arrangement " + i);
            for (String s: it) {
                System.out.println(s);
            }
            System.out.println();
            i += 1;
        }
	}
	
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> res = new ArrayList<>();
        
        char[][] board = new char[n][n];
        
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
             board[i][j] = '.';   
            }
        }
        
        // Start placing queens from the first column (col = 0)
        placeQueens(n,0,board,res);
        
        return res;
    }
    
    /**
     * Recursive method to try placing queens column by column.
     *
     * @param n     The size of the board (n x n)
     * @param col   The current column where we are placing a queen
     * @param board The current board configuration
     * @param res   The result list storing valid board arrangements
     */
    void placeQueens(int n,int col,char[][] board,List<List<String>> res){
        
    	// Base case: all queens are placed successfully
        if(col == n){
            addSolution(n,board,res);
            return;
        }
        
        // Try placing a queen in every row of the current column
        for(int row=0;row<n;row++){
            if(isSafeForQ(board,row,col)){
            	// Place the queen
                board[row][col] = 'Q';

                // Recur to place the next queen in the next column
                placeQueens(n, col + 1, board, res);

                // Backtrack: remove the queen and try next possibility
                board[row][col] = '.';
            }
        }
    }
    
    /**
     * Checks if placing a queen at board[row][col] is safe.
     * If we can make sure below 3 things we are safe to place Queen
     * 1. Upper-left diagonal
     * 2. left
     * 3. Lower-left diagonal
     *
     * @param board The current board state
     * @param row   The row index
     * @param col   The column index
     * @return      True if safe, otherwise false
     */
    boolean isSafeForQ(char[][] board, int row,int col){
        
        int tRow = row-1;
        int tCol = col-1;
        
        // Check upper-left diagonal
        while(tRow>=0 && tCol >= 0){
            if(board[tRow][tCol] == 'Q') return false;
            tRow--;
            tCol--;
        }
        
        tRow = row;
        tCol = col-1;
        
        // Check left side (same row)
        while(tCol >= 0){
            if(board[tRow][tCol] == 'Q') return false;
            tCol--;
        }
        
        tRow = row+1;
        tCol = col-1;
        
        // Check lower-left diagonal
        while(tRow < board.length && tCol >= 0){
            if(board[tRow][tCol] == 'Q') return false;
            tRow++;
            tCol--;
        }
        
        return true;
        
    }
    
    void addSolution(int n,char[][] board,List<List<String>> res){
        List<String> sol = new ArrayList<String>();
        
         for(int i=0;i<n;i++){
             String s="";
            for(int j=0;j<n;j++){
             s += board[i][j];
            }
             sol.add(s);
        }
        
        res.add(sol);
    }
}
