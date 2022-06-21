package com.recursion;

import java.util.ArrayList;
import java.util.List;

public class Rec2 {

	public static void main(String[] args) {
		
		Rec2 r2 = new Rec2();
		
		System.out.println("String Subsequences : "); //O(2^n) because 2 operations every time, 
		//Space : O(n) as depth of rec tree would be n at max
		r2.subseqStr("abc", "");
		
		System.out.println("\n\nArray Subsequences : ");
		//O(2^n * n(for printing arraylist evertime)) because 2 operations every time,
		//Space : O(n) as depth of rec tree would be n at max
		int[] a = {1,2,3};
		ArrayList<Integer> ll = new ArrayList<>();
		
		r2.subseqIntArr(a, 0, 3, ll);
		
		ll = new ArrayList<>();
		r2.subseqIntArr2(a, 0, 3, ll);
		
		int[] a2 = {1,2,9,3,4,5,6};
		ArrayList<Integer> ll3 = new ArrayList<>();
		System.out.println("\n\nStop when any subseq with sum == k is found: ");
		
		System.out.println(r2.subseqIntArrBool(a2, 0, 7, 5,ll3,0));
		ArrayList<Integer> ll2 = new ArrayList<>();
		ll2.sort((a1,b1)->a1.compareTo(b1));
		System.out.println("\n\nCount of subsequences with sum k : " );
		
		System.out.println("Count : "+  r2.subseqIntArr(a2, 0, 7, 5,ll2,0));
		
		 List<List<Integer>> res = new ArrayList<>();
		 
		 System.out.println("\n\nCombinations 1 :"); //pick and not pick pattern
		 //O(2^n * k) k->avg size of combination as we create list out of it evertime 
		 // space O(x*k) x-> no of combinations (Ignoring aux space for recursion)
		 r2.combinations(new int[] {2,3,6,7},0,7,new ArrayList<>(),res);
	     System.out.println(res); 
	     
	     res = new ArrayList<>();
	     r2.combinations1(new int[] {2,3,6,7},0,7,new ArrayList<>(),res);
	     System.out.println(res); 
	     
	     
	     System.out.println("\n\nCombinations 2 : "); // Here it is about selecting 
		 //O(2^n * k) k->avg size of combination as we create list out of it evertime O(x*k) x-> no of combinations
	     List<List<Integer>> ress = new ArrayList<>();
	        
	     r2.combinations2(new int[] {1,1,2,5,6,7,10},0,8,new ArrayList<>(),ress);
	     System.out.println(ress);
	     
	}
	
    void combinations2(int[] candidates,int i,int target,List<Integer> ds,List<List<Integer>> res){
        
        if(target == 0){
            res.add(new ArrayList<>(ds));
            return;
        }
        
	    for(int j = i;j < candidates.length;j++)
        {
            if(target - candidates[j] < 0 ) return;
            //J == I because ignore for firsst and rest skip , Hence no diplicate combinations (Only once that combination witll be considered)
            if(j == i || candidates[j] != candidates[j-1]){
                ds.add(candidates[j]);
                combinations2(candidates,j+1,target-candidates[j],ds,res);
                ds.remove(ds.size() - 1);
            }
        }
    }
    
    void combinations1(int[] candidates,int i,int target,List<Integer> ds,List<List<Integer>> res){
        
        if(target == 0){
            res.add(new ArrayList<>(ds));
            return;
        }
        
        if(i == candidates.length){
            return;
        }
        
        
        for(int j=i;j<candidates.length;j++) {
	        if(candidates[j] <= target){
	            ds.add(candidates[j]);
	            combinations1(candidates,j,target-candidates[j],ds,res);
	            ds.remove(ds.size()-1);
	        }
        }
    }
    void combinations(int[] candidates,int i,int target,List<Integer> ds,List<List<Integer>> res){
        
        if(target == 0){
            res.add(new ArrayList<>(ds));
            return;
        }
        
        if(i == candidates.length){
            return;
        }
        
        
        if(candidates[i] <= target){
            ds.add(candidates[i]);
            combinations(candidates,i,target-candidates[i],ds,res);
            ds.remove(ds.size()-1);
        }
        
        combinations(candidates,i+1,target,ds,res);
    }
	/*
	 * This is how we count in recursions
	 * 
	 * All kinds of patterns
	 * https://www.youtube.com/watch?v=eQCS_v3bw0Q&list=PLgUwDviBIf0rGlzIn_7rsaR2FQ5e6ZOL9&index=7
	 */
	int subseqIntArr(int[] a,int i,int n,int k,ArrayList<Integer> ll,int sum) {
		
		if(i == n) {
			if(sum == k) {
				ll.forEach(e->System.out.print(e + " "));
				System.out.println();
				return 1;
			}
			return 0;
		}
		
		int x = a[i];
		sum += x;
		ll.add(x);
		int l = subseqIntArr(a,i+1,n,k,ll,sum);
		
		ll.remove(ll.size() - 1);
		sum -= x;
		int r = subseqIntArr(a,i+1,n,k,ll,sum);
		
		return l + r;
	}
	
	/*
	 * THis is how we return boolean values
	 */
	boolean subseqIntArrBool(int[] a,int i,int n,int k,ArrayList<Integer> ll,int sum) {
		
		if(i == n) {
			if(sum == k) {
				ll.forEach(e->System.out.print(e + " "));
				System.out.println();
				return true;
			}
			return false;
		}
		
		int x = a[i];
		sum += x;
		ll.add(x);
		if(subseqIntArrBool(a,i+1,n,k,ll,sum) == true)
			return true;
		
		ll.remove(ll.size() - 1);
		sum -= x;
		if(subseqIntArrBool(a,i+1,n,k,ll,sum) == true)
			return true;
		
		return false;
	}
	
	void subseqIntArr(int[] a,int i,int n,ArrayList<Integer> ll) {
		
		if(i == n) {
			if(ll.isEmpty()) {
				System.out.println("Empty");
			}else {
				ll.forEach(e->System.out.print(e + " "));
				System.out.println();
			}
			return;
		}
		
		int x = a[i];
		ll.add(x);
		subseqIntArr(a,i+1,n,ll);
		ll.remove(ll.size() - 1);
		subseqIntArr(a,i+1,n,ll);
	}
	
	void subseqIntArr2(int[] a,int i,int n,ArrayList<Integer> ll) {
		
		if(i == n) {
			if(ll.isEmpty()) {
				System.out.println("Empty");
			}else {
				ll.forEach(e->System.out.print(e + " "));
				System.out.println();
			}
			return;
		}
		int j = i;
		for( ;j<n;j++) {
			int x = a[j];
			ll.add(x);
			subseqIntArr2(a,j+1,n,ll);
			ll.remove(ll.size() - 1);
		}
		subseqIntArr2(a,j,n,ll);
	}
	
	
	void subseqStr(String s,String osf) {
		if(s.length() == 0) {
			if(osf != "")
				System.out.print(osf+" ");
			else
				System.out.println("Empty");
			return;
		}
			
		
		char c = s.charAt(0);
		subseqStr(s.substring(1),osf + c);
		subseqStr(s.substring(1),osf);
	}
}
