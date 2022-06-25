package com.stackandqueue;

import java.util.Stack;

import com.arrays.Arrays1;

public class NextGreaterElement {

	/*
	 * TC : O(2n + 2n) Space : O(n)
	 */
	public static void main(String[] args) {
		int arr[] = { 5, 7, 1, 2, 6, 0 };

		int nge[] = nextGreaterElements(arr);
		System.out.println("The next greater elements are : ");
		Arrays1.printArray(arr);
		System.out.println();
		Arrays1.printArray(nge);
		
		System.out.println();
		int arr2[] = {7,8,1,5,3,4,10,1};
		//Not for Circular
 		System.out.println("The previous Smaller Element : ");
 		Arrays1.printArray(arr2);
 		int pse[] = prevSmaller(arr2);
 		System.out.println();
 		Arrays1.printArray(pse);
	}

	public static int[] nextGreaterElements(int[] nums) {

		int n = nums.length;
		Stack<Integer> s = new Stack<>();
		int[] nge = new int[n];

		for (int i = 2 * n - 1; i >= 0; i--) {

			while (!s.isEmpty() && nums[s.peek()] <= nums[i % n]) {
				s.pop();
			}

			if (i < n) {
				nge[i % n] = s.isEmpty() ? -1 : nums[s.peek()];
			}
			s.push(i % n);
		}
		return nge;
	}

	public static int[] prevSmaller(int[] A) {

		int n = A.length;
		Stack<Integer> s = new Stack<>();
		int[] pse = new int[n];

		for (int i = 0; i < n; i++) {

			while (!s.isEmpty() && A[s.peek()] >= A[i]) {
				s.pop();
			}

			if (s.isEmpty()) {
				pse[i] = -1;
			} else {
				pse[i] = A[s.peek()];
			}
			s.push(i);
		}

		return pse;
	}
}
