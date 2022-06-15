package com.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arrays1 {

    public int findDuplicate(int[] nums) {
        int slow = 0;
        int fast = 0;
        
        do{
            slow = nums[slow];
            fast = nums[nums[fast]];
        }while(slow != fast);
        
        fast = 0;
        
        do{
            slow = nums[slow];
            fast = nums[fast];
        }while(slow != fast);
        
        return slow;
    }
    
    public void sortColors(int[] nums) {
        
        int low = 0;
        int mid = 0;
        int high = nums.length-1;
        
        while(mid <= high){
            if(nums[mid] == 0){
            	swapInArray(nums,mid,low);
                low++;
                mid++;
            }else if (nums[mid] == 1){
                mid++;
            }else{
            	swapInArray(nums,mid,high);
                high--;
            }
        }
        printArray(nums);
    }
    
    public void sortColors2(int[] nums) {
        
        int low = 0;
        int mid = 0;
        int high = nums.length-1;
        
        while(mid <= high){
            if(nums[mid] == 0){
                nums[mid] = nums[low];
                nums[low] = 0;
                low++;
            }
            
            if (nums[mid] == 2){
                nums[mid] = nums[high];
                nums[high] = 2;
                high--;
                mid--;
            }
            
            mid++;
        }
    }
    
    void swapInArray(int[] nums,int i1,int i2){
        int temp = nums[i2];
        nums[i2] = nums[i1];
        nums[i1] = temp;
    }
    
    void printArray(int[] arr) {
    	for(int a:arr) {
    		System.out.print(a + " ");
    	}
    }
    
    public int missingRepeatingNumber(int[] nums) {
        //https://takeuforward.org/data-structure/find-the-repeating-and-missing-numbers/ 
    	//solution 2 Maths approach 
    	// could have used a boolean array
    	long len = nums.length;
        
        //corner case for missing only problem
        if(len == 1){
            if(nums[0] == 0){
                return 1;
            }
        }
        int missing,repeating;
        
        long s = (len * (len + 1))/2; //sum of all elements
        long p = (len * (len + 1) * (2*len + 1))/6; //sum of squares of all elements
        
        for(int i=0;i< len ; i++){
           s -= nums[i];
           p -= nums[i] * nums[i];
        }
        // now p -> m^2 - r^2;  s -> m - r 
        
        //corner case for missing only problem
        if(s == 0 || p == 0){
            return 0;
        }
            
        long l = p/s; //
        missing = (int)((s + l)/2);
        repeating = (int)(l - missing);
        System.out.println("Repeating : " + repeating);
        return missing;
    }
    
    public void missingRepeatingNumberCount(int[] nums) {
    	System.out.println("Count Approach using extra array");
    	
    	int[] count = new int[nums.length + 1];
    	
    	for(int i=0;i<nums.length;i++) {
    		count[nums[i]] = count[nums[i]] + 1; 
    	}
    	
    	for(int i=1;i<count.length;i++) {
    		if(count[i] == 0) {
    			System.out.println("Missing :"  + i);
    		}
    		if(count[i] == 2) {
    			System.out.println("Repeating :"  + i);
    		}
    	}
    }
    
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m-1;
        int j = n-1;
        int k = m+n-1;
        
        while(i != -1 && j != -1){
            if(nums1[i] > nums2[j]){
                nums1[k] = nums1[i];
                i--;
                k--;
            }else{
                nums1[k] = nums2[j];
                j--;
                k--;
            }
        }
        
        while(j != -1){
            nums1[k] = nums2[j];
            j--;
            k--;
        }
        
        printArray(nums1);
    }
    
    public void merge2(int[] nums1, int m, int[] nums2, int n) {
    	
    	for(int i=0;i<m;i++) {
    		if(nums2[0] < nums1[i]) {
    			int temp = nums2[0];
    			nums2[0] = nums1[i];
    			nums1[i] = temp;
    			
    			
    			
    			int first = nums2[0];
    			int  k;
    			for(k = 1 ;k < n && first > nums2[k]; k++) {
    				nums2[k-1] = nums2[k];
    			}
    			nums2[k-1] = first;
    		}
    	}
    	
    	printArray(nums1);
    	System.out.print("   ");
    	printArray(nums2);
    }
    
    public int maxSubArray(int[] nums) {
        int sum =0;
        int maxi = Integer.MIN_VALUE;
        int start = 0;
        int end = 0;
        for(int i=0;i<nums.length;i++){
            sum += nums[i];
            if(sum > maxi) {
                maxi = sum;
                end = i;
            }
            if(sum < 0){
                sum = 0;
                start = i+1;
            }
        }
        System.out.println("Start = " + start + "\nEnd = "+ end);
        return maxi;    
    }
    
    public int[][] mergeSubintervals(int[][] intervals) {
        List<int[]> res = new ArrayList<>();
        printIntervals(intervals);
        if(intervals.length == 0 || intervals == null) 
            return res.toArray(new int[0][]);
        
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        printIntervals(intervals);
        int start = intervals[0][0];
        int end = intervals[0][1];
        
        for(int[] i : intervals) {
            if(i[0] <= end) {
                end = Math.max(end, i[1]);
            }
            else {
                res.add(new int[]{start, end});
                start = i[0];
                end = i[1];
            }
        }
        res.add(new int[]{start, end});
        
        printIntervals(res.toArray(new int[0][]));
       return res.toArray(new int[0][]);
    }
    
    void printIntervals(int[][] intervals) {
    	for(int[] i: intervals) {
    		System.out.print(i[0] + "  " + i[1] + "||");
    	}
    	System.out.println();
    }
    
	public static void main(String[] args) {
		Arrays1 arrays = new Arrays1();
		System.out.println("1. Find the duplicate in an array of N integers");  //O(n)
		// we can use hashing for O(n),O(n)  , But below methd is O(n) Linked list cycle method
		System.out.println("Duplicate element:" +arrays.findDuplicate(new int[]{1,3,4,2,5,8,1,7,6}));
		
		System.out.println("\n2. Sort an array of 0’s 1’s 2’s without using extra space or sorting algo");
		//can use counting for O(N) + O(N) but below is optimised
		arrays.sortColors(new int[] {2,0,2,1,1,0}); //O(n)
		
		System.out.println("\n\n3. Repeat and Missing Number");//O(n)
		System.out.println("Missing :" + arrays.missingRepeatingNumber(new int[] {3,1,2,5,6,7,3}));
		arrays.missingRepeatingNumberCount(new int[] {3,1,2,5,6,7,3});//O(n),O(n)
		
		/* Merge two sorted array leetcode
		Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
				Output: [1,2,2,3,5,6]*/
		System.out.println("\n4. Merge two sorted array leetcode");
		arrays.merge(new int[] {1,2,3,0,0,0}, 3, new int[] {2,5,6}, 3);
		System.out.println("\n4. Merge two sorted array No extra space in first array");
		// O(m*n)
		arrays.merge2(new int[] {1,4,7,8,10}, 5, new int[] {2,3,9}, 3);
		
		System.out.println("\n\n5. Kadane's Algo :");
		System.out.println(arrays.maxSubArray(new int[] {-2,1,-3,4,-1,2,1,-5,4}));
		
		System.out.println("\n\n6. Merge Overlapping Subintervals");
		int[][] intervals = new int[8][];
		intervals[0] = new int[]{1,3};
		intervals[1] = new int[]{2,4};
		intervals[2] = new int[]{2,6};
		intervals[3] = new int[]{9,11};
		intervals[4] = new int[]{15,18};
		intervals[5] = new int[]{16,17};
		intervals[6] = new int[]{8,9};
		intervals[7] = new int[]{8,10};

		arrays.mergeSubintervals(intervals);
		
	}
}
