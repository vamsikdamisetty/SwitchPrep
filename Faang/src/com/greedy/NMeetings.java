 package com.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//TC :O (n) + O(nlogn) + O(n) -> O(nlogn)
//SC : O(n) for since we used an additional data structure for storing the start time, end time
public class NMeetings {
	
	/*
	 * This and ActivitySelection are same
	 * Intuition : To maximize the number of meeting we should take those meeting first which finish quickly
	 * Sort the meeting based on ending time and see how many meetings can be accommodated
	 */
	public static void main(String args[]) {

		   int n = 6;
		   int start[] = {1,3,0,5,8,5};
		   int end[] = {2,4,5,7,9,9};
		   System.out.println(maxMeetings(start, end, n));;
	}

	public static int maxMeetings(int[] start, int[] end, int n) {

		ArrayList<Meeting> list = new ArrayList<>();
		for (int i = 0; i < end.length; i++) {
			list.add(new Meeting(start[i], end[i]));
		}

//		list.sort((m1,m2)->m1.end-m2.end);
//		Collections.sort(list, (m1, m2) -> m1.end < m2.end ? -1 : 1);
		//Sort expects a comparator and look at method def to understand
		list.sort(Comparator.comparingInt(Meeting::getEnd));



		int meetingCount = 0;
		int endTime = -1;

		for (Meeting meeting : list) {
			if (meeting.getStart() > endTime) {
				meetingCount++;
				endTime = meeting.getEnd();
			}
		}

		return meetingCount;
	}
}

class Meeting {
	public int start;
	public int end;
	
	
	public int getStart() {
		return start;
	}


	public int getEnd() {
		return end;
	}


	public Meeting(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
}
