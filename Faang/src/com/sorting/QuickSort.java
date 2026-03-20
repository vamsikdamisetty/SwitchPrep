package com.sorting;

public class QuickSort {

	// O(nlogn) avg case but Worst O(n^2) when the array is already sorted, 
	//So quickSort will go n levels n + n-1 + n-2 ... 1  n(n+1)/2 hence N^2
	//Space : O(1)
	/*
	Pick a pivot (any element)

	Rearrange array so:
	Left → smaller than pivot
	Right → greater than pivot
	Pivot is now in final sorted position
	Repeat on left + right

	 */
	public static void main(String[] args) {
		int arr[] = {4, 2, 6, 1, 4,9,2,4,7,55,1,3,6};
	    quickSort(arr,0,arr.length-1);
	    for (int a:
	    arr) {
	      System.out.println(a);
	    }
	}

	private static void quickSort(int[] arr, int l, int h) {
		
		if(l < h) {
			
			int pivot = partition(arr,l,h);
			
			quickSort(arr, l, pivot - 1);
			quickSort(arr, pivot + 1, h);
		}
		
	}

	private static int partition(int[] arr, int l, int h) {
		
		int i = l;
		int pivot = arr[h];
		/*
		 i is tracking the left list [ smaller than pivot]
		 while swapping all on the other side become  larger than pivot
		 */
		for(int j=l;j<h;j++) {
			if(arr[j] <= pivot) {
				//swap
				int temp = arr[j];
				arr[j] = arr[i];
				arr[i] = temp;
				i++;
			}
		}

		/*
		move the pivot to the middle
		and return pivot index
		 */
		int temp = arr[i];
		arr[i] = pivot;
		arr[h] = temp;
		return i;
	}
	
}
