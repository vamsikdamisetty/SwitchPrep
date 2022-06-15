package com.search;

public class LinearSearch {



  public static void main(String[] args) {
    int arr[]={8,3,5,0,1};
    lsearch(arr,0,5);
  }

  private static void lsearch(int[] arr, int i, int key) {
    if(arr[i]==key){
      System.out.println("Element found at positon "+ (i+1));
      return;
    }
    if(i==arr.length) return;

    lsearch(arr,i+1,key);

  }

}
