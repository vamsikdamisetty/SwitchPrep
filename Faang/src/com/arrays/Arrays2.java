package com.arrays;

import java.util.Arrays;

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
    
	public void setZeroesOptimal(int[][] matrix) {

		int m = matrix.length;
		int n = matrix[0].length;

		int col0 = 1;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (matrix[i][j] == 0) {
					matrix[i][0] = 0;

					if (j != 0)
						matrix[0][j] = 0;
					else
						col0 = 0;
				}
			}
		}

		for (int i = 1; i < m; i++) {
			for (int j = 1; j < n; j++) {
				if (matrix[i][0] == 0 || matrix[0][j] == 0) {
					matrix[i][j] = 0;
				}
			}
		}
		
		//if we do col0 first that might change the value of matrix[0][0]
		if (matrix[0][0] == 0) {
			for (int i = 0; i < n; i++) {
				matrix[0][i] = 0;
			}
		}

		if (col0 == 0) {
			for (int j = 0; j < m; j++) {
				matrix[j][0] = 0;
			}
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
    
    static void swap(int[][] matrix,int x1,int y1,int x2,int y2){
        int temp = matrix[x1][y1];
        matrix[x1][y1] = matrix[x2][y2];
        matrix[x2][y2] = temp;
    }
    
    public static void printMatrix(int[][] matrix) {
        for(int i = 0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length;j++){
            	System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
    
	public void nextPermutation(int[] nums) {

		int breakIndex = -1;
		int n = nums.length;

		// Find a break index from the end where there is dip (where descending breaks)
		for (int i = n - 2; i >= 0; i--) {
			if (nums[i] < nums[i + 1]) {
				breakIndex = i;
				break;
			}
		}

		// corner case where we have last permutations
		if (breakIndex == -1) {
			reverseArray(nums, 0, n - 1);
		} else {
			// Find the immediate bigger element after element in break index and swap it
			for (int i = n - 1; i > breakIndex; i--) {
				if (nums[i] > nums[breakIndex]) {
					int temp = nums[i];
					nums[i] = nums[breakIndex];
					nums[breakIndex] = temp;
					break;
				}
			}
			// now we know the swapped element has
			// all the larger one's on left and smaller on right
			// so we reverse the sub array
			reverseArray(nums, breakIndex + 1, n - 1);
		}

	}

	void reverseArray(int[] nums, int i, int j) {
		while (i < j) {
			int temp = nums[i];
			nums[i] = nums[j];
			nums[j] = temp;

			i++;
			j--;
		}
	}
	
	static int inversionCount(int arr[]) {
        int[] cnt = new int[1];
        mergeSort(arr,cnt);

        return cnt[0];
    }
    
    public static int[] merge(int[] array1, int[] array2,int[] cnt) {
        int[] combined = new int[array1.length + array2.length];
        int index = 0;
        int i = 0;
        int j = 0;
        while (i < array1.length && j < array2.length) {
            if (array1[i] <= array2[j]) {
                combined[index] = array1[i];
                index++;
                i++;
            } else {
            	/*
            	 * Only change is here 
            	 * Idea is to think of two sorted arrays and imagine 
            	 * if i1 is greater than j1 then all the elements in array 1 after i1 are greater and can make pairs with
            	 * all the elemets in array2 after j2
            	 */
                cnt[0] += array1.length - i ;
                combined[index] = array2[j];
                index++;
                j++;
            }
        }
        while (i < array1.length) {
            combined[index] = array1[i];
            index++;
            i++;
        }
        while (j < array2.length) {
            combined[index] = array2[j];
            index++;
            j++;
        }
        return combined;
    }

    public static int[] mergeSort(int[] array,int[] cnt) {
        if (array.length == 1) return array;

        int midIndex = array.length/2;
        int[] left = mergeSort(Arrays.copyOfRange(array, 0, midIndex),cnt);
        int[] right = mergeSort(Arrays.copyOfRange(array, midIndex, array.length),cnt);

        return merge(left, right,cnt);
    }
    
	public static void main(String[] args) {
		
		Arrays2 arrays = new Arrays2();
		
		System.out.println("1. Set Matrix Zeros");
		int[][] matrix =new int[3][];
		matrix[0] = new int[] {1,1,1};
		matrix[1] = new int[] {1,0,1};
		matrix[2] = new int[] {1,1,1};
		
		arrays.setZeroes(matrix);  // time O(m*n + m*n)  ,space O(m) + O(n)
		
		matrix[0] = new int[] {1,1,1};
		matrix[1] = new int[] {1,0,1};
		matrix[2] = new int[] {1,1,1};
		
		System.out.println("Ans of Optimal Approach");
		arrays.setZeroesOptimal(matrix);
		arrays.printMatrix(matrix);	
		
		System.out.println("\n\n3. Next Permutation");
		int[] nums = new int[] {3,1,5,4,2};
		Arrays1.printArray(nums);
		System.out.println();
		arrays.nextPermutation(nums);
		Arrays1.printArray(nums);
 		
		System.out.println("\n\n4. Count Inversions");
		System.out.println(Arrays2.inversionCount(new int[] {4,3,6,1,2})); //O(nlogn) space would be O(n) if array cannot be modified
		
		System.out.println("\n\n5. Stock Buy and Sell");
		System.out.println("Maximum Profit : "+arrays.maxProfit(new int[] {7,1,5,3,6,4})); //O(n) space O(1)
		
		
		System.out.println("\n\n6. Rotate Matrix");
		//Approach is to first transpose and then reverse a matrix
		matrix =new int[3][];
		matrix[0] = new int[] {1,2,3};
		matrix[1] = new int[] {4,5,6};
		matrix[2] = new int[] {7,8,9};
		arrays.rotate(matrix);
		
		
	}
}
