package com.twopointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TwoPointer {

	public static List<List<Integer>> threeSum(int[] nums) {
		// Sorting the array to apply 2 pointer
		Arrays.sort(nums);

		List<List<Integer>> triplets = new LinkedList<>();

		for (int i = 0; i < nums.length - 2; i++) {

			// Avoiding duplicates by moving i to new elements as we would have already made
			// triplets with it
			if (i != 0 && nums[i] == nums[i - 1])
				continue;

			int j = i + 1, k = nums.length - 1;

			// fixing nums[i] and moving j,k to make triplets
			while (j < k) {
				int sum = nums[i] + nums[j] + nums[k];
				/*
				 * if sum is less than 0 we need increase the value hence moving j++ if sum is
				 * greater than 0 we need decrease the value hence moving k--
				 */
				if (sum < 0) {
					j++;
				} else if (sum > 0) {
					k--;
				} else {
					List<Integer> triplet = Arrays.asList(nums[i], nums[j], nums[k]);
					triplets.add(triplet);
					j++;
					k--;
					// Avoiding duplicates by moving j,k to new elements as we already made triplets
					while (j < k && nums[j] == nums[j - 1])
						j++;
					while (j < k && nums[k] == nums[k + 1])
						k--;
				}
			}
		}
		return triplets;
	}
	
	/*
	 * Similar to 3Sum two pointer approach
	 */
	public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        int n=nums.length;
        List<List<Integer>> res = new ArrayList<>();
        for(int i=0;i < n;i++){
            if(i!=0 && nums[i] == nums[i-1]) continue;

            for(int f=i+1;f < n;f++){
            	//i+1 as we don't want to consider for 1st element of the loop
                if(f!=i+1 && nums[f] == nums[f-1]) continue;

                int j = f+1;
                int k = n-1;

                while(j<k){
                	//long and separate sum to handle integer overflow
                    long sum = nums[i] + nums[f];
                    sum += nums[j];
                    sum += nums[k];
                    if(sum < target){
                        j++;
                    } else if(sum > target){
                        k--;
                    } else{
                        List<Integer> triplet = Arrays.asList(nums[i],nums[f],nums[j],nums[k]);
                        res.add(triplet);
                        j++;k--;
                        while(j<k && nums[j] == nums[j-1]) j++;
                        while(j<k && nums[k] == nums[k+1]) k--;
                    }
                }
            }
        }
        return res;
    }

	public int removeDuplicates(int[] nums) {

		if (nums == null || nums.length == 0)
			return 0;

		int low = 0, itr = 1;

		while (itr != nums.length) {

			if (nums[itr] != nums[low]) {
				nums[++low] = nums[itr];
				if (itr != low)
					nums[itr] = 0; // not mandatory
			} else {
				nums[itr] = 0; // not mandatory
			}

			itr++;
		}
		return ++low;
	}

	public void printArray(int[] a) {
		for (int i : a) {
			System.out.print(i + " ");
		}
	}

	public int findMaxConsecutiveOnes(int[] nums) {

		int c = 0;
		int max = 0;

		for (int j = 0; j < nums.length; j++) {

			if (nums[j] == 1) {
				c++;
			} else {
				max = Math.max(max, c);
				c = 0;

			}

		}

		return max = Math.max(max, c);
	}

	/*
	 * Intuition behind this Amount of water that can be trapped in current index i
	 * is min(left maximum bar,right maximum bar) - arr[i]
	 * 
	 * In brute force we traverse in both the sides to calculate left max,right max
	 * O(n^2) In below aprroach we are finding prefix max and suffix max to avoid
	 * that O(3n) and space O(2n)
	 */
	public int trap1(int[] height) {

		int n = height.length;
		int[] pMax = new int[n];
		int[] sMax = new int[n];
		int maxi = Integer.MIN_VALUE;

		/*
		 * finding prefix max
		 */
		for (int i = 0; i < n; i++) {
			maxi = Math.max(maxi, height[i]);
			pMax[i] = maxi;
		}

		maxi = Integer.MIN_VALUE;
		/*
		 * finding suffix max
		 */
		for (int i = n - 1; i >= 0; i--) {
			maxi = Math.max(maxi, height[i]);
			sMax[i] = maxi;
		}

		int water = 0;
		for (int i = 0; i < n; i++) {
			water += Math.min(sMax[i], pMax[i]) - height[i];
		}

		return water;
	}
	
	/*
	 * Using two pointer No extra space
	 * Intuition:
	 * I always want to hold the bigger pillar on one side
	 * So that I can start finding other side max to find current water trapped
	 * 
	 * ex: When I can make sure right has bigger pillar and I know 
	 * if I can find element lesser than lmax, I can find out current water trapped
	 * 
	 */
	public int trapOptimal(int[] height) {

		int lMax = 0;
		int rMax = 0;
		int water = 0;

		int l = 0, r = height.length - 1;

		while (l < r) {
			// We will only proceed the side for which we are sure there is a elevation on
			// other side to hold water
			// remember,we always want the minumum of max, and here we are moving towards min side
			if (height[l] <= height[r]) {
				// we know right side has bigger elevation, go for lmax
				if (height[l] < lMax) {
					water += lMax - height[l];
				} else {
					lMax = height[l];
				}
				l++;
			} else {
				if (height[r] < rMax) {
					water += rMax - height[r];
				} else {
					rMax = height[r];
				}
				r--;
			}
		}
		return water;
	}

	public static void main(String[] args) {

		TwoPointer tp = new TwoPointer();

		/*
		 * Below is the optimal 2 pointer approach But the better than brute force
		 * approach is Using HashSet Look at the link or video
		 * https://takeuforward.org/data-structure/3-sum-find-triplets-that-add-up-to-a-
		 * zero/
		 */
		System.out.println("3. 3 sum"); // O(n^2) + O(nlogn) = O(n^2) space : O(1)
		System.out.println("List of Triplets: " + threeSum(new int[] { -1, 0, 1, 2, -1, -4 }));

		System.out.println("\n\n5. Remove Duplicate from Sorted array\r\n"); // O(n) single pass
		int a[] = { 1, 1, 2, 2, 33, 33, 44, 44, 89, 89, 90, 90, 100 };
		System.out.println("Count of Duplicates" + tp.removeDuplicates(a));
		tp.printArray(a);
		tp.removeDuplicates(null);

		System.out.println("\n\n6. Max continuous number of 1�s"); // O(1);
		System.out.println("Max consecutive 1's : " + tp.findMaxConsecutiveOnes(new int[] { 1, 1, 0, 1, 1, 1 }));

		int height[] = { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 };
		System.out.println("\n\n4. Trapping rainwater");
		System.out.println("Max Water trapped : " + tp.trap1(height)); // Time O(3n) space O(2n) Not the optimal

		// Now lets do two pointer optimal sol O(n) , O(1)
		System.out.println("Optimal - Max Water trapped : " + tp.trapOptimal(height));
	}
}
