package com.backtracking;

import java.util.ArrayList;
import java.util.List;

import com.arrays.Arrays1;

public class GeneratePermutations {
	
	public static void main(String[] args) {
		
		GeneratePermutations r = new GeneratePermutations();
		
        System.out.println("\n\nPrint permutaions::"); 
        //O(n! * n) n because everytime we are looping till array length
        //space O(n) generate permutaitos
        List<Integer> ds = new ArrayList<>();
        
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
	
	/*
	 * Approach: 
	 * Generating permutations by using a Map to remember used elements
	 */
	void permute2(String str,boolean[] marked,String osf,List<String> al) {
		
	    // Base Case: If the current permutation is the same length as the original string,
	    // it means we've used all characters. Add it to the result list.
		if(osf.length() == str.length()) {
			al.add(osf);
			return;
		}
		
		for(int i=0;i<str.length();i++) {
			/*
			 * Pick the element only if it is not yet used
			 */
			if(!marked[i]) {
				marked[i] = true;
				permute2(str, marked, osf + str.charAt(i), al);
				marked[i] = false;
			}
		}
	}
	
	/*
	 * Generates all permutations of an integer array using in-place swapping.
	 */
	void permutations(int[] arr,int n,int i,List<Integer> ds) {
		
		// Base case: if the current permutation has 'n' elements, print it
		if(ds.size()==n) {
			System.out.println(ds);
		}
		
		/*
		 * At a particular Index i
		 * Try all the right elements at that index
		 * ex: at index 0 -> try 1 or 2 or 3 
		 * And continue this for all the indices
		 */
		for(int j=i;j<n;j++) {
			Arrays1.swapInArray(arr, i, j);
			ds.add(arr[i]);
			permutations(arr,n,i+1,ds);
			ds.remove(ds.size()-1);
			Arrays1.swapInArray(arr, i, j);
		}
	}
}
