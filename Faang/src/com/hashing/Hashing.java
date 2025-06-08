package com.hashing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arrays.Arrays1;

public class Hashing {

	/*
	 * Idea is to keep adding visited elements into hashmap so that Here instead of
	 * using a loop, we will use the HashMap to check if the other element i.e.
	 * target-(selected element) exists.
	 */
	public int[] twoSum(int[] nums, int target) {

		Map<Integer, Integer> hm = new HashMap<>();

		for (int i = 0; i < nums.length; i++) {
			int x = target - nums[i];
			if (hm.containsKey(x)) {
				return new int[] { hm.get(x), i };
			}

			hm.put(nums[i], i);
		}

		return new int[] { 0, 0 };
	}

	public List<List<Integer>> fourSum(int[] nums, int target) {

		int n = nums.length;

		Set<List<Integer>> st = new HashSet<>();

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				Set<Long> hashset = new HashSet<>();
				for (int k = j + 1; k < n; k++) {
					long sum = nums[i] + nums[j];
					sum += nums[k];

					long last = target - sum;

					if (hashset.contains(last)) {
						List<Integer> temp = new ArrayList<>();
						temp.add(nums[i]);
						temp.add(nums[j]);
						temp.add(nums[k]);
						temp.add((int) last);
						temp.sort(Integer::compareTo);
						st.add(temp);
					}

					hashset.add((long) nums[k]);
				}
			}
		}
		List<List<Integer>> ans = new ArrayList<>(st);
		return ans;
	}

	public int longestConsecutive(int[] nums) {

		if (nums.length == 0)
			return 0;

		Set<Integer> st = new HashSet<>();

		for (int i = 0; i < nums.length; i++) {
			st.add(nums[i]);
		}

		int lcs = 0;
		int cnt = 1;
		for (int x : st) {
			/*
			 * I'll only start counting if I know this is the first element of the sequence
			 * This makes sure that second loop can never go higher than n
			 */
			if (st.contains(x - 1))
				continue;

			while (st.contains(x + 1)) {
				x++;
				cnt++;
			}
			lcs = Math.max(cnt, lcs);
			cnt = 1;
		}

		return lcs;
	}

	public static void main(String[] args) {

		Hashing hashing = new Hashing();

		int[] arr = { 2, 6, 5, 8, 11 };
		int target = 14;

		// O(N) Time and O(N) Space
		// To remove space used, use 2 pointer approach
		int[] ans = hashing.twoSum(arr, target);
		System.out.println("1. Two Sum::");
		System.out.println("Target can be derived by add elements at below index");
		Arrays1.printArray(ans);

		int[] nums = { 4, 3, 3, 4, 4, 2, 1, 2, 1, 1 };
		target = 9;

		List<List<Integer>> anss = hashing.fourSum(nums, target);
		System.out.println("\n\n2. Four SUM::");
		/*
		 * Time Complexity: O(N3*log(M)), where N = size of the array, M = no. of
		 * elements in the set. Reason: Here, we are mainly using 3 nested loops, and
		 * inside the loops there are some operations on the set data structure which
		 * take log(M) time complexity.
		 * 
		 * Space Complexity: O(2 * no. of the quadruplets)+O(N) Reason: we are using a
		 * set data structure and a list to store the quads. This results in the first
		 * term. And the second space is taken by the set data structure we are using to
		 * store the array elements. At most, the set can contain approximately all the
		 * array elements and so the space complexity is O(N).
		 * 
		 * We can again remove space by using two pointer approach
		 */
		System.out.println("The quadruplets(which can add up to get target) are: ");
		System.out.println(anss);

		/*
		 * Time Complexity: O(N) + O(2*N) ~ O(3*N), where N = size of the array. Reason:
		 * O(N) for putting all the elements into the set data structure. After that for
		 * every starting element, we are finding the consecutive elements. Though we
		 * are using nested loops, the set will be traversed at most twice in the worst
		 * case. So, the time complexity is O(2*N) instead of O(N2).
		 * 
		 * Space Complexity: O(N), as we are using the set data structure to solve this
		 * problem.
		 */
		int[] a = { 100, 200, 1, 2, 3, 4 };
		System.out.println("\n\n3. Longest Consecutive Sequence");
		int lcs = hashing.longestConsecutive(a);
		System.out.println("The longest consecutive sequence is " + lcs);

	}
}
