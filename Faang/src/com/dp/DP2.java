package com.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DP2 {
	
	public static void main(String[] args) {
		
		//https://www.codingninjas.com/codestudio/problems/maximum-sum-of-non-adjacent-elements_843261

		List<Integer> nums = Arrays.asList(5,8,7,2);
        int[] dp = new int[nums.size()]; 
        Arrays.fill(dp,-1);
        System.out.println("Maximum sum of non-adjacent elements:");
        //TC : O(n)   Sc : O(n) + O(n)
        System.out.println(maxSum(nums,nums.size()-1,dp));
        //TC : O(n)   Sc : O(n)
        System.out.println(maxSumTab(nums, nums.size(), dp));
        
        
        //https://www.codingninjas.com/codestudio/problems/house-robber_839733
        System.out.println("House Robber II");

        System.out.println(houseRobber(new int[] {6 ,5 ,4 ,3 ,2 ,1, 7}));
	}
	
    static int maxSum(List<Integer> nums,int n,int[] dp){
        if(n == 0) return nums.get(0);
        if(n < 0) return 0;
        if(dp[n] != -1) return dp[n];
        
        int pick = maxSum(nums,n-2,dp) + nums.get(n);
        
        int notPick = maxSum(nums,n-1,dp);
        
        return dp[n] = Math.max(pick,notPick);
        
    }
    
    static int maxSumTab(List<Integer> nums,int n,int[] dp){
    	dp[0] = nums.get(0);
    	
    	for(int i=1;i<n;i++) {
    		
    		int pick = nums.get(i) ;
    		if(i > 1) pick += dp[i-2];
    		
    		int notPick = dp[i-1];
    		
    		dp[i] = Math.max(pick, notPick);
    	}
    	
    	return dp[n-1];
    	
    	
    	/* space optimied to O(1)
    	 *         int prev1 = nums.get(0);
        int prev2 = 0;
        
        for(int i=1;i<n;i++) {
            
            int pick = nums.get(i) ;
            if(i > 1) pick += prev2;
            
            int notPick = prev1;
            
            int cur = Math.max(pick, notPick);
            
            prev2 = prev1;
            prev1 = cur;
        }
        
        return prev1;
    	 */
    }
    
    
	public static long houseRobber(int[] valueInHouse) {
		
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
          
        int n = valueInHouse.length;
        if(n==1) return valueInHouse[0];
        for(int i=0;i<n;i++){
            if(i!=0) l1.add(valueInHouse[i]);
            if(i!=n-1) l2.add(valueInHouse[i]);
        }
        
        //intuition is since its a circular one, Keppiign the maxSum logic same 
        //Revmove 1st element and remove last element one which gives max money is ans	
        return Math.max(maxSumTabSpaceOpt(l1,n-1),maxSumTabSpaceOpt(l2,n-1));
	}	
    
     static long maxSumTabSpaceOpt(List<Integer> nums,int n){       
        long prev1 = nums.get(0);
        long prev2 = 0;
        
        for(int i=1;i<n;i++) {
            
            long pick = nums.get(i) ;
            if(i > 1) pick += prev2;
            
            long notPick = prev1;
            
            long cur = Math.max(pick, notPick);
            
            prev2 = prev1;
            prev1 = cur;
        }
        
        return prev1;
         
    }
}
