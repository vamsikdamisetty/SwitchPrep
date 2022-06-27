package com.stackandqueue;

import java.util.ArrayDeque;
import java.util.Deque;

import com.arrays.Arrays1;

/*
 * O(n^2) brute force just iterate through all windows
 * 
 * Using Queue:
 * Time Complexity: O(N)
 * Space Complexity: O(K) As deque can never be bigger than window
 * 
 * Steps:
 * 1. remove element outside of window
 * 2. Before inserting curr elemnt check and remove if any emlemnt is less than curr from left side
 * 3. Add element
 * 4. Element at front is max of window
 * 
 */
public class SlidingWindowMax {

	public static void main(String[] args) {
		int[] res = maxSlidingWindow(new int[] {1,3,-1,-3,5,3,6,7}, 3);
		
		Arrays1.printArray(res);
	}
    
	public static int[] maxSlidingWindow(int[] nums, int k) {
		int n = nums.length;
		int[] res = new int[n - k + 1];
		
		Deque<Integer> deque = new ArrayDeque<>();
		
		for(int i=0;i<n;i++) {
			
			if(!deque.isEmpty() && deque.peek() == i-k) {
				deque.pollFirst();
			}
			
			while(!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
				deque.pollLast();
			}
			
			deque.offer(i);
			
			if(i >= k-1) {
				res[i-(k-1)] = nums[deque.peekFirst()];
			}
		}
		
		return res;
    }
    
}
