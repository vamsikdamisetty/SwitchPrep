package com.stackandqueue;

public class Stack {
    int size = 3;
    int arr[] = new int[size];
    int top = -1;
    void push(int x) {
    	
    	if(top == arr.length -1) {
    		System.out.println("Stack OverFlow");
    		return;
    	}
    	
    	top++;
    	arr[top] = x;
    }
    int pop() {
    	if(top == -1) {
    		System.out.println("Stack underFlow");
    		return top;
    	}
        int x = arr[top];
        top--;
        return x;
    }
    int top() {
        return arr[top];
    }
    int size() {
        return top + 1;
    }
}
