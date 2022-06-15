package com.sorting;


public class SelectionSOrt {
	//THis algo finds min index and swaps I wilth min_Index
  public static void main(String[] args) {
    int arr[] = {4, 2, 6, 1, 4,9,2,4,7,55,1,3,6};
    selectionSort(arr);
    int n = arr.length;
    for (int i=0; i<n; ++i)
      System.out.print(arr[i]+" ");
    System.out.println();
  }

  private static void selectionSort(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
      int min_index=i;
      for (int j = i+1; j < arr.length; j++) {
        if (arr[min_index] > arr[j]) {
         min_index=j;
        } 
      }
          int t = arr[min_index];
          arr[min_index] = arr[i];
          arr[i] = t;
        
    }
  }

}

