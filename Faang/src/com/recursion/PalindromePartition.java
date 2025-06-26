package com.recursion;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartition {

	public static void main(String[] args) {
		
		PalindromePartition r= new PalindromePartition();
		/*
		 	Time Complexity: O( (2^n) *k*(n/2) )

			Reason: O(2^n) to generate every substring and O(n/2)  to check if the substring generated is a palindrome. O(k) is for inserting the palindromes in another data structure, where k  is the average length of the palindrome list.
			
			Space Complexity: O(k * x)
			
			Reason: The space complexity can vary depending upon the length of the answer. k is the average length of the list of palindromes and if we have x such list of palindromes in our final answer. The depth of the recursion tree is n, so the auxiliary space required is equal to the O(n).
		 */
		System.out.println("3. Palindrome Partioning:");
		System.out.println(r.partition("aabab"));
	}
	
    public List<List<String>> partition(String s) {
        
        List<List<String>> res = new ArrayList<>();
        
        List<String> rsf = new ArrayList<>();
        
        solve(s,0,rsf,res);
        
        return res;
    }
    
    void solve(String s,int index,List<String> rsf,List<List<String>> res){
        
        if(index == s.length()){
            res.add(new ArrayList<>(rsf));
            return;
        }
        
        for(int i=index;i < s.length();i++){
            if(isPalindrome(s,index,i)){
                rsf.add(s.substring(index,i+1));
                solve(s,i+1,rsf,res);
                rsf.remove(rsf.size()-1);
            }
        }
    }
    
    boolean isPalindrome(String s,int start,int end){
        while(start<end){
            if(s.charAt(start++) != s.charAt(end--)){
                return false;
            }
        }
        return true;
    }
}
