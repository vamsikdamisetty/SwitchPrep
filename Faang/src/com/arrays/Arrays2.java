package com.arrays;

public class Arrays2 {

    public void setZeroes(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        
        boolean[] dRow = new boolean[m];
        boolean[] dCol = new boolean[n];
        
        for(int i =0;i<m;i++){
            for(int j=0;j<n;j++){
                if(matrix[i][j] == 0){
                    dRow[i] = true;
                    dCol[j] = true;
                }
            }
        }
        
        for(int i =0;i<m;i++){
            for(int j=0;j<n;j++){
                if(dRow[i] == true || dCol[j] == true){
                    matrix[i][j] = 0;
                }
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public int maxProfit(int[] prices) {
        
        int mini = Integer.MAX_VALUE;
        int profit = 0;
        
        for(int i: prices){
            if(mini > i){
                mini = i;
            }
            if((i - mini) > profit){
                profit = i-mini;
            }
            /*
             *             else{
                profit = Math.max(profit,i-mini);
            }
             * */
        }
        
        return profit;
    }
    
    public void rotate(int[][] matrix) {
        
    	//Transpose 
        for(int i = 0;i<matrix.length;i++){
            for(int j=i;j<matrix.length;j++){
                swap(matrix,i,j,j,i);
            }
        }
        
        //reverse
        for(int i = 0;i<matrix.length;i++){
            for(int j=0;j<(matrix.length/2);j++){
                swap(matrix,i,j,i,matrix.length-j-1);
            }
        }
        printMatrix(matrix);
    }
    
    void swap(int[][] matrix,int x1,int y1,int x2,int y2){
        int temp = matrix[x1][y1];
        matrix[x1][y1] = matrix[x2][y2];
        matrix[x2][y2] = temp;
    }
    
    void printMatrix(int[][] matrix) {
        for(int i = 0;i<matrix.length;i++){
            for(int j=0;j<matrix.length;j++){
            	System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
    
	public static void main(String[] args) {
		
		Arrays2 arrays = new Arrays2();
		
		System.out.println("1. Set Matrix Zeros");
		int[][] matrix =new int[3][];
		matrix[0] = new int[] {1,1,1};
		matrix[1] = new int[] {1,0,1};
		matrix[2] = new int[] {1,1,1};
		
		arrays.setZeroes(matrix);  // time O(m*n + m*n)  ,space O(m) + O(n)
		
		System.out.println("\n\n5. Stock Buy and Sell");
		System.out.println("Maximum Profit : "+arrays.maxProfit(new int[] {7,1,5,3,6,4})); //O(n) space O(1)
		
		
		System.out.println("\n\n6. Rotate Matrix");
		matrix =new int[3][];
		matrix[0] = new int[] {1,2,3};
		matrix[1] = new int[] {4,5,6};
		matrix[2] = new int[] {7,8,9};
		arrays.rotate(matrix);
		
		
	}
}
