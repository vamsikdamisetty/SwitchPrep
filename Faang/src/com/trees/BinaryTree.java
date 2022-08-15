package com.trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class BinaryTree {
    
	/*
	 * TIme complexity: O(N) N-> number of TreeNodes
	 * Space : O(N) at max because of Skew
	 */
	static void preOrderTrav(TreeNode root, ArrayList < Integer > preOrder) {
        if (root == null)
            return;

        preOrder.add(root.data);
        preOrderTrav(root.left, preOrder);
        preOrderTrav(root.right, preOrder);
    }

	static void inOrderTrav(TreeNode root, ArrayList < Integer > inOrder) {
        if (root == null)
            return;

        inOrderTrav(root.left, inOrder);
        inOrder.add(root.data);
        inOrderTrav(root.right, inOrder);
    }
	
	static void postOrderTrav(TreeNode root, ArrayList < Integer > postOrder) {
        if (root == null)
            return;

        postOrderTrav(root.left, postOrder);
        postOrderTrav(root.right, postOrder);
        postOrder.add(root.data);
    }
	
	/*
	 * Time : O(n)
	 * SPace : O(n)  O(n/2) n/2 is all leaf TreeNode for a complete binary tree
	 */
	static void levelOrderTrav(TreeNode root, ArrayList < Integer > levelOrder) {
		
		if(root == null) return;
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.offer(root);
		/*
		 * https://leetcode.com/problems/binary-tree-level-order-traversal/submissions/
		 * 
		 * No need of extra for loop unless we want to seperate the levels
		 */
		while(!queue.isEmpty()) {
			int levelSize = queue.size();
			for(int i=0;i<levelSize;i++) {
				TreeNode TreeNode = queue.poll();
				if(null != TreeNode.left) queue.offer(TreeNode.left);
				if(null != TreeNode.right) queue.offer(TreeNode.right);
				
				levelOrder.add(TreeNode.data);
			}
			levelOrder.add(0);
		}
	}
	
    public static void main(String args[]) {

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.left.right.left = new TreeNode(8);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        root.right.right.left = new TreeNode(9);
        root.right.right.right = new TreeNode(10);

        ArrayList < Integer > preOrder = new ArrayList < > ();
        preOrderTrav(root, preOrder);

        System.out.print("The preOrder Traversal is : ");
        preOrder.forEach(e->System.out.print(e + " "));

        ArrayList < Integer > inOrder = new ArrayList < > ();
        inOrderTrav(root, inOrder);

        System.out.print("\nThe inOrder Traversal is : ");
        inOrder.forEach(e->System.out.print(e + " "));
        
        ArrayList < Integer > postOrder = new ArrayList < > ();
        postOrderTrav(root, postOrder);

        System.out.print("\nThe postOrder Traversal is : ");
        postOrder.forEach(e->System.out.print(e + " "));
        
        ArrayList < Integer > levelOrder = new ArrayList < > ();
        levelOrderTrav(root, levelOrder);

        System.out.print("\nThe levelOrder Traversal is : ");
        levelOrder.forEach(e->System.out.print(e + " "));
     
        
        //-------------------------------------------------------------------------
        //Iterative Traversals
        System.out.println("\n\n\nIterative Traversals");
        
        IterativeTravarsal iterativeTravarsal = new IterativeTravarsal();
        
        preOrder = (ArrayList<Integer>) iterativeTravarsal.preOrderTrav(root);
        System.out.print("The preOrder Traversal is : ");
        preOrder.forEach(e->System.out.print(e + " "));
        
        inOrder = (ArrayList<Integer>) iterativeTravarsal.inOrderTrav(root);
        System.out.print("\nThe inOrder Traversal is : ");
        inOrder.forEach(e->System.out.print(e + " "));
        
        postOrder = (ArrayList<Integer>) iterativeTravarsal.postOrderTrav(root);
        System.out.print("\nThe postOrder Traversal is : ");
        postOrder.forEach(e->System.out.print(e + " "));
    
    }

}

/*
 * There are little confusing 
 * PostOrder must be looked carefuuly
 */
class IterativeTravarsal{
	
	List<Integer> preOrderTrav(TreeNode root){
		if(root == null) return null;
		List<Integer> preOrder = new ArrayList<>();
		Stack<TreeNode> stack = new Stack<>();
		stack.push(root);
		
		while(!stack.isEmpty()) {
			TreeNode TreeNode = stack.pop();
			preOrder.add(TreeNode.data);
			if(TreeNode.right != null) stack.push(TreeNode.right);
			if(TreeNode.left != null) stack.push(TreeNode.left);
		}
		
		return preOrder; 
	}
	
	List<Integer> inOrderTrav(TreeNode root){
		if(root == null) return null;
		List<Integer> inOrder = new ArrayList<>();
		Stack<TreeNode> stack = new Stack<>();
		TreeNode TreeNode = root;
		while(true) {
			if(TreeNode != null) {
				stack.push(TreeNode);
				TreeNode = TreeNode.left;
			}else {
				if(stack.isEmpty()) {
					break;
				}
				TreeNode = stack.pop();
				inOrder.add(TreeNode.data);
				TreeNode = TreeNode.right;
			}
		}
		
		return inOrder; 
	}
	
	List<Integer> postOrderTrav(TreeNode root){
		if(root == null) return null;
		List<Integer> postOrder = new ArrayList<>();
		Stack<TreeNode> stack1 = new Stack<>();
		Stack<TreeNode> stack2 = new Stack<>();
		stack1.push(root);
		while(!stack1.isEmpty()) {
			TreeNode TreeNode = stack1.pop();
			stack2.push(TreeNode);
			
			if(TreeNode.left!= null) stack1.push(TreeNode.left);
			if(TreeNode.right!= null) stack1.push(TreeNode.right);
			
		}
		
		while(!stack2.isEmpty()) {
			postOrder.add(stack2.pop().data);
		}
		
		return postOrder; 
	}
	
}