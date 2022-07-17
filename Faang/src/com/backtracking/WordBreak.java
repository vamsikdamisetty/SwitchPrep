package com.backtracking;

import java.util.Arrays;
import java.util.List;

public class WordBreak {

	
	/*
	 * This solution is using backtracking but can be done better with DP 
	 * Time Complexity: O(2n). Because there are 2n combinations in The Worst Case.
	 * Auxiliary Space: O(n2). Because of the Recursive Stack of wordBreakUtil(�)
	 * function in The Worst Case.
	 */
	 public static void main(String args[])
	  {
	    String str1 = "iloveicecreamandmango"; // for first test case
	    String str2 ="ilovesamsungmobile";     // for second test case
	    int n1 = str1.length();                 // length of first string
	    int n2 = str2.length();                 // length of second string
	 
	    // List of strings in dictionary
	    List <String> dict= Arrays.asList("mobile","samsung","sam","sung",
	                                      "man","mango", "icecream","and",
	                                      "go","i","love","ice","cream");        
	    System.out.println("First Test:");
	 
	    // call to the method
	    wordBreak(n1,dict,str1);
	    System.out.println("\nSecond Test:");
	 
	    // call to the method
	    wordBreak(n2,dict,str2);
	  }

	private static void wordBreak(int n1, List<String> dict, String s) {
		
		String ans = "";
		
		solve(s,dict,ans);
		
	}

	private static void solve(String s, List<String> dict, String ans) {
		
		for(int i=1;i<=s.length();i++) {
			
			String prefix = s.substring(0,i); // making prefizes for recursion
			
			if(dict.contains(prefix)) { // Verifying valid prefixes for recursion
				
				if(i == s.length()) { //Base condition
					ans += prefix;
					System.out.println(ans);
					return;
				}
				
				solve(s.substring(i), dict, ans +prefix + " ");
			}
		
	}
	}
}
