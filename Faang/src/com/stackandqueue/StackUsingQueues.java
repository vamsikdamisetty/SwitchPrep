package com.stackandqueue;

import java.util.LinkedList;
import java.util.Queue;

public class StackUsingQueues {

	/*
	 * Time Complexity: O(N)
	 * Space Complexity: O(N)
	 */
	public static void main(String[] args) {
		MyStack s = new MyStack();
		
		s.push(1);
		s.push(2);
		s.push(3);
		
		System.out.println(s.pop());
		System.out.println(s.pop());
		System.out.println(s.pop());
		
		s.pushSingleQueue(1);
		s.pushSingleQueue(5);
		s.pushSingleQueue(7);
		
		System.out.println(s.pop());
		System.out.println(s.pop());
		System.out.println(s.pop());
		
	} 
	
}

class MyStack {

	Queue<Integer> q1;
	Queue<Integer> q2;
    public MyStack() {
        q1 = new LinkedList<>();
        q2 = new LinkedList<>();
    }

    //O(n)
    public void push(int x) {
        q2 .add(x);
        q2.addAll(q1);
        q1.clear();
        Queue<Integer> temp = q2;
        q2 = q1;
        q1 = temp;
        
    }
    //O(n)
    public void pushSingleQueue(int x) {
        q1.add(x);
        for(int i=1;i<q1.size();i++) {
        	q1.add(q1.remove());
        }
        
    }
    
    public int pop() {
        return q1.remove();
    }
    
    public int top() {
        return q1.peek();
    }
    
    public boolean empty() {
        return q1.isEmpty();
    }
}