package com.heap;

import java.util.PriorityQueue;

public class KthLargestElement {

    /*
        1️⃣ Min Heap (Most Common Interview Answer)
        Idea

        Maintain a min heap of size k.

        Add elements to heap.

        If heap size > k → remove smallest.

        After processing all elements, heap top = kth largest.

        Time Complexity
        O(N log K)
     */
    public static int findKthLargest(int[] nums, int k) {

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for(int i:nums){
            minHeap.add(i);

            if(minHeap.size() > k){
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }

    public static void main(String[] args) {
        int[] nums = new int[]{12,1,7,3,5,6,9,10};
        //1,3,5,6,7,8,10,12
        int k=4; //find 4th largest element;


        System.out.println("kth Largest Element:: " + findKthLargest(nums,4));

    }
}
