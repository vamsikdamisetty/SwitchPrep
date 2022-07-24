package com.dp;

import java.util.Arrays;

public class NinjaTraining {
	
	public static void main(String[] args) {
		
		int[][] points = {{2,1,3},{3,4,6},{10,1,6},{8,3,7}};
		int n = points.length;
		int[][] dp = new int[n][4];
        for (int[] row: dp)
            Arrays.fill(row, -1);
        //Recursion 
        System.out.println(maxMerit(points,n-1,3,dp)); 
        

        for (int[] row: dp)
            Arrays.fill(row, -1);
        //O(n*4) * 3 
        //Sc :: O(n) + O(n*4)
        System.out.println(maxMeritMemo(points, n-1, 3, dp));
        
        //O(n*4) * 3 
        //Sc ::  O(n*4)
        System.out.println(maxMeritTab(points, n));
        
        //O(n*4) * 3 
        //Sc ::  O(4)
        System.out.println(maxMeritTabSO(points, n));
	}
	
    static int maxMerit(int points[][],int day,int lastActitvity,int[][] dp){
        
        if(day == 0){
            int maxi = 0;
            for(int task=0;task<3;task++){
                if(task != lastActitvity){
                    maxi = Math.max(points[0][task],maxi);
                }
            }
            return maxi;
        }
        
        int maxi = 0;
        for(int task=0;task<3;task++){
                if(task != lastActitvity){
                    int merit = points[day][task] 
                        + maxMerit(points,day-1,task,dp);
                    maxi = Math.max(merit,maxi);
                }
         }
        return maxi;
    }  
    
    static int maxMeritMemo(int points[][],int day,int lastActitvity,int[][] dp){
        
        if(day == 0){
            int maxi = 0;
            for(int task=0;task<3;task++){
                if(task != lastActitvity){
                    maxi = Math.max(points[0][task],maxi);
                }
            }
            return maxi;
        }
        if(dp[day][lastActitvity] != -1){
            return dp[day][lastActitvity];
        } 
        
        int maxi = 0;
        for(int task=0;task<3;task++){
                if(task != lastActitvity){
                    int merit = points[day][task] 
                        + maxMerit(points,day-1,task,dp);
                    maxi = Math.max(merit,maxi);
                }
         }
        return dp[day][lastActitvity] = maxi;
    }  
    
    static int maxMeritTab(int points[][],int n){
        
    	int[][] dp = new int[n][3];
    	dp[0][0]  = Math.max(points[0][1], points[0][2]);
    	dp[0][1]  = Math.max(points[0][0], points[0][2]);
    	dp[0][2]  = Math.max(points[0][0], points[0][1]);
    	
    	
    	
    	for(int day=1;day<points.length;day++) {
    		 for(int lastActitvity=0;lastActitvity<3;lastActitvity++){
    			 dp[day][lastActitvity] = 0;
    			 int maxi = 0;
    			  for(int task=0;task<3;task++){
    	                if(task != lastActitvity){
    	                    int merit = points[day][task] 
    	                        + dp[day-1][task];
    	                    maxi = Math.max(merit,maxi);
    	                }
    	         }
    			  dp[day][lastActitvity] = maxi; 
    		 }
    	}
      return Math.max((Math.max(dp[n-1][0], dp[n-1][1])),dp[n-1][2]);
    }  

    static int maxMeritTabSO(int points[][],int n){
        
    	int[] dp = new int[3];
    	dp[0]  = Math.max(points[0][1], points[0][2]);
    	dp[1]  = Math.max(points[0][0], points[0][2]);
    	dp[2]  = Math.max(points[0][0], points[0][1]);
    	
    	
    	
    	for(int day=1;day<points.length;day++) {
    		int[] temp = new int[3];
    		 for(int lastActitvity=0;lastActitvity<3;lastActitvity++){
    			 
    			 temp[lastActitvity] = 0;
    			  for(int task=0;task<3;task++){
    	                if(task != lastActitvity){
    	                    int merit = points[day][task] 
    	                        + dp[task];
    	                    temp[lastActitvity] = Math.max(merit,temp[lastActitvity]);
    	                }
    	         }
    		 }
    		 dp = temp;
    	}
      return Math.max((Math.max(dp[0], dp[1])),dp[2]);
    }  


}
