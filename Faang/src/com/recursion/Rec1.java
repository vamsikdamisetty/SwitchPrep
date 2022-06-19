package com.recursion;

import com.arrays.Arrays1;

public class Rec1 {

	public static void main(String[] args) {
		Rec1 r = new Rec1();
		r.printName(5);
		r.printLinearly(5);
		r.printLinearlyRev(5);
		
		//parametrized
		r.sumOfN(5, 0);
		
		//funcionreturn - reccursive
		System.out.println("Sum of N : " + r.sumOfN(5));
		
		
		System.out.println("Factorial : " + r.fact(5));
		
		System.out.println("Reverse an array with reccursion: " );
		
		int a[] = {1,2,3,4,5,6,7,8,9};
 		r.revArray(a, 0, 8);
 		Arrays1.printArray(a);
 		
 		System.out.println("\n\nPalindrome : "+ r.palindrome(0, new char[] {'M','A','D','A','M','M'}, 6));
	
 		System.out.println("\n\n Fib : " + r.fib(10));
	}
	
	int fib(int n) {
		if(n <= 1) return n;
		
		return fib(n-1) + fib(n-2);
	}
	
	
	boolean palindrome(int i,char[] arr,int n) {
		
		if(i >= n/2) return true;
		
		if(arr[i] != arr[n-i-1])
			return false;
		
		return palindrome(i+1, arr, n);
	}
	
	void revArray(int[] a,int i,int j) {
		
		if(i >= j ) return;
		
		Arrays1.swapInArray(a, i, j);
		revArray(a, i+1, j-1);
	}
	
	
	int fact(int n) {
		if(n==1) return 1;
		
		return n * fact(n-1);
	}
	
	int sumOfN(int n) {
		if(n == 1) return 1;
		
		return n + sumOfN(n - 1);
	}
	void sumOfN(int n,int sum) {
		if(n < 1) {
			System.out.println(sum);
			return;
		}
		
		sumOfN(n - 1, sum + n);
	}
	
	void printName(int n) {
		if(n==0) return;
		
		System.out.println("Vamsi");
		printName(n-1);
	}
	
	void printLinearly(int n) {
		if(n==0) return;
		
		
		printLinearly(n-1);
		System.out.println(n);
	}
	
	void printLinearlyRev(int n) {
		if(n==0) return;
		
		
		System.out.println(n);
		printLinearlyRev(n-1);
		
	}
}
