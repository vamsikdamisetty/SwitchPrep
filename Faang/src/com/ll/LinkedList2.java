package com.ll;

public class LinkedList2 {
	Node head; // head of linked list

	/* Linked list node */
	class Node {
		int data;
		Node next;

		Node(int d) {
			data = d;
			next = null;
		}
	}

	/* Inserts a new Node at front of the list. */
	public void push(int new_data) {
		/*
		 * 1 & 2: Allocate the Node & Put in the data
		 */
		Node new_node = new Node(new_data);

		/* 3. Make next of new Node as head */
		new_node.next = head;

		/* 4. Move the head to point to new Node */
		head = new_node;
	}

	/*
	 * This function prints contents of linked list starting from the given node
	 */
	public void printList() {
		Node tnode = head;

		while (tnode != null) {
			System.out.print(tnode.data + "->");
			tnode = tnode.next;
		}
		System.out.println("NULL");
	}

	public Node getIntersectionNode(Node headA, Node headB) {
		Node dummy1 = headA;
		Node dummy2 = headB;

		/*
		 * Intution : when dummy1/dummy2 node reaches end Only the difference is pending
		 * in other dummy We use that length to move to the equal point in both
		 */
		while (dummy1 != dummy2) {
			dummy1 = dummy1 == null ? headB : dummy1.next;
			dummy2 = dummy2 == null ? headA : dummy2.next;
		}

		return dummy1;
	}

	public boolean isPalindrome(Node head) {

		if (head == null || head.next == null)
			return true;

		Node slow = head;
		Node fast = head;

		while (fast.next != null && fast.next.next != null) {
			fast = fast.next.next;
			slow = slow.next;
		}

		slow.next = reverse(slow.next);

		slow = slow.next;

		while (slow != null) {
			if (slow.data != head.data) {
				return false;
			}

			slow = slow.next;
			head = head.next;

		}
		return true;
	}

	Node reverse(Node head) {
		Node pre = null;
		Node next = null;

		while (head != null) {
			next = head.next;
			head.next = pre;
			pre = head;
			head = next;
		}

		return pre;
	}

	public Node rotateRight(Node head, int k) {

		if (k == 0 || head == null || head.next == null)
			return head;
		Node curr = head;
		int len = 1;

		while (curr.next != null) {
			len++;
			curr = curr.next;
		}

		curr.next = head;//LINKING THE LAST NODE TO FIRST NODE 
		k = len - (k % len);

		while (k != 0) {
			k--;
			curr = curr.next;
		}

		head = curr.next; // Moving the head to new first Node in the list
		curr.next = null; // Breaking the link of new last node in the list

		return head;
	}
	/*
	 * Node flatten(Node root) { if(root == null || root.next == null){ return root;
	 * }
	 * 
	 * root.next = flatten(root.next);
	 * 
	 * root = mergeLists(root,root.next);
	 * 
	 * return root;
	 * 
	 * }
	 * 
	 * Node mergeLists(Node a,Node b){ Node temp = new Node(0); Node res = temp;
	 * 
	 * while(a != null && b != null){
	 * 
	 * if(a.data <= b.data){ temp.bottom = a; a = a.bottom; }else{ temp.bottom = b;
	 * b = b.bottom; } temp = temp.bottom;
	 * 
	 * if(a != null){ temp.bottom = a; }else{ temp.bottom = b; } }
	 * 
	 * return res.bottom; }
	 */

	public boolean hasCycle(Node head) {
		if (head == null || head.next == null)
			return false;

		Node slow = head;
		Node fast = head;

		while (fast != null && fast.next != null) {
			fast = fast.next.next;
			slow = slow.next;

			if (fast == slow) {
				return true;
			}
		}
		return false;
	}

	public Node detectCycle(Node head) {
		if (head == null || head.next == null)
			return null;

		Node slow = head;
		Node fast = head;
		Node entry = head;

		while (fast != null && fast.next != null) {
			fast = fast.next.next;
			slow = slow.next;

			if (fast == slow) {
				while (entry != slow) {
					slow = slow.next;
					entry = entry.next;
				}
				return entry;
			}
		}
		return null;
	}

	public Node reverseKGroup(Node head, int k) {

		Node cur = head;
		for (int i = 0; i < k; i++) {

			if (cur == null) {
				return head;
			}
			cur = cur.next;
		}

		cur = head;
		Node pre = null;
		Node next = null;
		int count = 0;
		while (cur != null && count != k) {
			next = cur.next;
			cur.next = pre;
			pre = cur;
			cur = next;
			count++;
		}

		if (next != null) {
			head.next = reverseKGroup(next, k);
		}

		return pre;

	}

