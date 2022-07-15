package com.recursion;

import java.util.ArrayList;
import java.util.List;

public class KthpermutationSequence {

	/*
	 *  Time Complexity: O(N) * O(N) = O(N^2)

		Reason: We are placing N numbers in N positions. This will take O(N) time. For every number, 
		we are reducing the search space by removing the element already placed in the previous step. This takes another O(N) time.
		
		Space Complexity: O(N) 
		
		Reason: We are storing  the numbers in a data structure(here vector)
	 */
	public static void main(String[] args) {
		
		System.out.println(getPermutation(4, 18));
	} 
	
	 public static String getPermutation(int n, int k) {
	        
	        int fact = 1;
	        List<Integer> list = new ArrayList<>();
	        
	        for(int i=1;i<n;i++){
	            fact = fact * i;
	            list.add(i);
	        }
	        
	        list.add(n);
	        k--; //logic follows 0 indexing
	        String res = "";
	        while(true){
	            
	        	//find which elemnt is suitable 
	            res += list.get(k/fact);
	            list.remove(k/fact);  
	            
	            if(list.size() == 0){
	                break;
	            }
	            
	            k = k % fact; //to fill next number in res
	            fact = fact/list.size(); //Decresing the level of fact
	            
	        }
	        return res;
	    }
}
