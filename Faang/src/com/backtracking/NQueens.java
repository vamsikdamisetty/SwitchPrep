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
		int N = 4;
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
        
        placeQueens(n,0,board,res);
        
        return res;
    }
    
    void placeQueens(int n,int col,char[][] board,List<List<String>> res){
        
        if(col == n){
            addSolution(n,board,res);
            return;
        }
        
        for(int row=0;row<n;row++){
            if(isSafeForQ(board,row,col)){
                board[row][col] = 'Q';
                placeQueens(n,col+1,board,res);
                board[row][col] = '.';
            }
        }
    }
    
    boolean isSafeForQ(char[][] board, int row,int col){
        
        int tRow = row;
        int tCol = col;
        
        while(tRow>=0 && tCol >= 0){
            if(board[tRow][tCol] == 'Q') return false;
            tRow--;
            tCol--;
        }
        
        tRow = row;
        tCol = col;
        
        
        while(tCol >= 0){
            if(board[tRow][tCol] == 'Q') return false;
            tCol--;
        }
        
        tCol = col;
        
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
