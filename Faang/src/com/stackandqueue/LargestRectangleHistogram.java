package com.stackandqueue;

import java.util.Stack;

/*
 * Time Complexity: O( N )
 * Space Complexity: O(3N) where 3 is for the stack, pse and nse
 */
public class LargestRectangleHistogram {

	public static void main(String[] args) {

		System.out.println(largestRectangleArea(new int[] { 2, 1, 5, 6, 2, 3 }));
	}

	public static int largestRectangleArea(int[] heights) {

		int n = heights.length;
		int[] nse = new int[n];
		int[] pse = new int[n];

		Stack<Integer> s = new Stack<>();

		/*
		 * Finiding Next Smaller Element
		 */
		for (int i = n - 1; i >= 0; i--) {

			while (!s.empty() && heights[i] <= heights[s.peek()]) {
				s.pop();
			}

			if (!s.isEmpty()) {
				nse[i] = s.peek();
			} else {
				nse[i] = n;
			}
			s.add(i);
		}

		s.clear();

		/*
		 * Finiding Prev Smaller Element
		 */
		for (int i = 0; i < n; i++) {

			while (!s.empty() && heights[i] <= heights[s.peek()]) {
				s.pop();
			}

			if (!s.isEmpty()) {
				pse[i] = s.peek();
			} else {
				pse[i] = -1;
			}
			s.add(i);
		}

		int maxHeight = 0;
		for (int i = 0; i < n; i++) {
			maxHeight = Math.max(maxHeight, heights[i] * (nse[i] - pse[i] - 1));
		}

		return maxHeight;
	}

}
