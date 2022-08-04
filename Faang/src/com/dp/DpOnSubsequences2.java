package com.dp;

import java.util.Arrays;

public class DpOnSubsequences2 {
	public static void main(String[] args) {
		System.out.println((int)Math.pow(10, 7));
		//0/1 Knapsack
		int[] weight = {1,2,4,5};
		int[] value= {50,40,80,60};
		int n = 4;
		int maxWeight = 5;
		int[][] dp = new int[n][maxWeight+1];
	       
		for(int row[]:dp){
	            Arrays.fill(row,-1);
	        } 
	    
		/*
		 * Only Rec:
		 * TC : O(2^n)
		 * SC : O(n)
		 * 
		 * Memo:
		 * TC : O(n*maxWeight)
		 * SC : O(n*k) + O(n)
		 * 
		 * Tab:
		 * TC : O(n*maxWeight)
		 * SC : O(n*k) 
		 */
		System.out.println("Max Value theif can Attain : ");
		System.out.println(fillBag(weight, value, n-1, maxWeight, dp));
		
		System.out.println(fillBagTab(weight, value, n, maxWeight));
		
		
		/*
		 * Min Coins
		 * Only Recursion
		 * TC: Exponential, we cannot say 2^n
		 * SC: O(target)
		 */
		int[] coins = {1,2,5,6};
		int amount = 20;
		System.out.println("Min Coins");
		System.out.println(coinChange(coins, amount));
		/*
		 * Memoization
		 * TC : O(N*amount)
		 * SC : O(N*amount) + O(T)
		 */
		System.out.println(coinChangeMemo(coins, amount));
		/*
		 * Tabulation
		 * TC : O(N*amount)
		 * SC : O(N*amount)
		 */
		System.out.println(coinChangeTab(coins, amount));
	}

	static int fillBag(int[] weight, int[] value, int n, int maxWeight, int[][] dp) {

		//even  if we dont mention this case code will work
		if (maxWeight == 0) {
			return 0;
		}
		if (n == 0) {
			if (weight[0] <= maxWeight) {
				return value[0];
			} else {
				return 0;
			}
		}
		if (dp[n][maxWeight] != -1)
			return dp[n][maxWeight];

		int notTake = fillBag(weight, value, n - 1, maxWeight, dp);

		int take = Integer.MIN_VALUE;
		if (weight[n] <= maxWeight) {
			take = value[n] + fillBag(weight, value, n - 1, maxWeight - weight[n], dp);
		}

		return dp[n][maxWeight] = Math.max(take, notTake);
	}
	
	static int fillBagTab(int[] weight, int[] value, int n, int maxWeight) {

		int[][] dp = new int[n][maxWeight+1];
	    
		//not required as anyway it initializes to 0
		for (int i = 0; i < n; i++) {
			dp[i][0] = 0;
		}
		
		//if the weightLeft in the bag is >= itemweight we can take it  
		for(int i = weight[0];i <= maxWeight;i++) {
			dp[0][i] = value[0];
		}
		
		for (int ind = 1; ind < n; ind++) {
			for (int targetWeight = 1; targetWeight <= maxWeight; targetWeight++) {
				int notTake = dp[ind-1][targetWeight];

				int take = Integer.MIN_VALUE;
				if (weight[ind] <= targetWeight) {
					take = value[ind] + dp[ind-1][targetWeight - weight[ind]];
				}

				dp[ind][targetWeight] = Math.max(take, notTake);
			}
		}

		 return dp[n-1][maxWeight];
		 /* space optimization
		          int[] dp = new int[maxWeight+1];
         
        for(int i = weight[0];i <= maxWeight;i++) {
            dp[i] = value[0];
        }
        
        for (int ind = 1; ind < n; ind++) {
            int[] temp = new int[maxWeight+1];
            for (int targetWeight = 1; targetWeight <= maxWeight; targetWeight++) {
                int notTake = dp[targetWeight];

                int take = Integer.MIN_VALUE;
                if (weight[ind] <= targetWeight) {
                    take = value[ind] + dp[targetWeight - weight[ind]];
                }

                temp[targetWeight] = Math.max(take, notTake);
            }
            dp = temp;
        }
        return dp[maxWeight];
        
		  */
	}
	
    public static int coinChange(int[] coins, int amount) {
        
        int n = coins.length;
        int mCoins = minCoins(coins,n-1,amount);
        return (mCoins >= (int)Math.pow(10,9)) ? -1 : mCoins;
    }
    
    static int minCoins(int[] coins,int n, int amount){
        
        if(n == 0){
            if(amount % coins[0] == 0) return amount/coins[0];
            
            return (int)Math.pow(10,9);
        }
        
        int notTake = 0 + minCoins(coins,n-1,amount);
        
        int take = Integer.MAX_VALUE;
        if(coins[n] <= amount){
            take = 1 + minCoins(coins,n,amount-coins[n]);
        }
        return Math.min(take,notTake);
    }
    
    public static int coinChangeMemo(int[] coins, int amount) {
        
        int n = coins.length;
        int[][] dp = new int[n][amount+1];
        
        for(int row[]:dp){
	            Arrays.fill(row,-1);
	    } 
	    
        int mCoins = minCoins(coins,n-1,amount,dp);
        return (mCoins >= (int)Math.pow(10,9)) ? -1 : mCoins;
    }
    
    static int minCoins(int[] coins,int n, int amount,int[][] dp){
        
    	//Not mandatory but good to have
        if(amount == 0){
            return 0;
        }
        if(n == 0){
            if(amount % coins[0] == 0) return amount/coins[0];
            //If we return Int_MAX -> Overflow might happen
            return (int)Math.pow(10,9);
        }
        
        if(dp[n][amount] != -1) return dp[n][amount];
        
        int notTake = 0 + minCoins(coins,n-1,amount,dp);
        
        int take = Integer.MAX_VALUE;
        if(coins[n] <= amount){
        	//not decreasing index because of infinite supply
            take = 1 + minCoins(coins,n,amount-coins[n],dp);
        }
        return dp[n][amount] = Math.min(take,notTake);
    }
    
	public static int coinChangeTab(int[] coins, int amount) {

		int n = coins.length;
		int[][] dp = new int[n][amount + 1];

		for (int i = 0; i <= amount; i++) {
			if(i % coins[0] == 0) {
				dp[0][i] = i/coins[0];
			}else {
				dp[0][i] = (int) Math.pow(10, 9);
			}
		}
		
		for (int ind = 1; ind < n; ind++) {
			for (int amt = 0; amt <= amount; amt++) {
				
				int notTake = 0 + dp[ind-1][amt];

				int take = Integer.MAX_VALUE;
				if (coins[ind] <= amt) {
					take = 1 + dp[ind][amt-coins[ind]];
				}
				dp[ind][amt] = Math.min(take, notTake);
			}
		}

		int mCoins = dp[n-1][amount];
		return (mCoins >= (int) Math.pow(10, 9)) ? -1 : mCoins;
	}
}
