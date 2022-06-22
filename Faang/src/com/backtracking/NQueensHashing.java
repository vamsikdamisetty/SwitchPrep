package com.backtracking;

import java.util.ArrayList;
import java.util.List;

public class NQueensHashing {

	/*
	 * Time Complexity: Exponential in nature since we are trying out all ways, to be precise its O(N! * N).
	 * 
	 * Space Complexity: O(N)
	 * 
	 * By this Hashing we are saving O(n+n+n) from isSafe method by using Hashing technique
	 */
	public static void main(String[] args) {
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
        
        boolean[] left = new boolean[n];
        boolean[] upperDiagonal = new boolean[2*n - 1];
        boolean[] lowerDiagonal = new boolean[2*n - 1];
        
        placeQueens(n,0,board,left,upperDiagonal,lowerDiagonal,res);
        
        return res;
    }
    
    void placeQueens(int n,int col,char[][] board,boolean[] left,boolean[] upperDiagonal,boolean[] lowerDiagonal,List<List<String>> res){
        
        if(col == n){
            addSolution(n,board,res);
            return;
        }
        
        for(int row=0;row<n;row++){
            if(!left[row] && !upperDiagonal[n-1 + col-row]  && !lowerDiagonal[row + col]){
                left[row] = true;
                upperDiagonal[n-1 + col-row] = true;
                lowerDiagonal[row + col] = true;
                board[row][col] = 'Q';
                placeQueens(n,col+1,board,left,upperDiagonal,lowerDiagonal,res);
                board[row][col] = '.';
                left[row] = false;
                upperDiagonal[n-1 + col-row] = false;
                lowerDiagonal[row + col] = false;
            }
        }
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
