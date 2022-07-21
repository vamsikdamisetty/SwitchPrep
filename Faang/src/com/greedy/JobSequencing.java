package com.greedy;

import java.util.Arrays;
import java.util.Iterator;

class Job {
	int id, profit, deadline;

	Job(int x, int y, int z) {
		this.id = x;
		this.deadline = y;
		this.profit = z;
	}
}

public class JobSequencing {

	public static void main(String[] args) {
		Job[] arr = new Job[4];
		arr[0] = new Job(1, 4, 20);
		arr[1] = new Job(2, 1, 10);
		arr[2] = new Job(3, 2, 40);
		arr[3] = new Job(4, 2, 30);

		// function call
		int[] res = JobScheduling(arr, 4);
		System.out.println(res[0] + " " + res[1]);
	}

	static int[] JobScheduling(Job arr[], int n) {

		Arrays.sort(arr, (j1, j2) -> j2.profit - j1.profit);

		int maxi = 0;
		for (int i = 0; i < arr.length; i++) {
			maxi = Math.max(maxi, arr[i].deadline);
		}
		
		int[] res = new int[maxi+1];
		
		for (int i = 1; i < res.length; i++) {
			res[i] = -1;
		}
		
		int profit = 0;
		int cnt = 0;
		for (int i = 0; i < arr.length; i++) {
			
			for (int j = arr[i].deadline; j > 0; j--) {
				
				if(res[j] == -1) {
					res[j] = i;
					profit += arr[i].profit;
					cnt++;
					break;
				}
			}
		}
		
		int[] ans = {cnt,profit};
		
		return ans;
	}

}
