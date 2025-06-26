package com.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class JobSequencingSol2 {

	/*
	 * Intuition: The strategy to maximize profit should be to pick up jobs that
	 * offer higher profits. Hence we should sort the jobs in descending order of
	 * profit. Now say if a job has a deadline of 4 we can perform it anytime
	 * between day 1-4, but it is preferable to perform the job on its last day.
	 * This leaves enough empty slots on the previous days to perform other jobs.
	 * 
	 * Time : O(n*MAX_DEADLINE)
	 * Space : O(MAX_DEADLINE)
	 */
	public ArrayList<Integer> jobSequencing(int[] deadline, int[] profit) {

		List<Job> list = new ArrayList<Job>();

		int maxDeadline = 0;
		for (int i = 0; i < profit.length; i++) {
			list.add(new Job(deadline[i], profit[i]));
			maxDeadline = Math.max(maxDeadline, deadline[i]);
		}
		
		//Sorting based on Profit
		list.sort(Comparator.comparingInt(Job::getProfit).reversed());
		
		//Create an array to store best day before deadline to perform Job
		int[] jobs = new int[maxDeadline + 1];
		
		int cntJobs = 0;
		int maxProfit = 0;
		for (Job job : list) {

			int index = job.deadline;
			
			//Finding max Day to perform job before deadline
			while (index > 0 && jobs[index] != 0) {
				index--;
			}
			
			//If there is an empty slot, perform the job
			if (index != 0) {
				jobs[index] = job.profit;
				cntJobs++;
				maxProfit += job.profit;
			}
		}

		return new ArrayList<Integer>(Arrays.asList(cntJobs, maxProfit));

	}

	class Job {
		int deadline;
		int profit;

		public Job(int deadline, int profit) {
			super();
			this.deadline = deadline;
			this.profit = profit;
		}

		public int getDeadline() {
			return deadline;
		}

		public int getProfit() {
			return profit;
		}
	}
}

