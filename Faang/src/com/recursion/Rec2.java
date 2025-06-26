package com.recursion;

import java.util.ArrayList;
import java.util.List;

public class Rec2 {

	/*
	 * WE KNOW THE NUMBER OF SUBSETS FOR A SET FORMULA 2^n -1
	 */
	public static void main(String[] args) {

		Rec2 r2 = new Rec2();

		// Contiguous or Non-contiguous group of elements without loosing the order is a
		// subsequence
		System.out.println("String Subsequences : "); // O(2^n) because 2 operations every time,
		// Space : O(n) as depth of rec tree would be n at max
		r2.subseqStr("abc", "");

		System.out.println("\n\nArray Subsequences : ");
		// O(2^n * n(for printing arraylist evertime)) because 2 operations every time,
		// Space : O(n) as depth of rec tree would be n at max
		int[] a = { 1, 2, 3 };
		ArrayList<Integer> ll = new ArrayList<>();

		r2.subseqIntArr(a, 0, 3, ll);

		// Skip this.. Not a clean approach
		ll = new ArrayList<>();
		r2.subseqIntArr2(a, 0, 3, ll);

		int[] a2 = { 1, 2, 9, 3, 4, 5, 6 };
		ArrayList<Integer> ll3 = new ArrayList<>();
		System.out.println("\n\nStop when any subseq with sum == k is found: ");

		System.out.println(r2.subseqIntArrBool(a2, 0, 7, 5, ll3, 0));
		ArrayList<Integer> ll2 = new ArrayList<>();

		System.out.println("\n\nCount of subsequences with sum k : ");

		System.out.println("Count : " + r2.printSubsequencesWithSum(a2, 0, 7, ll2, 0));

		List<List<Integer>> res = new ArrayList<>();

		System.out.println("\n\nCombinations 1 :"); // pick and not pick pattern
		// O(2^t * k) where t is the target, k is the average length
		// Assume if you were not allowed to pick a single element multiple times, every
		// element will have a couple of options: pick or not pick which is 2^n
		// different recursion calls, also assuming that the average length of every
		// combination generated is k. (to put length k data structure into another data
		// structure)
		// space O(x*k) x-> no of combinations (Ignoring aux space for recursion)
		r2.combinations(new int[] { 2, 3, 6, 7 }, 0, 7, new ArrayList<>(), res);
		System.out.println(res);

		res = new ArrayList<>();
		r2.combinations1(new int[] { 2, 3, 6, 7 }, 0, 7, new ArrayList<>(), res);
		System.out.println(res);

		System.out.println("\n\nCombinations 2 : "); // Here it is about selecting
		// O(2^n * k) k->avg size of combination as we create list out of it evertime
		// O(x*k) x-> no of combinations
		List<List<Integer>> ress = new ArrayList<>();

		r2.combinations2(new int[] { 1, 1, 2, 5, 6, 7, 10 }, 0, 8, new ArrayList<>(), ress);
		System.out.println(ress);

	}

	/*
	 * THis expects a Sorted array to avoid duplicate answers
	 */
	void combinations2(int[] candidates, int i, int target, List<Integer> ds, List<List<Integer>> res) {

		if (target == 0) {
			res.add(new ArrayList<>(ds));
			return;
		}

		/*
		 * Index Selection Approach
		 * This runs on a logic that at a position I can choose any of the elements as
		 * my starting index For ex: [1,1,2,5,6,10] Form 0th index I can choose any of
		 * the index till end to try forming solution except repetitive elements to
		 * avoid duplication
		 * 
		 */
		for (int j = i; j < candidates.length; j++) {
			if (target - candidates[j] < 0)
				return;// we are returning because array is sorted
			// J == I because ignore for first and rest skip , Hence no diplicate
			// combinations (Only once that combination witll be considered)
			if (j == i || candidates[j] != candidates[j - 1]) {
				ds.add(candidates[j]);
				combinations2(candidates, j + 1, target - candidates[j], ds, res);
				ds.remove(ds.size() - 1);
			}
		}
	}

