package com.twopointer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TwoPointer {

	public static List<List<Integer>> threeSum(int[] nums) {
		//Sorting the array to apply 2 pointer
		Arrays.sort(nums);

		List<List<Integer>> triplets = new LinkedList<>();

		for (int i = 0; i < nums.length - 2; i++) {

			if (i == 0 || nums[i] != nums[i - 1]) {

				int low = i + 1, high = nums.length - 1, sum = 0 - nums[i];

				while (low < high) {
					if (nums[low] + nums[high] == sum) {

						triplets.add(Arrays.asList(nums[i], nums[low], nums[high]));

						while (low < high && nums[low] == nums[low + 1])
							low++;

						while (low < high && nums[high] == nums[high - 1])
							high--;

						low++;
						high--;

					} else if (nums[low] + nums[high] < sum) {
						low++;
					} else {
						high--;
					}
				}
			}
		}
		return triplets;
	}
	
	public int removeDuplicates(int[] nums) {

		if(nums == null || nums.length == 0) return 0;
		
		int low = 0, itr = 1;

		while (itr != nums.length) {

			if (nums[itr] != nums[low]) {
				nums[++low] = nums[itr];
				if(itr != low) nums[itr] = 0; //not mandatory
			} else {
				nums[itr] = 0; //not mandatory
			}

			itr++;
		}
		return ++low;
	}
	
	public void printArray(int[] a) {
		for(int i:a) {
			System.out.print(i + " ");
		}
	}
	
    public int findMaxConsecutiveOnes(int[] nums) {
        
        int c = 0; 
        int max = 0;
        
        for(int j=0;j<nums.length;j++){
            
            if(nums[j] == 1){
                c++;
            }
            else{
                max = Math.max(max,c);
                c = 0;
                
            }
            
        }
        
        return  max = Math.max(max,c);
    }
    
    public int trap1(int[] height) {
        
        int n = height.length;
        int[] pMax = new int[n];
        int[] sMax = new int[n];
        int maxi = Integer.MIN_VALUE;
        
        /*
         * finding prefix max
         */
        for(int i=0;i<n;i++){
            maxi = Math.max(maxi,height[i]);
            pMax[i] = maxi;
        }
        
        maxi = Integer.MIN_VALUE;
        /*
         * finding suffix max
         */
        for(int i=n-1;i>=0;i--){
            maxi = Math.max(maxi,height[i]);
            sMax[i] = maxi;
        }
        
        int water = 0;
        for(int i=0;i<n;i++){
            water += Math.min(sMax[i],pMax[i]) - height[i];
        }
        
        return water;
    }
    
 public int trapOptimal(int[] height) {
        
        int lMax = 0;
        int rMax = 0;
        int water = 0;
        
        int l = 0,r = height.length - 1;
        
        while(l < r){
        	//We will only proceed the side for which we are sure there is a elevation on other side to hold water
            if(height[l] <= height[r]){
            	//we know right side has bigger elevation, go for lmax
                if(height[l] < lMax){
                    water += lMax - height[l];
                }else{
                    lMax = height[l];
                }
                l++;
            }else{
                if(height[r] < rMax){
                    water += rMax - height[r];
                }else{
                    rMax = height[r];
                }
                r--;
            }
        }
     return water;   
    }
    
	public static void main(String[] args) {
		
		TwoPointer tp = new TwoPointer();
		
		System.out.println("3. 3 sum");  //O(n^2) + O(nlogn) = O(n^2)  space : O(1)
		System.out.println("List of Triplets: " + threeSum(new int[] {-1,0,1,2,-1,-4}));
	
		System.out.println("\n\n5. Remove Duplicate from Sorted array\r\n"); //O(n) single pass
		int a[] = {1,1,2,2,33,33,44,44,89,89,90,90,100};
		System.out.println("Count of Duplicates" +  tp.removeDuplicates(a));
		tp.printArray(a);
		tp.removeDuplicates(null);
		
		System.out.println("\n\n6. Max continuous number of 1�s"); //O(1);
		System.out.println("Max consecutive 1's : " + tp.findMaxConsecutiveOnes(new int[] {1,1,0,1,1,1}));
		
		int height[] = {0,1,0,2,1,0,1,3,2,1,2,1};
		System.out.println("\n\n4. Trapping rainwater");
		System.out.println("Max Water trapped : " + tp.trap1(height));  //O(n) space O(2n) Not the optimal
		
		//Now lets do two pointer optimal sol O(n) , O(1)
		System.out.println("Optimal - Max Water trapped : " + tp.trapOptimal(height)); 
	}
}
