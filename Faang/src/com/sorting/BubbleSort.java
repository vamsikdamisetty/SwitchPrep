package com.sorting;

public class BubbleSort {
//This algo is pushing Larger elements to the last
//Optimized approach is interesting
  public static void main(String[] args) {
    int arr[] = {4,2,6,1,4};
    bubbleSort(arr);
    for (int a:
    arr) {
      System.out.println(a);
    }
  }

  private static void bubbleSortOpt(int[] arr) {
    for(int i=0;i< arr.length-1;i++){
      boolean swapped=false; //optimisation
      for(int j=0;j< arr.length-i-1;j++){
        if(arr[j+1]<arr[j]){
          int t=arr[j+1];
          arr[j+1]=arr[j];
          arr[j]=t;
          swapped=true;
        }
      }
      if(swapped==false){
        break;
      }
    }
  }

  private static void bubbleSort(int[] arr) {
    for(int i=0;i< arr.length-1;i++){
      for(int j=0;j< arr.length-i-1;j++){
        if(arr[j+1]<arr[j]){
          int t=arr[j+1];
          arr[j+1]=arr[j];
          arr[j]=t;
        }
      }
    }
  }

}
