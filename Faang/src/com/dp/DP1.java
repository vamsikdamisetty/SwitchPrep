package com.dp;

import java.util.Arrays;

public class DP1 {
	public static void main(String[] args) {

		// https://takeuforward.org/data-structure/dynamic-programming-introduction/
		int n = 10;
		int dp[] = new int[n + 1];

		Arrays.fill(dp, -1);
		// TC - > O(n) SC -> O(n) dp array + O(n) stack trace
		System.out.println("Fib for " + n + " :" + fib(n, dp));

		// TC - > O(n) SC -> O(n) dp array
		System.out.println("Fib for " + n + " :" + tabularFib(n, dp));

		// space optimzation
		System.out.println("Fib for " + n + " :" + spaceOFib(n));

		// Count Ways To Reach The N-th Stairs  Overlapping subprob
		System.out.println("\nWays to reeach Nth stair " + spaceOfNthStair(6));
		
		//https://www.codingninjas.com/codestudio/problems/frog-jump_3621012
		// TC - > O(n)Overlapping subprob
		int[] heights = {30,10,60,10,60,50}; 
		n = heights.length;
		dp = new int[n];
	    Arrays.fill(dp, -1);
		System.out.println("\nMin energy required for Frog " + forgJump(n-1, heights, dp));

		//https://atcoder.jp/contests/dp/tasks/dp_b
		// TC - > O(n)  Overlapping subprob
		heights = new int[] {40 ,10, 20, 70, 80, 10, 20, 70, 80, 60};
		n = heights.length;
		dp = new int[n];
	    Arrays.fill(dp, -1);
		System.out.println(frogJumpK(n-1, heights, 4, dp));
	}

	static int fib(int n, int[] dp) {

		if (n <= 1)
			return n;

		if (dp[n] != -1)
			return dp[n];

		return dp[n] = fib(n - 1, dp) + fib(n - 2, dp);
	}

	static int tabularFib(int n, int[] dp) {
		dp[0] = 0;
		dp[1] = 1;

		for (int i = 2; i <= n; i++) {
			dp[i] = dp[i - 1] + dp[i - 2];
		}

		return dp[n];
	}

	static int spaceOFib(int n) {

		int prev1 = 1;
		int prev2 = 0;
		int curr = 0;

		for (int i = 2; i <= n; i++) {
			curr = prev2 + prev1;
			prev2 = prev1;
			prev1 = curr;
		}
		return curr;
	}

	static int spaceOfNthStair(int n) {

		if (n == 1)
			return 1;
		int prev1 = 1;
		int prev2 = 1;
		int nthStair = 1;

		for (long i = 2; i <= n; i++) {
			nthStair = prev1 + prev2;
			prev2 = prev1;
			prev1 = nthStair;
		}

		return nthStair;
	}
	
	static int forgJump(int n,int[] heights,int[] dp){
/*Recursion
		if(n == 0) return 0;
        if(dp[n] != -1) return dp[n];
        
        int l = forgJump(n-1,heights,dp) + Math.abs(heights[n] - heights[n-1]);
        
        int r = n==1 ?Integer.MAX_VALUE: (forgJump(n-2,heights,dp) +                               Math.abs(heights[n] - heights[n-2]) );
        
        return dp[n] = Math.min(l,r);
  */      
        /* Tabulation
		dp[0] = 0;
		dp[1] = Math.abs(heights[1] - heights[0]) ;

		for (int i = 2; i <= n; i++) {
			int step1 = dp[i-1] + Math.abs(heights[i] - heights[i-1]) ;
			int step2 = dp[i-2] + Math.abs(heights[i] - heights[i-2]) ;
			dp[i] = Math.min(step1, step2); 
		}
		return dp[n];
		*/
		
		/*Space Opt*/
		
		int prev2 = 0;
		int prev1 = Math.abs(heights[1] - heights[0]) ;
		for (int i = 2; i <= n; i++) {
			int step1 = prev1 + Math.abs(heights[i] - heights[i-1]) ;
			int step2 = prev2 + Math.abs(heights[i] - heights[i-2]) ;
			
			int curr = Math.min(step1, step2);
			prev2 = prev1;
			prev1 = curr;
		}
		return prev1;
    }
	
	static int frogJumpK(int n,int[] heights,int k,int[] dp) {
		/* recursion
		if(n == 0) return 0;
        if(dp[n] != -1) return dp[n];
        
        
        int mini = Integer.MAX_VALUE;
        for(int i=1;i<=k;i++) {
        	if(n-i < 0) break; 
        	int energy = frogJumpK(n-i,heights,k,dp) + Math.abs(heights[n] - heights[n-i]);
        	mini = Math.min(mini, energy);
        }
        return dp[n] = mini;
        */
        
        dp[0] = 0;
        for(int i = 1;i<=n;i++) {
        	int miniEnergy = Integer.MAX_VALUE;
            for(int j=1;j<=k;j++) {
            	if(i-j < 0) break; 
            	int energy = dp[i-j] + Math.abs(heights[i] - heights[i-j]);
            	miniEnergy = Math.min(miniEnergy,energy);
            }
            dp[i] = miniEnergy;
        }
        return dp[n];
        
	}
}
