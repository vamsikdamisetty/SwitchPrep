package com.stackandqueue;
import java.util.Stack;

/*
 * Time Complexity: O(1)
 * Space Complexity: O(N)
 * 
 * Other approch is to create a pair stack (Value,Minimum so far )
 * space : O(2N) 
 */
public class MinStackImpl {

	public static void main(String[] args) {
		MinStack ms = new MinStack();
		ms.push(-2);
		ms.push(1);
		System.out.println(ms.getMin());
		ms.push(-3);
		ms.push(4);
		ms.push(-3);
		System.out.println(ms.top());
		System.out.println("Min" + ms.getMin());
		System.out.println(ms.top());
		ms.pop();
		System.out.println(ms.top());
		ms.pop();
		System.out.println(ms.top());
		ms.pop();
		System.out.println("Min" + ms.getMin());
		System.out.println(ms.top());
		ms.pop();
	}
}
class MinStack {

    Long mini;
    Stack<Long> s;
    
    public MinStack() {
        mini = Long.MAX_VALUE;    
        s = new Stack<>();
    }
    
    public void push(int value) {
        Long val = Long.valueOf(value);
        if(s.empty()){
            s.push(val);
            mini = val;
        }else{
            if( mini > val){
            //Formula to rememeber
            // Here we are trying to create a flag point where min is changes and it will help while popping
            s.push(2*val - mini);
            mini = val;
            }else{
                s.push(val);
            }   
        }
    }
    
    public void pop() {
        if(s.peek() < mini){
            //Formula to rememeber
            mini = 2*mini - s.peek();
        }
        s.pop();
    }
    
    public int top() {
        Long val = s.peek();
        if(val < mini){
            return mini.intValue();
        }
        return val.intValue();
    }
    
    public int getMin() {
        return mini.intValue();
    }
}