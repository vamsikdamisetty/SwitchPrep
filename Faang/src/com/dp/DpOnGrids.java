package com.dp;

import java.util.Arrays;

public class DpOnGrids {
	public static void main(String[] args) {

		int m = 4, n = 3;
		int[][] dp = new int[m][n];
		for (int[] row : dp) {
			Arrays.fill(row, -1);
		}
		
		/*
		 * TC : O(m*n) Without dp -> O(2^m*n)
		 * SC : o((n-1)+(m-1)) + O(m*n) 
		 */
		System.out.printf("No of Unique Paths for %d X %d matrix is :\n", m, n);
		System.out.println(countUniquePaths(m - 1, n - 1, dp));
		/*
		 * TC : O(m*n)
		 * SC : O(m*n) 
		 */
		System.out.println(countUniquePathsTab(m, n));
		
		//https://leetcode.com/problems/unique-paths-ii
		int[][] obstacleGrid = {{0,0,0},{0,1,0},{0,0,0}};
        m = obstacleGrid.length;
        n = obstacleGrid[0].length;
    
        
        dp = new int[m][n];
		for (int[] row : dp) {
			Arrays.fill(row, -1);
		}
        System.out.println("Unique paths with obstacles:");
        System.out.println(countUniquePaths2(m-1, n-1, obstacleGrid, dp));
        
        
        int[][] grid = {{1,3,1},{1,5,1},{4,2,1}};
        m = grid.length;
        n = grid[0].length;
        
        dp = new int[m][n];
		for (int[] row : dp) {
			Arrays.fill(row, -1);
		}
        
		System.out.println("Minimum Path Sum:");
        System.out.println(minimumPathSum(m-1,n-1,grid,dp));
	}

	static int countUniquePaths(int m, int n, int[][] dp) {

		if (m == 0 && n == 0)
			return 1;

		if (m < 0 || n < 0)
			return 0;

		if (dp[m][n] != -1)
			return dp[m][n];

		int l = countUniquePaths(m - 1, n, dp);
		int r = countUniquePaths(m, n - 1, dp);

		return dp[m][n] = l + r;
	}
	
	static int countUniquePathsTab(int m,int n) {
		
		int[][] dp = new int[m][n];
		
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				
				if(i == 0 && j == 0) {
					dp[0][0] = 1;
				}else {
					int left=0,up=0;
					if(i > 0) left = dp[i-1][j]; 
					if(j > 0) up = dp[i][j-1];
					
					dp[i][j] = up+left;
				}
				
			}
		}
		return dp[m-1][n-1];
	}
	
    static int countUniquePaths2(int m, int n,int[][] obstacleGrid, int[][] dp) {

        if ((m < 0 || n < 0) || obstacleGrid[m][n] == 1)
			return 0;
        
        if (m == 0 && n == 0)
			return 1;
        
		if (dp[m][n] != -1)
			return dp[m][n];

        
		int left = countUniquePaths2(m - 1, n,obstacleGrid, dp);
        int up = countUniquePaths2(m, n - 1,obstacleGrid, dp);

		return dp[m][n] = left + up;
		
		/*
		for(int i=0; i<n ;i++){
      for(int j=0; j<m; j++){
          
          //base conditions
          if(i>0 && j>0 && maze[i][j]==-1){
            dp[i][j]=0;
            continue;
          }
          if(i==0 && j==0){
              dp[i][j]=1;
              continue;
          }
          
          int up=0;
          int left = 0;
          
          if(i>0) 
            up = dp[i-1][j];
          if(j>0)
            left = dp[i][j-1];
            
          dp[i][j] = up+left;
      }
		 */
	}
    
    
    static int minimumPathSum(int m, int n,int[][] grid, int[][] dp) {

        if (m == 0 && n == 0)
			return grid[0][0];
        
        if (m < 0 || n < 0) 
			return Integer.MAX_VALUE;
        
		if (dp[m][n] != -1)
			return dp[m][n];

        
		int left =  minimumPathSum(m - 1, n,grid, dp);
        int up = minimumPathSum(m, n - 1,grid, dp);

		return dp[m][n] = grid[m][n] + Math.min(left,up);
		/* Tabulation
		int dp[][]=new int[n][m];
	    
	    for(int i=0; i<n ; i++){
	        for(int j=0; j<m; j++){
	            if(i==0 && j==0) dp[i][j] = matrix[i][j];
	            else{
	                
	                int up = matrix[i][j];
	                if(i>0) up += dp[i-1][j];
	                else up += (int)Math.pow(10,9);
	                
	                int left = matrix[i][j];
	                if(j>0) left+=dp[i][j-1];
	                else left += (int)Math.pow(10,9);
	                
	                dp[i][j] = Math.min(up,left);
	            }
	        }
	    }
	    
	    return dp[n-1][m-1];*/
	}

}
