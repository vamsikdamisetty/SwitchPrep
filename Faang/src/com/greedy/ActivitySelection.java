package com.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TC: O(nlogn) SC: O(n)
public class ActivitySelection {

	public static void main(String[] args) {
		int[] start = new int[] {1, 3, 2, 5};
		int[] end = new int[] {2, 4, 3, 6};
		System.out.println("Maximum Activities:");
		System.out.println(activitySelection(start, end, 4));
	}
	
	public static int activitySelection(int start[], int end[], int n)
    {
        List<Activity> list = new ArrayList<>();
        for(int i=0;i<n;i++){
            list.add(new Activity(start[i],end[i]));
        }
        
        Collections.sort(list, (m1, m2) -> m1.end - m2.end );
        
        int res = 1;
        int endIndex = 0;
        for(int i=1;i<n;i++){
            if(list.get(i).start > list.get(endIndex).end){
                res++;
                endIndex = i;
            }
        }
        return res;
    }
}

class Activity{
    int start;
    int end;
    
    Activity(int start,int end){
        this.start = start;
        this.end = end;
    }
}
