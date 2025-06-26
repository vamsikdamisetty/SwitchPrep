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
        
	}
	
	
	/*
	 * Index Selection Approach
	 */
	void subsetSum(int[] a,int n,int i,int sum,List<Integer> ll) {
		ll.add(sum);
		for(int j=i;j<n;j++) {
			subsetSum(a, n, j+1, sum+a[j], ll);
		}
	}
	
	/*
	 * Pick/ Not Pick approach
	 */
	void subsetSum1(int[] a,int n,int i,int sum,List<Integer> ll) {
		
		if(i == n) {
			ll.add(sum);
			return;
		}
		

		subsetSum1(a, n, i+1, sum+a[i], ll);		
		subsetSum1(a, n, i+1, sum, ll);
	}
	
	/*
	 * DIfference between Combinations sum and subset sum?
	 * Comb :: Only unique combinations that sum to target
	 * Subset :: All unique subsets (no sum constraint)
	 */
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
