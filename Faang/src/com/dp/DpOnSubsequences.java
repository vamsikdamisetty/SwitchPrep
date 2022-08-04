package com.dp;

import java.util.Arrays;

public class DpOnSubsequences {

	public static void main(String[] args) {

		int arr[] = { 2, 3, 1, 1 };
		int n = arr.length;
		int k = 4; // target
		int dp[][] = new int[n][k + 1];
		for (int row[] : dp)
			Arrays.fill(row, -1);

		/*
		 * Only recc TC : O(2^n)
		 * 
		 * Memo: Time Complexity: O(N*K)
		 * 
		 * Reason: There are N*K states therefore at max ‘N*K’ new problems will be
		 * solved.
		 * 
		 * Space Complexity: O(N*K) + O(N)
		 * 
		 * Reason: We are using a recursion stack space(O(N)) and a 2D array ( O(N*K)).
		 */
		System.out.println("Subset Sum to target : ");
		System.out.println(subsetSumK(n - 1, k, arr, dp));

		System.out.println(subsetSumKTab(n, k, arr));
		// https://leetcode.com/problems/partition-equal-subset-sum/submissions/
		
		/*
		 * Question:
		 * Partition a set into two subsets such that the difference of subset sums is minimum.
		 * 
		 * Uses the subsetSumKTab concept
		 * We know in that dp[n-1][] contains what all target it can make till K
		 * 
		 *  
		 */
		System.out.println(minSubsetSumDifference(arr, n));
		
		System.out.println("Count of:");
		dp = new int[n][k + 1];
		for (int row[] : dp)
			Arrays.fill(row, -1);


		/*
		 * TC,SC same as subsetSumK
		 * 
		 * Note : if arr[i] can be 0 then we need to handle it differently 
		 */
		System.out.println(countSubsetSumK(n-1, k, arr,dp));
		

		System.out.println(countSubsetSumKTab(n, k, arr));
	}

	static boolean subsetSumK(int n, int k, int arr[], int[][] dp) {

		if (k == 0) {
			return true;
		}

		if (n == 0)
			return arr[0] == k; // if n == 0 k sould be equal to arr[0] else target not reached

		if (dp[n][k] != -1)
			return false; // If visited no solution can be found return false;

		if (arr[n] <= k) {
			if (subsetSumK(n - 1, k - arr[n], arr, dp))
				return true;
			else {
				dp[n][k] = 1;// Marking Visited
			}
		}

		if (subsetSumK(n - 1, k, arr, dp))
			return true;
		else {
			dp[n][k] = 1;
		}
		return false;
	}

	static boolean subsetSumKTab(int n, int k, int arr[]) {

		boolean[][] dp = new boolean[n][k + 1];

		// When there is no target to complete we can consider any index
		for (int i = 0; i < n; i++) {
			dp[i][0] = true;
		}

		// 0th index can only be true when target is equal to the value arr[0] contains
		if (arr[0] <= k) {
			dp[0][arr[0]] = true;
		}

		for (int ind = 1; ind < n; ind++) {
			/*
			 * Here we are trying to find if any index ind contain a subsequence whose sum
			 * can be equal to any of the target <= K
			 * 
			 * Finally for last index we can just find if it has a subsequence whoich can contain sum to K
			 * 
			 * But we could have returned k the moment any of the index's target == k is true
			 */
			for (int target = 1; target <= k; target++) {
				/*For understanding
				 * if (ind == n - 1) {
					target = k;
				}*/
				boolean notTake = dp[ind - 1][target];

				boolean take = false;
				if (target - arr[ind] > 0) {
					take = dp[ind - 1][target - arr[ind]];
				}

				dp[ind][target] = take || notTake;
				/*
				 * For understanding
				 * if(target == k && dp[ind][target]){
				 *  return true; 
				 *  }
				 */
			}
		}

		return dp[n - 1][k];
	}
	
	public static int minSubsetSumDifference(int[] arr, int n) {

		int total = 0;
		//Finding total summ of arr
		for (int i = 0; i < n; i++) {
			total += arr[i];
		}

		//optimization    
		int k = total / 2;

		boolean[][] dp = new boolean[n][k + 1];

		for (int i = 0; i < n; i++) {
			dp[i][0] = true;
		}

		if (arr[0] <= k) {
			dp[0][arr[0]] = true;
		}

		for (int ind = 1; ind < n; ind++) {
			for (int target = 1; target <= k; target++) {

				boolean notTake = dp[ind - 1][target];

				boolean take = false;
				if (arr[ind] <= target) {
					take = dp[ind - 1][target - arr[ind]];
				}

				dp[ind][target] = take || notTake;
			}
		}
		
		/*
		 * Now we know if our arr can make or cannot make target <= total/2
		 * If it can make then it becomes out sum1
		 * sum2 is total - sum1
		 * We just have to find the mini of difference   
		 */
		int mini = total;

		for (int i = 0; i <= k; i++) {
			if (dp[n - 1][i]) {
				int s1 = i;
				int s2 = total - i;

				mini = Integer.min(mini, Math.abs(s1 - s2));
			}
		}
		return mini;
	}
	
	static int countSubsetSumK(int n, int k, int arr[],int[][] dp) {
		
		if(k == 0) {
			return 1;
		}
		
		if(n==0) {
			if(arr[0] == k) {
				return 1;
			}
			return 0;
		}
		
		if(dp[n][k] != -1) {
			return dp[n][k];
		}
		
		int notTake = countSubsetSumK(n-1,k,arr,dp);
		
		int take = 0;
		if(arr[n] <=  k) {
			take = countSubsetSumK(n-1,k-arr[n],arr,dp);
		}
		
		return dp[n][k] = notTake + take;
	}
	
	static int countSubsetSumKTab(int n, int k, int arr[]) {
		
		int[][] dp = new int[n][k+1];
		
		for (int i = 0; i < n; i++) {
			dp[i][0] = 1;
		}
		
		if(arr[0] <= k) {
			dp[0][arr[0]] = 1;
		}
		
		for (int ind = 1; ind < n; ind++) {
			for (int target = 0; target <= k; target++) {
				
				int notTake = dp[ind-1][target];
				
				int take = 0;
				if(arr[ind] <=  target) {
					take = dp[ind-1][target-arr[ind]];
				}
				
				dp[ind][target] = notTake + take;
			}
		}
		return dp[n-1][k];
	}

	
}
