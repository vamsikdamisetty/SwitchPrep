package com.greedy;

import java.util.ArrayList;
import java.util.List;

import com.arrays.Arrays1;
import com.arrays.Arrays2;

public class InsertInterval {

	public static void main(String[] args) {
		int[][] intervals = {
			    {1, 2},
			    {3, 5},
			    {6, 7},
			    {8, 10},
			    {12, 16}
			};
		
		int[] newInterval = {4,8};
		
		System.out.println("Current Intervals");
		Arrays2.printMatrix(intervals);
		System.out.println("New Interval to insert");
		Arrays1.printArray(newInterval);
		
		int[][] res = insert(intervals, newInterval);
		
		System.out.println("\n after inserting:");
		Arrays2.printMatrix(res);
	}
	
	/*
	 * Intuition is to divide the problem into 3 portions
	 * Left -> Where intervals are not overlapped with new
	 * Middle -> Overlapped One's
	 * Right -> Intervals after all the overlapped are checked  
	 * 
	 * Time -> O(n)
	 * Space -> O(n)
	 */
	public static int[][] insert(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        List<int[]> res = new ArrayList<>();

        int index = 0;
        
        /*
         * Till the interval end is < new interval's start
         */
        while(index < n && intervals[index][1] < newInterval[0]){
             res.add(intervals[index]);
            
            index++;
        }

        /*
         * Out of all the overlapping intervals
         * find the min start and max end
         */
        while(index < n && intervals[index][0] <= newInterval[1]){
            newInterval[0] = Math.min(newInterval[0], intervals[index][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[index][1]);
            index++;
        }

        /*
         *Insert min start and max end as new interval 
         */
        res.add(newInterval);
        
        /*
         * Just fill the right portion
         */
        while(index < n ){
            res.add(intervals[index]);
            index++;
        }

        
        return res.toArray(new int[res.size()][]);
    }
}
