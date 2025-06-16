package com.greedy;

import java.util.Arrays;
/*
 *  Naive Approach 

Intuition: Take each interval of arrival and departure one by one and count the number of overlapping time intervals. 
This can easily be done using nested for-loops. 
Maintain the maximum value of the count during the process and return the maximum value at the end.

Approach: We need to run two nested for-loops. Inside the inner loop count the number of 
intervals which intersect with the interval represented by the outer loop. 
As soon as the inner loop ends just update the maximum value of count and proceed with the next iteration of the outer loop. 
After the process ends we will get the maximum value of the count.
 */

/*
 * Time Complexity: O(nlogn) Sorting takes O(nlogn) and traversal of arrays takes O(n) so overall time complexity is O(nlogn).
 *	Space complexity: O(1)  (No extra space used).
 */
public class MinimumPlatforms {

	public static void main(String[] args) {
		int[] arr = { 900, 945, 955, 1100, 1500, 1800 };
		int[] dep = { 920, 1200, 1130, 1150, 1900, 2000 };
		int n = arr.length;
		int totalCount = findPlatform(arr, dep, n);
		System.out.println("Minimum number of Platforms required " + totalCount);
	}

	/*
	 * Intuition: No of trains arrived before any departure happens is the number of
	 * platforms used at that point
	 * 
	 */
	/*
	 * At first we need to sort both arrays. When the events will be sorted, it will
	 * be easy to track the count of trains that have arrived but not departed yet.
	 * The total platforms needed at one time can be found by taking the difference
	 * between arrivals and departures at that time and the maximum value of all
	 * times will be the final answer.
	 */
	static int findPlatform(int arr[], int dep[], int n) {
		Arrays.sort(arr);
		Arrays.sort(dep);

		int maxPlats = 0;
		int platsUsed = 0;
		int j = 0;
		for (int i = 0; i < arr.length; i++) {
			
			//Check for every arrival time how many trains can departure
			while (j < dep.length && dep[j] < arr[i]) {
				j++;
				platsUsed--;
			}
			
			platsUsed++;
			maxPlats = Math.max(platsUsed, maxPlats);

		}

		return maxPlats;
	}
}
