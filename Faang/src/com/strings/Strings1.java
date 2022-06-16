package com.strings;

import java.util.HashMap;
import java.util.Scanner;

public class Strings1 {

	public static void main(String[] args) {
		
		System.out.println("1. Reverse Words in a String");
		String s = "   Vamsi  is an   amazing    coder   ";
		System.out.println(reverseWords(s));
		System.out.println(reverseWords2(s));
		
//		System.out.println("Plaindrome of a String");
//		palindrome();
		
		System.out.println("\n\n2. Longest Palindrome in a string"); //O(n^2) using DP 
		//https://www.youtube.com/watch?v=UflHuQj6MVA
		//If Brute force O(n^3) by going throw all substrings and checking palindrome
		System.out.println(longestPalindrome("aaaaa"));
		
		System.out.println("\n\n3.  Roman Number to Integer and vice versa");
		System.out.println("Roman number: MCMXCIV :: " + romanToInt("MCMXCIV"));
		
		System.out.println("Integer to Roman Numeral :: " + intToRoman(romanToInt("MCMXCIV")));
		System.out.println("Integer to Roman Numeral :: Better sol " + intToRoman2(3215));
		
		System.out.println("\n\n4. Implement ATOI/STRSTR");
		System.out.println("ATOI :"+myAtoi("-91283472332"));
		System.out.println("STRSTR : Index:: " + strStr("Hello", "ll"));
		
		System.out.println("\n\n5. Longest Common Prefix");  //O(m*n) , space O(m) N = Number of strings M = Length of the largest string
		System.out.println(longestCommonPrefix(new String[] {"","Flow","Flop"}));
	}



	private static String reverseWords(String s) {
		String s1[]= s.split(" ");
        String res="";
	    for(int i=s1.length-1;i>=0;i--){
	    	if(!s1[i].trim().isEmpty()) {
	    		res=res+s1[i]+" ";
	    	}
	    }
        return res.trim();
	}
	
    public static String reverseWords2(String s) {
        
        String out = "";

        int i=0,j;
        int n = s.length();
        while(i<n){
            while(i<n &&  s.charAt(i) == ' '){
                i++;
            }
            j = i+1; //not mandatory
            while(j<n && s.charAt(j) != ' '){
                j++;
            }
            
            if(i != n){
	            if(!out.isEmpty())
	                out = s.substring(i,j) + " " + out;
	            else
	                out = s.substring(i,j);
	            i = j+1;
            }
        }
        return out;
    }
    
    static void palindrome() {
        String string = "Kayak";  
        Scanner sc = new Scanner(System.in);
        
        while(true)
        {
        	string = sc.next();
	        boolean flag = true;  
	          
	        //Converts the given string into lowercase  
	        string = string.toLowerCase();  
	          
	        //Iterate the string forward and backward, compare one character at a time   
	        //till middle of the string is reached  
	        for(int i = 0; i < string.length()/2; i++){  
	            if(string.charAt(i) != string.charAt(string.length()-i-1)){  
	                flag = false;  
	                break;  
	            }  
	        }  
	        if(flag)  
	            System.out.println("Given string is palindrome");  
	        else  
	            System.out.println("Given string is not a palindrome");
        }
        
    }
    
    public static String longestPalindrome(String s) {
        int n = s.length();
        
        boolean[][] dp = new boolean[n][n];
        
        int maxLength = 1;
        
        for(int i=0;i < n;i++){
            dp[i][i] = true;
        }
        
        int start = 0;
        int end=0;
        for(int i=0;i<n-1;i++){
            if(s.charAt(i) == s.charAt(i+1)){
                dp[i][i+1] = true;
                maxLength = 2;
                start = i;
                end = i+1;
            }
        }
        
        for(int k=3;k<=n;k++){
            for(int i=0;i<n-k+1;i++){
                
                int j = i + k -1 ;
                if(dp[i+1][j-1] &&  s.charAt(i) == s.charAt(j)){
                    
                    dp[i][j] = true;
                    if(k > maxLength){
                        maxLength = k;
                        start = i;
                        end = j;
                    }
                }
            }
        }
        
        return s.substring(start,end+1);
    }
    