	/*
	 * Iterative Solution TUF (Understood now)
	 */
	Node kReverse(Node head, int k) {

		Node temp = head;
		Node prevLast = null;

		while (temp != null) {
			Node kNode = findKNode(temp, k);

			if (kNode == null) { //Rest of the LL is less than size of K
				if (prevLast != null) {//Not even 1 group available
					prevLast.next = temp;
				}
				break;
			}
			Node nextNode = kNode.next;//store next node after the group
			kNode.next = null;
			reverse(temp);

			if (temp == head) {//For first group
				head = kNode;
			} else {//Attach prev group last element to current group reversed first element
				prevLast.next = kNode;
			}

			prevLast = temp;
			temp = nextNode;

		}
		return head;
	}

	private Node findKNode(Node temp, int k) {

		k -= 1;
		while (temp != null && k > 0) {
			temp = temp.next;
			k--;
		}
		return temp;
	}

	public static void main(String[] args) {

		// 1. Find intersection point of Y LinkedList O(2m) Cannot implement this so
		// follow beloe link
		// https://leetcode.com/problems/intersection-of-two-linked-lists/submissions/
		/*
		 * Hashing is one approach FInd difference in length and move the longer one by
		 * the difference.. move both and return when common
		 * 
		 * Optimal is the one on top
		 */

		System.out.println("2. Check if a LinkedList is palindrome or not");
		LinkedList2 ll1 = new LinkedList2();
		ll1.push(1);
		ll1.push(2);
		ll1.push(3);
		ll1.push(2);
		ll1.push(1);
		ll1.printList();
		System.out.println(ll1.isPalindrome(ll1.head)); // O(n) -> O(n/2)+O(n/2)+O(n/2) space:O(1)
		System.out.println(ll1.isPalindrome(ll1.head)); // LL half reversed
		
		/* 4. Detect a cycle in ll
		 * Intuition:
		 * Detect loop :  
		 * Imagine once both slow and fast comes into loop 
		 * distance from fast to slow is 'd' 
		 * Now everytime both pointers move distance is reduced by 1 and at one point distance will be 0 and loop is found
		 * 
		 * Starting point of the loop:
		 * We know distance from fast to slow is 'd' 
		 * lets say slow moved l1 dist to reach starting point and fast will be l1 far from slow (Distance moved by fast is l1 + l1)
		 * For here, slow moves d to reach collision and fast moves 2d
		 * If you observe length of the loop is l1 + d 
		 * As slow already travelled d remaining id l1 from collison to starting point
		 * And head to starting point is also l1
		 * Place slow at starting point and move both(fast at collision) at same pace to reach starting point of the loop
		 * 
		 */
		// 
		// https://leetcode.com/problems/linked-list-cycle/submissions/
		// O(n) This will not take many turns for Fast to catch up slow, I may take
		// multiple rotations but it will reach eventually

		// removing loop O(N) space: O(1)
		// https://leetcode.com/problems/linked-list-cycle-ii/submissions/

		
		
		// 5. Flattening of a LinkedList (Code in notes)
		// O(2*N*M) , space O(N)  (N number of node in horizontal list M MAX no of nodes in each vertical list)
		// merge takes 2M to merge two lists  and  recursion stack takes N space 
		// https://practice.geeksforgeeks.org/problems/flattening-a-linked-list/1
		// Merge sort kind of logic (Uses Reccursion)
		// We use recursion here to avoid taking care next link while merging the two child lists
		

		System.out.println("6. Rotate a LinkedList");
		// O(n) + O(n - k%n) = O(n) , space O(1)
		ll1.printList();
		ll1.head = ll1.rotateRight(ll1.head, 18);
		ll1.printList();

		System.out.println("\n\n3. Reverse a LinkedList in groups.");
		LinkedList2 ll2 = new LinkedList2();
		ll2.push(8);
		ll2.push(7);
		ll2.push(6);
		ll2.push(5);
		ll2.push(4);
		ll2.push(3);
		ll2.push(2);
		ll2.push(1);
		ll2.printList();
		ll2.head = ll2.kReverse(ll2.head, 3);
		ll2.printList();
		/*
		 * Time Complexity: O(n). Traversal of list is done only once and it has �n�
		 * elements. Auxiliary Space: O(n/k). For each Linked List of size n, n/k or
		 * (n/k)+1 calls will be made during the recursion.
		 * 
		 * THis can be solved in O(1) space but that's iterative and looked confusing
		 * for me
		 * 
		 * Added Iterative approach also (Now i prefer this)
		 * TC : O(2n) SC : O(1)
		 */
	}
	
	
	
}
