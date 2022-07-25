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
        
        //https://leetcode.com/problems/triangle/
        
        int[][] triangle = {{1},{2,3},{3,6,7},{8,9,6,10}};
        n=4;
        dp=new int[n][n];
        for(int row[]: dp)
        Arrays.fill(row,-1);
        System.out.println("Min path in Triangle");
        /*
         * Memo
         * Time Complexity: O(N*N)

		Reason: At max, there will be (half of, due to triangle) N*N calls of recursion.

		Space Complexity: O(N) + O(N*N)

		Reason: We are using a recursion stack space: O((N), where N is the path length and an external DP Array of size ‘N*N’
         */
        System.out.println(minPathInTriangle(triangle, n, 0, 0, dp));
        
        /*
         * Time Complexity: O(N*N)
         * space : O(N*N)  
         * can do space opt to O(n)
         */
        System.out.println(minPathInTriangleTab(triangle, n, dp));
        
        //https://leetcode.com/problems/minimum-falling-path-sum/
        //TC: O(N*M)
        //SC : O(N*M) + O(N)
        System.out.println("Minimum/Maximum Falling Path Sum:");
        int[][] matrix= {{2,1,3},{6,5,4},{7,8,9}};
        System.out.println(minFallingPathSum(matrix));
        
        //TC: O(N*M)
        //SC : O(N*M) 
        System.out.println(minimumFallingPathSumTab(matrix,matrix.length));
        
        //https://www.codingninjas.com/codestudio/problems/ninja-and-his-friends_3125885
        System.out.println("Max Chocolates pickup 3d DP:");
        int[][] grid2 = {{2,3,1,2},{3,4,2,2},{5,6,3,5}};
        m = grid.length;
        n= grid[0].length;
        int[][][] dp2 = new int[m][n][n];
        for (int row1[][]: dp2) {
          for (int row2[]: row1) {
              Arrays.fill(row2, -1);
          }
        }
        /*
         * only recursion
         * TC: O(3^n *3^n)
         * SC: O(n);
         * 
         * Memo:
         * TC: O(m*n*n)*9
         * sc: O(n) + O(m*n*n)
         */
        System.out.println(maxChocolates(m, n, grid2, 0, 0, n-1, dp2));
        
        /*
         * TaB
         * TC: O(m*n*n)*9
         * sc: O(m*n*n)
         */
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

    static int minPathInTriangle(int[][] triangle,int n,int i,int j,int[][] dp){
        
        if(i == n-1)
        {
            return triangle[n-1][j];
        }
        
        if(dp[i][j] != -1) return dp[i][j];
        
        int down = triangle[i][j] + minPathInTriangle(triangle,n,i+1,j,dp);
        
        int diag = triangle[i][j] + minPathInTriangle(triangle,n,i+1,j+1,dp);
        
        return dp[i][j] = Math.min(down,diag);
    }
    
    static int minPathInTriangleTab(int[][] triangle,int n,int[][] dp){
        
    	for(int j=0;j<n;j++) {
    		dp[n-1][j] = triangle[n-1][j];
    	}
    	
		for (int i = n - 2; i >= 0; i--) {
			
			for (int j = 0; j <= i; j++) {
				int down = triangle[i][j] + dp[i+1][j];

				int diag = triangle[i][j] + dp[i+1][j+1];
				dp[i][j] = Math.min(down, diag);
			}
			
		}
    	
		return dp[0][0];
    }
    
    public static int minFallingPathSum(int[][] matrix) {
        int n = matrix.length;
        
        int[][] dp=new int[n][n];
        for(int row[]: dp)
        Arrays.fill(row,-1);
        
        int minFPS = Integer.MAX_VALUE;
        for(int j = 0;j<n;j++){
            minFPS = Math.min(minFPS,minimumFallingPathSum(matrix,n,0,j,dp));
        }
        return minFPS;
    }
    
    static int minimumFallingPathSum(int[][] matrix,int n,int i,int j,int[][] dp){
        
        if(i == n-1){
            return matrix[n-1][j];
        }
        
        if(dp[i][j] != -1) return dp[i][j];
        
        int leftDiag = Integer.MAX_VALUE;
        
        if(j>0) {
        	leftDiag = matrix[i][j] + minimumFallingPathSum(matrix,n,i+1,j-1,dp);
        }
        
        int down = matrix[i][j] + minimumFallingPathSum(matrix,n,i+1,j,dp);
        
        int rightDiag = Integer.MAX_VALUE;
        if(j < n-1 ) {
        	rightDiag = matrix[i][j] + minimumFallingPathSum(matrix,n,i+1,j+1,dp);
        }
        
        return dp[i][j] = Math.min(leftDiag,Math.min(down,rightDiag));
    }
    
   static int minimumFallingPathSumTab(int[][] matrix,int n){
        
	   	int[][] dp = new int[n][n];
	   	
	   	for(int j = 0;j<n;j++) {
	   		dp[0][j] = matrix[0][j];
	   	}
	   	for(int i= 1;i<n;i++) {
	   		for(int j=0;j<n;j++) {
	   		 int leftDiag = Integer.MAX_VALUE;
	         
	         if(j>0) {
	         	leftDiag = matrix[i][j] + dp[i-1][j-1];
	         }
	         
	         int up = matrix[i][j] + dp[i-1][j];
	         
	         int rightDiag = Integer.MAX_VALUE;
	         if(j < n-1 ) {
	         	rightDiag = matrix[i][j] + dp[i-1][j+1];
	         }
	         
	         dp[i][j] = Math.min(leftDiag,Math.min(up,rightDiag));
	   		}
	   	}
        
	   	int mini = Integer.MAX_VALUE;
	   	for(int j = 0;j<n;j++) {
	   		mini = Math.min(mini, dp[n-1][j]);
	   	}
	   	
	   	return mini;
    }

	static int maxChocolates(int m, int n, int[][] grid, int i, int j1, int j2, int[][][] dp) {

		if (j1 < 0 || j1 >= n || j2 < 0 || j2 >= n) {
			return (int) (Math.pow(-10, 9));
			//Not Int_min because later if something is added it becomes positive
			//and below maxi will be effected
		}

		if (i == m - 1) {
			if (j1 == j2)
				return grid[i][j1];
			else {
				return grid[i][j1] + grid[i][j2];
			}
		}
		if (dp[i][j1][j2] != -1)
			return dp[i][j1][j2];

		int maxi = 0;
		for (int dj1 = -1; dj1 < 2; dj1++) {
			for (int dj2 = -1; dj2 < 2; dj2++) {
				if (j1 == j2) {
					maxi = Math.max(maxi, grid[i][j1] + maxChocolates(m, n, grid, i + 1, j1 + dj1, j2 + dj2, dp));
				} else {
					maxi = Math.max(maxi,
							grid[i][j1] + grid[i][j2] + maxChocolates(m, n, grid, i + 1, j1 + dj1, j2 + dj2, dp));
				}
			}
		}
		return dp[i][j1][j2] = maxi;

	}
}
