package com.stackandqueue;

import java.util.Stack;

/*
 * Application of stack
 * Time Complexity: O(N)
 * Space Complexity: O(N)
 */
public class BalancedParanthesis {

	public static void main(String[] args) {

		System.out.println("Balanced ? ::" + isValid("{[((()))]}{}[]"));
		System.out.println("Balanced ? ::" + isValid("[][(]"));
	}

	public static boolean isValid(String s) {
		Stack<Character> stack = new Stack<>();

		for (Character ch : s.toCharArray()) {
			if (ch == '(' || ch == '{' || ch == '[') {
				stack.add(ch);
			} else {
				if (stack.empty()) {
					return false;
				}
				Character open = stack.pop();
				if (open == '(' && ch == ')' || open == '{' && ch == '}' || open == '[' && ch == ']') {
					continue;
				} else {
					return false;
				}
			}
		}
		return stack.empty();
	}
}