	void combinations1(int[] candidates, int i, int target, List<Integer> ds, List<List<Integer>> res) {

		if (target == 0) {
			res.add(new ArrayList<>(ds));
			return;
		}

		/*
		 * It starts from index i in the loop and allows j = i again → meaning you can
		 * pick the same element multiple times.
		 */
		for (int j = i; j < candidates.length; j++) {
			if (candidates[j] <= target) {
				ds.add(candidates[j]);
				combinations1(candidates, j, target - candidates[j], ds, res);
				ds.remove(ds.size() - 1);
			}
		}
	}

	void combinations(int[] candidates, int i, int target, List<Integer> ds, List<List<Integer>> res) {

		if (target == 0) {
			res.add(new ArrayList<>(ds));
			return;
		}

		if (i == candidates.length) {
			return;
		}

		if (candidates[i] <= target) {
			ds.add(candidates[i]);
			combinations(candidates, i, target - candidates[i], ds, res);
			ds.remove(ds.size() - 1);
		}

		combinations(candidates, i + 1, target, ds, res);
	}

	/*
	 * This is how we count in recursions
	 * 
	 * All kinds of patterns https://www.youtube.com/watch?v=eQCS_v3bw0Q&list=
	 * PLgUwDviBIf0rGlzIn_7rsaR2FQ5e6ZOL9&index=7
	 */
	int printSubsequencesWithSum(int[] arr, int index, int targetSum, ArrayList<Integer> current, int currentSum) {
		if (index == arr.length) {
			if (currentSum == targetSum) {
				current.forEach(e -> System.out.print(e + " "));
				System.out.println();
				return 1;
			}
			return 0;
		}

		// Include current element
		current.add(arr[index]);
		int countWith = printSubsequencesWithSum(arr, index + 1, targetSum, current, currentSum + arr[index]);

		// Exclude current element
		current.remove(current.size() - 1);
		int countWithout = printSubsequencesWithSum(arr, index + 1, targetSum, current, currentSum);

		return countWith + countWithout;
	}

	/*
	 * THis is how we return boolean values
	 */
	boolean subseqIntArrBool(int[] a, int i, int n, int k, ArrayList<Integer> ll, int sum) {

		if (i == n) {
			if (sum == k) {
				ll.forEach(e -> System.out.print(e + " "));
				System.out.println();
				return true;
			}
			return false;
		}

		int x = a[i];
		sum += x;
		ll.add(x);
		if (subseqIntArrBool(a, i + 1, n, k, ll, sum) == true)
			return true;

		ll.remove(ll.size() - 1);
		sum -= x;
		if (subseqIntArrBool(a, i + 1, n, k, ll, sum) == true)
			return true;

		return false;
	}

	void subseqIntArr(int[] a, int i, int n, ArrayList<Integer> ll) {

		if (i == n) {
			if (ll.isEmpty()) {
				System.out.println("Empty");
			} else {
				ll.forEach(e -> System.out.print(e + " "));
				System.out.println();
			}
			return;
		}

		int x = a[i];
		ll.add(x); // take
		subseqIntArr(a, i + 1, n, ll);
		ll.remove(ll.size() - 1); // Not take
		subseqIntArr(a, i + 1, n, ll);
	}

	/*
	 * First method is cleaner Don't know why I used this
	 */
	void subseqIntArr2(int[] a, int i, int n, ArrayList<Integer> ll) {

		if (i == n) {
			if (ll.isEmpty()) {
				System.out.println("Empty");
			} else {
				ll.forEach(e -> System.out.print(e + " "));
				System.out.println();
			}
			return;
		}
		int j = i;
		for (; j < n; j++) {
			int x = a[j];
			ll.add(x);
			subseqIntArr2(a, j + 1, n, ll);
			ll.remove(ll.size() - 1);
		}
		subseqIntArr2(a, j, n, ll);
	}

	/*
	 * Intuition is to take and not take elements ex: In abc if I have to print ac I
	 * take a, not take b, take c
	 */
	void subseqStr(String s, String osf) {
		if (s.length() == 0) {
			if (osf != "")
				System.out.print(osf + " ");
			else
				System.out.println("Empty");
			return;
		}

		char c = s.charAt(0);
		subseqStr(s.substring(1), osf + c); // Take
		subseqStr(s.substring(1), osf); // Not Take
	}
}
