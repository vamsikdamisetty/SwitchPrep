package com.sorting;

public class InsertionSort {

	public static void main(String[] args) {
		int arr[] = {4, 2, 6, 1, 4,9,2,4,7,55,1,3,6};
	    insertionSort(arr);
	    for (int a:
	    arr) {
	      System.out.println(a);
	    }
	}

	private static void insertionSort(int[] arr) {
		
		for(int i=1;i<arr.length;i++) {
			int currNum = arr[i];
			int j = i-1;
			while(j >= 0 && arr[j] > currNum) {
				arr[j+1] = arr[j];
				j--;
			}
			
			arr[j+1] = currNum;
		}
		
	}
}
