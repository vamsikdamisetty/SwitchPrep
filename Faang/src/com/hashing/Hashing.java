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

	/*
	 * intuition We keep the prefix sum and its index in HashMap if at any index i
	 * sum is x and it is in HashMap Subarray length is i - hm.get(x); using this we
	 * calculate the max subarray length with sum 0
	 */
	int maxLenSubarray(int arr[]) {
		// code here
		int sum = 0;
		int maxi = 0;
		Map<Integer, Integer> hm = new HashMap<>();

		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
			if (sum == 0) {
				maxi = i + 1;
			} else {
				if (hm.containsKey(sum)) {
					maxi = Math.max(maxi, i - hm.get(sum));
				} else {
					hm.put(sum, i);
				}
			}
		}
		return maxi;
	}

	/*
	 * Intuition How many sub arrays will end at index i with sum = k ? No of sub
	 * arrays that had sum -> (curr_sum-k) before the index i
	 */
	public int findAllSubarraysWithGivenSum(int arr[], int k) {
		int n = arr.length; // size of the given array.
		Map<Integer, Integer> mpp = new HashMap();
		int preSum = 0, cnt = 0;

		mpp.put(0, 1); // Setting 0 in the map.
		for (int i = 0; i < n; i++) {
			// add current element to prefix Sum:
			preSum += arr[i];

			// Calculate x-k:
			int remove = preSum - k;

			// Add the number of subarrays to be removed:
			cnt += mpp.getOrDefault(remove, 0);

			// Update the count of prefix sum
			// in the map.
			mpp.put(preSum, mpp.getOrDefault(preSum, 0) + 1);
		}
		return cnt;
	}

	/*
	 * Intuition How many sub arrays will end at index i with XOR = k ? No of sub
	 * arrays that had XOR -> (curr_sum XOR k) before the index i
	 */
	public int subarraysWithXorK(int[] a, int k) {
		int n = a.length; // size of the given array.
		int xr = 0;
		Map<Integer, Integer> mpp = new HashMap<>(); // declaring the map.
		mpp.put(0, 1); // setting the value of 0.
		int cnt = 0;

		for (int i = 0; i < n; i++) {
			// prefix XOR till index i:
			xr = xr ^ a[i];

			/*
			 * x XOR k = xr (x XOR k) XOR k = xr XOR k As both k's cancel as XOR of same
			 * elements is 0 x = xr XOR k
			 */
			// By formula: x = xr^k:
			int x = xr ^ k;

			// add the occurrence of xr^k
			// to the count:
			cnt += mpp.getOrDefault(x, 0);

			// Insert the prefix xor till index i
			// into the map:
			mpp.put(xr, mpp.getOrDefault(xr, 0) + 1);

		}
		return cnt;
	}

	/*
	 * intuition If we can keep track of last index of repeated char We can check
	 * the max length
	 */
	public int lengthOfLongestSubstring(String s) {

		Map<Character, Integer> mp = new HashMap<>();
		int lls = 0;
		int l = 0, r = 0;

		while (r < s.length()) {
			char c = s.charAt(r);

			if (mp.containsKey(c)) {
				// move l if the repeating character is inside the current window
				// ex abcdba imagine you are at 2nd 'a' even though it's repeating but your l in
				// window has already reached c
				l = Math.max(l, mp.get(c) + 1);
			}

			mp.put(c, r);

			// Upgate the length based on current window size
			lls = Math.max(lls, r - l + 1);
			r++;

		}

		return lls;
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

		/*
		 * Time Complexity: O(N), as we are traversing the array only once
		 * 
		 * Space Complexity: O(N), in the worst case we would insert all array elements
		 * prefix sum into our hashmap
		 */
		a = new int[] { 9, -3, 3, -1, 6, -5 };
		System.out.print("\n\n4. Longest Subarray with 0 sum is");
		int lsarrayLen = hashing.maxLenSubarray(a);
		System.out.println(" " + lsarrayLen);

		/*
		 * Time Complexity: O(N), as we are traversing the array only once
		 * 
		 * Space Complexity: O(N), in the worst case we would insert all array elements
		 * prefix sum into our hashmap
		 */
		arr = new int[] { 3, 1, 2, 4 };
		int k = 6;
		int cnt = hashing.findAllSubarraysWithGivenSum(arr, k);
		System.out.println("\n\nThe number of subarrays with given sum is: " + cnt);

		/*
		 * 
		 * Similar solution to above (Dry RUN MUST)
		 * 
		 * Time Complexity: O(N), as we are traversing the array only once
		 * 
		 * Space Complexity: O(N), in the worst case we would insert all array elements
		 * prefix sum into our hashmap
		 */
		arr = new int[] { 4, 2, 2, 6, 4 };
		k = 6;
		cnt = hashing.subarraysWithXorK(arr, k);
		System.out.println("\n\n5. The number of subarrays with XOR k is: " + cnt);

		
		/*
		 * Main difference from above problem is we need to maintain a window here
		 * Time Complexity: O( N )
		 * 
		 * Space Complexity: O(N) where N represents the size of HashSet where we are
		 * storing our elements
		 * 
		 * 
		 */
		String str = "takeUforward";
		System.out.println("\n\n6. The length of the longest substring without repeating characters is " +  hashing.lengthOfLongestSubstring(str));
	}
}
