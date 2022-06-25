package com.stackandqueue;

public class Queue1 {

	public static void main(String[] args) {
		Queue queue = new Queue();
		queue.push(1);
		queue.push(2);
		queue.push(3);
		queue.push(4);

		System.out.println(queue.top());
		
		System.out.println("Element Popped:"+ queue.pop());
		System.out.println("Size:" +queue.size());
		System.out.println("Element Popped:"+ queue.pop());
		System.out.println("Element Popped:"+ queue.pop());
		System.out.println("Element Popped:"+ queue.pop());
//		System.out.println("Element Popped:"+ queue.pop());
		queue.push(9);
	} 
	
}
