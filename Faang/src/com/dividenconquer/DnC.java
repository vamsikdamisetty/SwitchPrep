package com.dividenconquer;

public class DnC {

	public static void main(String[] args) {

		// O(logn)
		int singleNumber = singleNonDuplicate(new int[] { 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6, 7, 7 });
		System.out.println("\n3. Find the element that appears once in sorted array, and rest element appears twice: \n"
				+ singleNumber);
		
		
		// O(logn)
		int index = searchInRotated(new int[] {4,5,6,7,0,1,2}, 0);
		
		System.out.println("\n4. Search element in a sorted and rotated array.\r\n"
			+ index);
		
		
		// O(log(min(n1,n2)) we are doing binary search over min size array
		double median = findMedianSortedArrays(new int[] {1,2,4,9,12},new int[] {3,8,13,14,15});
		System.out.println("\n6. Median of Two Sorted Arrays");
		System.out.println(median);
		
		// O(log(min(n1,n2)) we are doing binary search over min size array
		int kthElement = findKInSortedArrays(new int[] {1,2,4,9,12},new int[] {3,8,13,14,15}, 10);
		System.out.println("\n5.K-th element of two sorted arrays");
		System.out.println(kthElement);
	}

	public static int singleNonDuplicate(int[] nums) {
		int l = 0, h = nums.length - 2;

		while (l <= h) {
			int m = l + (h - l) / 2;
			if (nums[m] == nums[m ^ 1]) { // XOR 1 will convert even to next odd / odd to prev even
				l = m + 1;
			} else {
				h = m - 1;
			}
		}

		return nums[l];
	}
	
    public static int searchInRotated(int[] nums, int target) {
        
        int l=0,h=nums.length-1;
        int m = (l+h)/2;
        
        while(l<=h){
            if(nums[m] == target)
                return m;
            
            if(nums[l] <= nums[m])
            {
                if(target < nums[m] && target >= nums[l]){
                    h = m-1;
                }else{
                    l = m+1;
                }
            }else{
                if(target > nums[m] && target <= nums[h]){
                    l = m+1;
                }else{
                    h = m-1;
                }
            }
            m = l + (h-l)/2;
        }
        return -1;
    }
    
   public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        
        if(nums2.length < nums1.length) return findMedianSortedArrays(nums2,nums1);
            
        int n1 = nums1.length;
        int n2 = nums2.length;
        
        int l = 0,h = n1;
        
        while(l <= h){
            
            int cut1 = (l + h )/ 2;
            int cut2 = (n1+n2+1)/2 - cut1;
            
            int l1 = cut1 == 0 ? Integer.MIN_VALUE : nums1[cut1-1];
            int l2 = cut2 == 0 ? Integer.MIN_VALUE : nums2[cut2-1];
            
            int r1 = cut1 == n1 ? Integer.MAX_VALUE : nums1[cut1];
            int r2 = cut2 == n2 ? Integer.MAX_VALUE : nums2[cut2];
            
            if(l1 <= r2 && l2 <= r1){
                
                if((n1+n2)%2 == 0){
                    return (Math.max(l1,l2) + Math.min(r1,r2))/2.0;
                }else{
                    return (double)Math.max(l1,l2);
                }
            }else if(l1 > r2){
                h = cut1-1;
            }else{
                l = cut1+1;
            }
        }
        return 0.0;
    }
   
   public static int findKInSortedArrays(int[] nums1, int[] nums2,int k) {
       
       if(nums2.length < nums1.length) return findKInSortedArrays(nums2,nums1,k);
           
       int n1 = nums1.length;
       int n2 = nums2.length;
       
       int l = Math.max(0, k-n2),h = Math.min(k, n1);
       
       while(l <= h){
           
           int cut1 = (l + h )/ 2;
           int cut2 = k - cut1;
           
           int l1 = cut1 == 0 ? Integer.MIN_VALUE : nums1[cut1-1];
           int l2 = cut2 == 0 ? Integer.MIN_VALUE : nums2[cut2-1];
           
           int r1 = cut1 == n1 ? Integer.MAX_VALUE : nums1[cut1];
           int r2 = cut2 == n2 ? Integer.MAX_VALUE : nums2[cut2];
           
           if(l1 <= r2 && l2 <= r1){
               
              return Math.max(l1, l2);
           }else if(l1 > r2){
               h = cut1-1;
           }else{
               l = cut1+1;
           }
       }
       return 0;
   }

}
