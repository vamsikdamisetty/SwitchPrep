package com.backtracking;

import java.util.ArrayList;

public class RatInAMaze {

	/*
	 * Time Complexity: O(2^(n^2)): The recursion can run upper bound 2^(n^2) times. 2) 
	 * Space Complexity: O(n^2): Output matrix is required so an extra space of size n*n is needed.
	 * Also Auxilary space for Recursion could be O(n^2) 
	 */
	  public static void main(String[] args) {

		    int n = 4;
		    int[][] a = {{1,0,0,0},{1,1,0,1},{1,1,0,0},{0,1,1,1}};

		    ArrayList < String > res = RatInAMaze.findPath(a, n);
		    if (res.size() > 0) {
		      for (int i = 0; i < res.size(); i++)
		        System.out.print(res.get(i) + " ");
		      System.out.println();
		    } else {
		      System.out.println(-1);
		    }
		  }
    public static ArrayList<String> findPath(int[][] m, int n) {
        ArrayList<String> res = new ArrayList<>();
        boolean[][] visited = new boolean[n][n];
        
        int[] di = {1,0,0,-1};
        int[] dj = {0,-1,1,0};
        
        if(m[0][0] == 1) solve(0,0,m,n,di,dj,visited,"",res);
        
        return res;
    }
    
    static void solve(int i,int j,int[][] m, int n,int[] di,int[] dj,boolean[][] visited,String path,ArrayList<String> res){
        
        if(i == n-1 && j == n-1){
            res.add(path);
            return;
        }
        
        String moves = "DLRU";
        for(int k=0;k<4;k++){
            int nextI = i + di[k];
            int nextJ = j + dj[k];
            
            if( nextI >= 0 && nextI < n && nextJ >= 0 && nextJ < n && !visited[nextI][nextJ] && m[nextI][nextJ] == 1){
                visited[i][j] = true;
                solve(nextI,nextJ,m,n,di,dj,visited,path + moves.charAt(k),res);
                visited[i][j] = false;
            }
        }
    }
}
