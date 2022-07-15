package com.recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.arrays.Arrays1;


public class Rec3 {

	public static void main(String[] args) {
		
		Rec3 r = new Rec3();
		
		int a[] = {5,2,1};
		
		System.out.println("4. Subset Sum-1:");

		List<Integer> ll = new ArrayList<>();
		r.subsetSum(a, 3, 0, 0, ll);
		Collections.sort(ll);
		ll.forEach(e->System.out.print(e + " "));
		
		System.out.println();
		ll = new ArrayList<>();
		r.subsetSum1(a, 3, 0, 0, ll);
		Collections.sort(ll);
		ll.forEach(e->System.out.print(e + " "));
		
		
		System.out.println("\n\n5. Subset Sum-2");
		List<List<Integer>> al = new ArrayList<>();
        int nums[] = {1,2,2,3,3,3,35};
        Arrays.sort(nums);
        List<Integer> ds = new ArrayList<>();
        r.subsetSum2(nums, nums.length, 0, ds,al);
        
        System.out.println(al);
		
        System.out.println("\n\nPrint permutaions::"); 
        //O(n! * n) n because everytime we are looping till array length
        //space O(n) generate permutaitos
        ds = new ArrayList<>();
        
        int [] arr = {1,2,3};
        r.permutations(arr, arr.length, 0, ds);
        
        System.out.println("Method 2:");
        //Using this for String as it is difficukt to swap in strings
        //O(n! * n) space: O(n)Marked array + O(n) generate permutaitos
        String str = "ABCD";
        List<String> stringList = new ArrayList<>(); 
        r.permute2(str, new boolean[str.length()], "", stringList);
        
        System.out.println(stringList);
        
	}
	
	void permute2(String str,boolean[] marked,String osf,List<String> al) {
		
		if(osf.length() == str.length()) {
			al.add(osf);
			return;
		}
		
		for(int i=0;i<str.length();i++) {
			
			if(!marked[i]) {
				marked[i] = true;
				permute2(str, marked, osf + str.charAt(i), al);
				marked[i] = false;
			}
		}
	}
	
	void permutations(int[] arr,int n,int i,List<Integer> ds) {
		
		if(ds.size()==n) {
			System.out.println(ds);
		}
		
		for(int j=i;j<n;j++) {
			Arrays1.swapInArray(arr, i, j);
			ds.add(arr[i]);
			permutations(arr,n,i+1,ds);
			ds.remove(ds.size()-1);
			Arrays1.swapInArray(arr, i, j);
		}
	}
	
	void subsetSum(int[] a,int n,int i,int sum,List<Integer> ll) {
		ll.add(sum);
		for(int j=i;j<n;j++) {
			subsetSum(a, n, j+1, sum+a[j], ll);
		}
	}
	
	void subsetSum1(int[] a,int n,int i,int sum,List<Integer> ll) {
		
		if(i == n) {
			ll.add(sum);
			return;
		}
		

		int num = a[i];
		sum += num;
		subsetSum1(a, n, i+1, sum, ll);
		sum -= num;
		subsetSum1(a, n, i+1, sum, ll);
	}
	
    void subsetSum2(int[] a,int n,int i,List<Integer> ll,List<List<Integer>> al) {

    al.add(new ArrayList<>(ll));
    for(int j=i;j<n;j++) {

        if(j == i || a[j] != a[j-1]){
            ll.add(a[j]);
               subsetSum2(a, n, j+1, ll,al);
            ll.remove(ll.size()-1);
        }
    }
	}
}
