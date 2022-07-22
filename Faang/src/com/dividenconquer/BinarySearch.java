package com.dividenconquer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.arrays.Arrays1;

public class BinarySearch {

	public static void main(String[] args) {
		
		int arr[] = {1,2,5,8,9,10,11,18,20};
		
//		Arrays.setAll(a, e->e=-1);
//		
//		Arrays1.printArray(a);
		
		System.out.println(Arrays.binarySearch(arr, 10));
		System.out.println(Arrays.binarySearch(arr, 15));
		
		System.out.println(Arrays.toString(arr));
//		Arrays.stream(arr).forEach(a->System.out.print(a));		
		
		int[] arr2 = {2,3,5,5,5,7,8,8,8};
		List<Integer> list = new ArrayList<>();
		for(int i:arr2) {
			list.add(i);
		}
		
		System.out.println("Lower Bound 5: " + first(arr2, 0, arr.length-1, 5, arr.length));
		System.out.println(lowerBound(arr2, 2)); //iterative
		System.out.println("Lower Bound 1: " + first(arr2, 0, arr.length-1, 1, arr.length));
		System.out.println("Lower Bound 8 by ArrayList: " + list.indexOf(8));
		
		
		
		System.out.println("Upper Bound 8: " + last(arr2, 0, arr.length-1, 8, arr.length));
		System.out.println("Upper Bound 9: " + last(arr2, 0, arr.length-1, 9, arr.length));
		System.out.println("Upper Bound 8 by ArrayList: " + list.lastIndexOf(8));
		
		System.out.println("Largest Number smaller than X :");
		int lower = first(arr2, 0,arr.length-1, 5, arr.length);
		if(lower != 0) {
			System.out.println(arr2[lower-1]);
		}
		
		System.out.println((int)1e9);
		System.out.println(Integer.MAX_VALUE);
		
	}
	
	   /* if x is present in arr[] then returns the index of
    FIRST occurrence of x in arr[0..n-1], otherwise
    returns -1 */
    public static int first(int arr[], int low, int high, int x, int n)
    {
        if (high >= low) {
            int mid = low + (high - low) / 2;
            if ((mid == 0 || x > arr[mid - 1]) && arr[mid] == x)
                return mid;
            else if (x > arr[mid])
                return first(arr, (mid + 1), high, x, n);
            else
                return first(arr, low, (mid - 1), x, n);
        }
        return -1;
    }
 
    /* if x is present in arr[] then returns the index of
    LAST occurrence of x in arr[0..n-1], otherwise
    returns -1 */
    public static int last(int arr[], int low, int high, int x, int n)
    {
        if (high >= low) {
            int mid = low + (high - low) / 2;
            if ((mid == n - 1 || x < arr[mid + 1]) && arr[mid] == x)
                return mid;
            else if (x < arr[mid])
                return last(arr, low, (mid - 1), x, n);
            else
                return last(arr, (mid + 1), high, x, n);
        }
        return -1;
    }
    
    
    public static int  lowerBound(int[] arr,int k) {
		
    	int l = 0;
    	int h = arr.length -1;
    	
    	while(l<=h) {
    		
    		int  m = (l+h)/2;
    		
    		if( (m == 0 || arr[m-1] < k) && arr[m]==k ) {
    			return m;
    		}
    		
    		if(k <= arr[m]) {
    			h = m-1;
    		}
    		else {
    			l = m+1;
    		}
    	}
    	
    	return -1;
	}
}
