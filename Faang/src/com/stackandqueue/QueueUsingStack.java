package com.stackandqueue;

import java.util.Stack;

public class QueueUsingStack {

	public static void main(String[] args) {
		MyQueue1 q1 = new MyQueue1();
		q1.push(1);
		q1.push(2);
		q1.push(3);
		
		System.out.println(q1.pop());
		System.out.println(q1.pop());
		System.out.println(q1.pop());
		
		MyQueue2 q2 = new MyQueue2();
		q2.push(1);
		q2.push(2);
		q2.push(3);
		
		System.out.println(q2.pop());
		System.out.println(q2.pop());
		System.out.println(q2.pop());
		
	}
	
}

/*
 * This is implemented using two stacks but TIme complexity
 * Push - > O(2n) 
 */
class MyQueue1 {

	Stack<Integer> s1;
	Stack<Integer> s2;
    public MyQueue1() {
        s1 = new Stack<>();
        s2 = new Stack<>();
    }
    
    public void push(int x) {
        while(!s1.isEmpty()) {
        	s2.add(s1.pop());
        }
        s2.add(x);
        while(!s2.isEmpty()) {
        	s1.add(s2.pop());
        }
    }
    
    public int pop() {
        return s1.pop();
    }
    
    public int peek() {
        return s1.peek();
    }
    
    public boolean empty() {
        return s1.isEmpty();
    }
}

/*
 * Here also we are using two stacks but 
 * Push operation is O(1);
 * And Pop,Peek is amortised O(1)
 * Almost everytime O(1)	
 */
class MyQueue2 {

	Stack<Integer> input;
	Stack<Integer> output;
    public MyQueue2() {
    	input = new Stack<>();
    	output = new Stack<>();
    }
    
    public void push(int x) {
       input.add(x);
    }
    
    public int pop() {
        peek();
        return output.pop();
    }
    
    public int peek() {
    	if(output.isEmpty()) {
        	while(!input.isEmpty()) {
        		output.add(input.pop());
        	}
        }
        return output.peek();
    }
    
    public boolean empty() {
        return input.isEmpty() && output.isEmpty();
    }
}

