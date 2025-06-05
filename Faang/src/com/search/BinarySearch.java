package com.search;

public class BinarySearch {

  public static void main(String[] args) {
    int arr[]={1,2,3,4,5,6,7,8,9,10};
    bsearch(arr,0,arr.length-1,8);
  }

  private static void bsearch(int[] arr, int l, int r, int key) {
	  
    if(l>r){
      System.out.println("Element not found");
      return;
    }
    int mid=l+(r-l)/2;    //can use (l+r)/2   we did that for crores of values to lower chances of overflow of int
    if(arr[mid]==key){
      System.out.println("Element found at "+(mid+1));
      return;
    }
    if(key<arr[mid]){
      bsearch(arr,l,mid-1,key);
    }
    else {
      bsearch(arr,mid+1,r,key);
    }
  }
}
