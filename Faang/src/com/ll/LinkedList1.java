package com.ll;


public class LinkedList1 {

	Node head; // head of linked list

	  /* Linked list node */
	  class Node
	  {
	    int data;
	    Node next;
	    Node(int d)
	    {
	      data = d;
	      next = null;
	    }
	  }

	 
	  /* Inserts a new Node at front of the list. */
	  public void push(int new_data)
	  {
			/* 1 & 2: Allocate the Node &
					Put in the data*/
	    Node new_node = new Node(new_data);

	    /* 3. Make next of new Node as head */
	    new_node.next = head;

	    /* 4. Move the head to point to new Node */
	    head = new_node;
	  }

	  /* This function prints contents of linked list
	  starting from the given node */
	  public void printList()
	  {
	    Node tnode = head;
	    
	    while (tnode != null)
	    {
	      System.out.print(tnode.data+"->");
	      tnode = tnode.next;
	    }
	    System.out.println("NULL");
	  }

	  public Node reverseList(Node head) {
		    
	        if (head == null){
	            return head;
	        }
	        Node curr = head;
	        Node prev = null;
	        
	        while(curr != null){
	        	Node temp = curr.next;
	            curr.next = prev;
	            prev = curr;
	            curr = temp;
	        }
	        return prev;
	    }

	    public Node middleNode(Node head) {
	        if(head == null){
	            return head;
	        }
	        
	        Node slow = head;
	        Node fast = head;
	        
	        while(fast!=null && fast.next !=null){
	            slow = slow.next;
	            fast = fast.next.next;
	        }
	        
	        return slow;
	    }

        public Node mergeTwoLists(Node list1, Node list2) {
            
            if(list1 == null) return list2;
            if(list2 == null) return list1;
        	
            Node temp = null;
            if(list1.data > list2.data){
                temp = list1;
                list1 = list2;
                list2 = temp;
            }
            
        	Node curr1 = list1;
        	Node prev = list1;
        	Node curr2 = list2;
        	
            
            while(curr1 != null && curr2 != null){
                if(curr1.data > curr2.data){
                        temp = curr2.next;
                        prev.next = curr2;
                        curr2.next = curr1;
                        prev = curr2;
                        curr2 = temp;
                }else{
                    prev = curr1;
                    curr1 = curr1.next;
                    
                }
            }
            
            if(curr2 != null){
            	prev.next = curr2;
            }
            
            return list1;
            
        }
        
        public Node removeNthFromEnd(Node head, int n) {
        	Node fast = head;
        	Node slow = head;
            
            for(int i=1;i<=n;i++){
                
                if(fast.next == null){
                    
                    if(i == n){
                        head = head.next;
                    }
                        
                    return head;
                }
                
                fast = fast.next;
            }
            
            while(fast.next != null){
                slow = slow.next;
                fast = fast.next;
            }
            
            slow.next = slow.next.next;
            
            return head;
        }
        
        public void deleteNode(Node node) {      
            node.data = node.next.data;
            node.next = node.next.next;
        }
        
        public Node addTwoNumbers(Node l1, Node l2) {
            
        	Node l3 = null,temp =null;
            
            int carry = 0;
            while(l1 != null || l2!= null || carry != 0){
                
                
                if(l1 != null){
                    carry += l1.data;
                    l1 = l1.next;
                }
                
                if(l2 != null){
                    carry += l2.data;
                    l2 = l2.next;
                } 
                
                if(l3 == null){
                    l3 = new Node(carry%10);
                    temp = l3;
                    carry = carry / 10;
                }else{
                    temp.next = new Node(carry%10);
                    temp = temp.next;
                    carry = carry / 10;
                }
                       
            }
            return l3;
        }
        
	  public static void main(String[] args) {
		  LinkedList1 ll = new LinkedList1();
		  ll.push(5);
		  ll.push(4);
		  ll.push(3);
		  ll.push(2);
		  ll.push(1);
		  
		  ll.printList();
		  
		  System.out.println("\n\n1. Reverse a LinkedList"); //O(n)
		  ll.head = ll.reverseList(ll.head);
		  ll.printList();
		  //reset
		  ll.head = ll.reverseList(ll.head);
		  
		  System.out.println("\n\n2. Find middle of LinkedList");
		  Node middle = ll.middleNode(ll.head); //O(n) single pass
		  System.out.println("Middle Node:" + middle.data);
		  
		  
		  System.out.println("\n\n3. Merge Two Sorted Linked Lists");
		  LinkedList1 ll2 = new LinkedList1();
		  ll2.push(8);
		  ll2.push(6);
		  ll2.push(5);
		  ll2.push(3);
		  ll2.push(1);
		  ll.mergeTwoLists(ll.head, ll2.head);
		  ll.printList();
		  
		  System.out.println("\n\n4. Remove N-th node from back of LinkedList ");
		  ll.printList();
		  ll.removeNthFromEnd(ll.head, 4);   //O(n) single pass
		  ll.printList();
		  
		  System.out.println("\n\n5. Delete a given Node when a node is given: O(1) solution");
		  //-------------------------------------------- Tail node will never be given to be deleted
		  ll.printList();
		  ll.deleteNode(ll.head.next.next.next.next);
		  ll.printList();
		  
		  System.out.println("\n\n6. Add two numbers as LinkedList");
		  LinkedList1 num1 = new LinkedList1();
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.push(9);
		  num1.printList();
		  
		  LinkedList1 num2 = new LinkedList1();
		  num2.push(9);
		  num2.push(9);
		  num2.push(9);
		  num2.push(9);
		  num2.push(9);
		  num2.printList();
		  
		  num1.head = ll.addTwoNumbers(num1.head, num2.head); //O(m+n)
		  num1.printList();
	}	
}
