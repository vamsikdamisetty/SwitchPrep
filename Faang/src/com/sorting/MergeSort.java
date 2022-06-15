package com.sorting;

public class MergeSort {

	/*
	 * O(nlogn) even in the worst case
	 * • In merge sort all elements are copied into an auxiliary array 
		• so N auxiliary space is required for merge sort.
	 * */
	public static void main(String[] args) {
		int arr[] = {4, 2, 6, 1, 4,9,2,4,7,55,1,3,6};
	    divide(arr,0,arr.length-1);
	    for (int a:
	    arr) {
	      System.out.println(a);
	    }
	}
	
	 static void conquer(int arr[], int l, int m, int r)
	    {
	 
	        /* Create temp array */
	        int merged[] = new int[r -l + 1];
	        
		 
	        // Initial indexx of array;
	        int x = 0;
	        int i1 = l, i2 = m+1;
	        
	 
	        while (i1 <= m && i2 <= r) {
	            if(arr[i1] <= arr[i2]) {
	            	merged[x++] = arr[i1++];
	            }else {
	            	merged[x++] = arr[i2++];
	            }
	        }
	        
	        while(i1 <= m) {
	        	merged[x++] = arr[i1++];
	        }
	        
	        while(i2 <= r) {
	        	merged[x++] = arr[i2++];
	        }
	        
	        for(int i=0, j = l;i<merged.length;i++,j++) {
	        	arr[j] = merged[i];
	        }
	    }
	 

	    static void divide(int arr[], int l, int r)
	    {
	        if (l < r) {
	            // Find the middle point
	            int m =l+ (r-l)/2;
	 
	            divide(arr, l, m);
	            divide(arr, m + 1, r);
	 
	            // Merge the sorted halves
	            conquer(arr, l, m, r);
	        }
	    }
}
