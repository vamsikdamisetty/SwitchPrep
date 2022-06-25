package com.stackandqueue;

public class Stack1 {
	public static void main(String[] args) {

        Stack s = new Stack();
        s.push(6);
        s.push(3);
        s.push(7);
        s.push(7);
        System.out.println("Top of the stack before deleting any element " + s.top());
        System.out.println("Size of the stack before deleting any element " + s.size());
        System.out.println("The element deleted is " + s.pop());
        System.out.println("Size of the stack after deleting an element " + s.size());
        System.out.println("Top of the stack after deleting an element " + s.top());
        s.pop();
        s.pop();
        s.pop();
    }
}
