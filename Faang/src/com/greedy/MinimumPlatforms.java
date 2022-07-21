package com.greedy;

import java.util.Arrays;

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

	static int findPlatform(int arr[], int dep[], int n) {
		Arrays.sort(arr);
		Arrays.sort(dep);

		int maxPlats = 0;
		int platsUsed = 0;
		int j = 0;
		for (int i = 0; i < arr.length; i++) {

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
