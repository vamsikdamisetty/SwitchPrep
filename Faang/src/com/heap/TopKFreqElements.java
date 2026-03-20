package com.heap;

import com.arrays.Arrays1;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKFreqElements {
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer,Integer> freq = new HashMap<>();

        for(int i:nums){
            freq.put(i,freq.getOrDefault(i,0) + 1);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a,b) -> freq.get(a) - freq.get(b));


        for(Map.Entry<Integer,Integer> entry:freq.entrySet()){
            minHeap.add(entry.getKey());

            if(minHeap.size() > k){
                minHeap.poll();
            }
        }

        int[] res = new int[k];
        int i=0;
        
        while(!minHeap.isEmpty()){
            res[i++] = minHeap.poll();
        }
        return res;
    }
    public static void main(String[] args) {
        int[] nums = new int[]{1,1,1,2,2,3};
        int k =2;

        System.out.println("Top K frequent elements:");

        Arrays1.printArray(topKFrequent(nums,k));

    }
}
