package com.greedy;

import java.util.ArrayList;
import java.util.Collections;

//TC :O (n) + O(nlogn) + O(n) -> O(nlogn)
//SC : O(n) for since we used an additional data structure for storing the start time, end time
public class NMeetings {

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

		Collections.sort(list, (m1, m2) -> m1.end < m2.end ? -1 : 1);

		int meetingCount = 0;
		int endTime = -1;

		for (Meeting meeting : list) {
			if (meeting.start > endTime) {
				meetingCount++;
				endTime = meeting.end;
			}
		}

		return meetingCount;
	}
}

class Meeting {
	int start;
	int end;

	public Meeting(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
}