    /*
     * Liked the below solution
     * 
     * 
class Solution {
    int maxLen = -1;
    int start = -1;
    public String longestPalindrome(String s) {
        for(int i = 0; i < s.length(); i++){
            isPalindrome(s, i, i);
            isPalindrome(s, i, i+1);
        }
        return s.substring(start, start+maxLen);
    }
    
    void isPalindrome(String s, int l, int r){
        while(l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)){
            l--;
            r++;
        }
        int len = r-l-1;
        if(len > maxLen){
            maxLen = len;
            start = l+1;
        }
    }
}
     */
    
    public static int romanToInt(String s) {
        HashMap<Character,Integer> map = new HashMap<>();
        map.put('I',1);
        map.put('V',5);
        map.put('X',10);
        map.put('L',50);
        map.put('C',100);
        map.put('D',500);
        map.put('M',1000);
        
        int res = map.get(s.charAt(s.length()-1));
        
        for(int i=s.length()-2;i>=0;i--){
            
            if(map.get(s.charAt(i)) < map.get(s.charAt(i+1))){
                res -= map.get(s.charAt(i));
            }else{
                res += map.get(s.charAt(i));
            }
        }
        return res;
    }
    
    public static String intToRoman(int num) {
        String thousands[] = {"","M","MM","MMM"};
        
        String hundreds[] = {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"};
        
        String tens[] = {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"};
        
        String ones[] = {"","I","II","III","IV","V","VI","VII","VIII","IX","X"};
        
        return thousands[num/1000] + hundreds[(num%1000)/100] + tens[(num%100)/10] +ones[num%10];
    }
    
	private static String intToRoman2(int num) {
		int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        
        String[] roman = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        
        String res="";
        for(int i=0;i<values.length;i++){
            
            while(num >= values[i]){
                res += roman[i];
                num -= values[i];
            }
        }
        return res;
	}
	
    public static int myAtoi(String s) {
        
    	//corner case
        if(s.length() == 0) return 0;
        int i = 0;
        
        while(i < s.length() && s.charAt(i) == ' '){
            i++;
        }
        int sign = 1;
        if(i < s.length()){
            if(s.charAt(i) == '-'){
                sign = -1;
                i++;
            }
            else if(s.charAt(i) == '+'){
                sign = 1;
                i++;
            }
        }
        long res = 0;  //Long TO handle Overflow and underflow of int
        
        while(i < s.length()){
            
            if(!(s.charAt(i) >='0' && s.charAt(i) <='9')){
                break;
            }
            
            if(res == 0){
                res = s.charAt(i) - '0';
            }else{
                res = res*10 + s.charAt(i) - '0';
            }
            
            //when value crosses :: Condition in leetcode
            if(res*sign <= Integer.MIN_VALUE){
                return Integer.MIN_VALUE;
            }else if(res*sign >= Integer.MAX_VALUE){
                return Integer.MAX_VALUE;
            }
            i++;
        }
        

    return (int)res * sign;
    }
    
    public static int strStr(String haystack, String needle) {
        
        
        int i =0;
        
        while(i < haystack.length()){
            
            if(haystack.charAt(i) == needle.charAt(0)){
                int j=0;
                int start = i;
                while(i < haystack.length() && haystack.charAt(i) == needle.charAt(j)){
                    i++;
                    j++;
                    if(j == needle.length())
                    {
                        return start;
                    }
                }
                i = start;
            }
            i++;
        }
        return -1;
    }
    
    public static String longestCommonPrefix(String[] strs) {
        
        // if(strs.length == 0) return "";
        String lcp = strs[0];
        
        // if(lcp == "") return "";
        for(int j=1;j<strs.length;j++){
                        
            while(strs[j].indexOf(lcp) != 0){
                lcp = lcp.substring(0,lcp.length()-1);
            }
        }
        return lcp;
    }
}
