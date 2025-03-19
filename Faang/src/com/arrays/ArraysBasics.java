package com.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ArraysBasics {

	public int getSecondLargest(int[] arr) {
		int second = -1;
		int first = arr[0];

		for (int i = 1; i < arr.length; i++) {
			if (first < arr[i]) {
				second = first;
				first = arr[i];
			} else if (second < arr[i] && arr[i] != first) {
				second = arr[i];
			}
		}
		return second;
	}

	int missingNumber(int arr[]) {
		long asum = 0;
		for (int i : arr) {
			asum += i;
		}

		long n = arr.length + 1;
		long sum = n * (n + 1) / 2;
		return (int) (sum - asum);
	}

	ArrayList<Integer> leaders(int arr[]) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		int leader = -1;
		for (int i = arr.length - 1; i >= 0; i--) {
			if (leader <= arr[i]) {
				res.add(0, arr[i]);
				leader = arr[i];
			}
		}

		return res;
	}

	public List<Integer> findDuplicates(int[] arr) {
		Map<Integer, Integer> map = new HashMap<>();

		for (int i : arr) {
			map.put(i, map.getOrDefault(i, 0) + 1);
		}

		List<Integer> res = new ArrayList<>();

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			if (entry.getValue() > 1) {
				res.add(entry.getKey());
			}
		}
		return res;
	}

	public int findEquilibrium(int arr[]) {
		int sumLeft = 0;
		int sumRight = 0;
		int actIndex = 1;

		for (int i = 1; i < arr.length; i++) {
			sumRight += arr[i];
		}

		if (sumRight == 0) {
			return 0;
		}

		while (actIndex < arr.length - 1) {
			sumLeft += arr[actIndex - 1];
			sumRight -= arr[actIndex];

			if (sumLeft == sumRight) {
				return actIndex;
			}

			actIndex++;
		}

		return -1;
	}
	
	public static boolean checkEqual(int[] a, int[] b) {
		if (a.length != b.length) {
			return false; // Arrays must have the same length
		}

		HashMap<Integer, Integer> freqMap = new HashMap<>();

		// Count frequencies of elements in array 'a'
		for (int num : a) {
			freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
		}

		// Compare frequencies with elements in array 'b'
		for (int num : b) {
			if (!freqMap.containsKey(num)) {
				return false; // Element in 'b' is not present in 'a'
			}
			freqMap.put(num, freqMap.get(num) - 1);
			if (freqMap.get(num) == 0) {
				freqMap.remove(num); // Remove key when count reaches 0
			}
		}

		return freqMap.isEmpty(); // If all elements matched, the map should be empty
	}
	
	/*
	 * Given an array arr[] of positive integers and another integer target.
	 * Determine if there exists two distinct indices such that the sum of there
	 * elements is equals to target.
	 */
	boolean twoSum(int arr[], int target) {
        
		Arrays.sort(arr);
		
		int l=0;
		int r=arr.length -1;
		
		while(l<r) {
			int sum = arr[l] + arr[r];
			if(sum == target)
				return true;
						
			if(sum > target) {
				r--;
			}else {
				l++;
			}
		}
		
		return false;
		
    }
	
	public static List<Integer> frequencyCount(int[] arr) {
		
		Integer[] result = new Integer[arr.length];
		
		for (int i = 0; i < arr.length; i++) {
			result[i] = 0;
		}
		
		for (int i = 0; i < arr.length; i++) {
			int x = arr[i];
			result[x-1] = result[x-1] + 1; 
		}
		
		return Arrays.asList(result);
		
	}
	
	public static int firstRepeated(int[] arr) {
        Map<Integer, Boolean> hm = new HashMap<Integer, Boolean>();
        
        int fre=0;
        for(int i=arr.length -1;i>=0;i--) {
        	
        	if(hm.containsKey(arr[i])) {
        		fre = arr[i];
        	}else {
        		hm.put(arr[i], true);
        	}
        		
        }
        return fre;
    }
	
	
	public static void main(String[] args) {
		ArraysBasics arraysBasics = new ArraysBasics();

		System.out.println("1. Find the second largest in the array");

		int secondLargest = arraysBasics.getSecondLargest(new int[] { 1, 6, 7, 13, 55, 22, 1 });
		System.out.println("secondLargest :: " + secondLargest);

		System.out.println("\n\n2. Missing in Array of N natural numbers");

		int missing = arraysBasics.missingNumber(new int[] { 1, 2, 3, 5, 6, 7 });
		System.out.println("Missing :: " + missing);

		System.out.println("\n\n3. Array Leaders");
		/*
		 * You are given an array arr of positive integers. Your task is to find all the
		 * leaders in the array. An element is considered a leader if it is greater than
		 * or equal to all elements to its right. The rightmost element is always a
		 * leader.
		 */
		ArrayList<Integer> leaders = arraysBasics.leaders(new int[] { 16, 17, 4, 3, 5, 2 });
		System.out.println("leaders :: " + leaders);

		System.out.println("\n\n4. Array Duplicates");
		/*
		 * find all the elements that occur more than once in the array
		 */
		List<Integer> dup = arraysBasics.findDuplicates(new int[] { 2, 3, 1, 2, 3 });
		System.out.println("Duplicates :: " + dup);

		System.out.println("\n\n5. equilibrium point");
		/*
		 * Given an array of integers arr[], the task is to find the first equilibrium
		 * point in the array.
		 * 
		 * The equilibrium point in an array is an index (0-based indexing) such that
		 * the sum of all elements before that index is the same as the sum of elements
		 * after it. Return -1 if no such point exists.
		 */
		int ep = arraysBasics.findEquilibrium(new int[] { 2, 3, 1, 2, 3 });
		System.out.println(" equilibrium point:: " + ep);
	
		System.out.println("\n6.Check Equal Arrays");
		/*
		 * Given two arrays a[] and b[] of equal size, the task is to find whether the
		 * elements in the arrays are equal.
		 * 
		 * Two arrays are said to be equal if both contain the same set of elements,
		 * arrangements (or permutations) of elements may be different though.
		 * 
		 * Note: If there are repetitions, then counts of repeated elements must also be
		 * the same for two arrays to be equal.
		 */
		boolean ea = checkEqual(new int[] {1,2,2,5,1,6}, new int[]{6,5,2,1,1,2});
		System.out.println("Arrays Equal : "+ ea);
		
		System.out.println("\n7. Frequency count:");
		System.out.println(frequencyCount(new int[] {2,3,2,3,5}));
		
		
		
	}
}
