package com.trees;

import java.util.ArrayList;

public class BST1 {

	public static void main(String[] args) {
		TreeNode root = new TreeNode(8);
		root.left = new TreeNode(4);
		root.left.left = new TreeNode(2);
		root.left.left.left = new TreeNode(1);
		root.left.left = new TreeNode(3);
		
		root.left.right = new TreeNode(6);
		root.left.right.left = new TreeNode(5);
		root.left.right.right = new TreeNode(7);
		
		
		root.right = new TreeNode(12);
		root.right.left = new TreeNode(10);
		root.right.left.right = new TreeNode(11);
		
		root.right.right = new TreeNode(14);
		root.right.right.right = new TreeNode(15);
	
		BST1 bst = new BST1();
		
		//O(logn base 2)
		System.out.println("Search in BST: ");
		TreeNode node= bst.searchBST(root, 15);
		System.out.println(node == null ? "Not found" :"FOund Node:" +node.data);
		
		node= bst.searchBST(root, 99);
		System.out.println(node == null ? "Not found 99" :"FOund Node:" );
		
		System.out.println("Find Ceil: ");
		System.out.println(bst.findCeil(root, 10));
		System.out.println(bst.findCeil(root, 9));
		System.out.println(bst.findCeil(root, 16));
		
		System.out.println("Find floor: ");
		System.out.println(bst.floorInBST(root, 13));
		System.out.println(bst.floorInBST(root, 0));
		
		/*
		 * Using Insert we can solve below
		 * https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/submissions/
		 */
		System.out.println("Insert into a Binary Search Tree");
		ArrayList<Integer> inOrder = new ArrayList<>();
		BinaryTree.inOrderTrav(root,inOrder);
		System.out.println("InOrder Before Insertion:");
		inOrder.forEach(e->System.out.print(e + " "));
		bst.insertIntoBST(root, 9);
		inOrder.clear();
		BinaryTree.inOrderTrav(root,inOrder);
		System.out.println("\nInOrder After Insertion");
		inOrder.forEach(e->System.out.print(e + " "));
		
		//TC : O(Height of tree)
		System.out.println("\nDelete a Node from Binary Tree");
		bst.deleteNode(root, 9);
		inOrder.clear();
		BinaryTree.inOrderTrav(root,inOrder);
		System.out.println("\nInOrder After Deletion");
		inOrder.forEach(e->System.out.print(e + " "));
		
		//TC : O(n) SC: O(1)
		System.out.println("\n4. Check is a BT is BST or not?");
		System.out.println(bst.isValidBST(root));
		
		//TC : O(H) Least Common Ancestor
		System.out.println("\n5. Find LCA of two nodes in BST:");
		System.out.println(bst.lca(root, 6, 5).data);
		System.out.println(bst.lca(root, 7, 5).data);
	}
	
	
    public TreeNode searchBST(TreeNode root, int val) {
        
        while(root != null && root.data != val){
            System.out.println(root.data);
        	root = val < root.data ? root.left : root.right;
        }
        
        return root;
    }
    
    public int findCeil(TreeNode root, int x) {

        int ceil = -1;
        while(root != null){
            if(root.data == x)
                return x;
            
            if(x > root.data){
                root = root.right;
            }else{
                ceil = root.data;
                root = root.left;
            }
        }
        return ceil;
            
    }
    
    public int floorInBST(TreeNode root, int key) {
        int floor = -1 ;
        
        while(root != null){
            if(root.data == key){
                return key;
            }
            
            if(key > root.data){
                floor = root.data;
                root = root.right;
            }else{
                root = root.left;
            }
        }
        return floor;
    }
    
    public TreeNode insertIntoBST(TreeNode root, int key) {
        
        if(root == null) return new TreeNode(key);
        
        TreeNode cur = root; 
        
        while(true){
            if(key < cur.data){
                if(cur.left != null){
                    cur = cur.left;
                }else{
                    cur.left = new TreeNode(key);
                    break;
                }
            }else{
                if(cur.right != null){
                    cur = cur.right;
                }else{
                    cur.right = new TreeNode(key);
                    break;
                }
            }
        }
        
        return root;
    }
    
    public TreeNode deleteNode(TreeNode root, int key) {
        if(root == null) return null;
        
        if(root.data == key){
            return deleteHelper(root);
        }
        
        TreeNode root1 = root;
        
        while(root != null){
            if(key < root.data){
                if(root.left != null && root.left.data == key){
                    root.left = deleteHelper(root.left);
                    break;
                }else{
                    root = root.left;
                }    
            }else{
                if(root.right != null && root.right.data == key){
                    root.right = deleteHelper(root.right);
                    break;
                }else{
                    root = root.right;
                }    
            }
            
        }
        
        return root1;
    }
    
    TreeNode deleteHelper(TreeNode root){
        if(root.left == null){
            return root.right;
        }else if(root.right == null){
            return root.left;
        }
        
        TreeNode rChild = root.right;
        TreeNode lastRight = findLastRight(root.left);
        lastRight.right = rChild;
        return root.left;
    }
    
    TreeNode findLastRight(TreeNode root){
        if(root.right == null) return root;
        
        return findLastRight(root.right);
    }
    
    
    public boolean isValidBST(TreeNode root) {
        return validate(root,Long.MIN_VALUE,Long.MAX_VALUE);
    }
    
    boolean validate(TreeNode root,long min,long max){
        if(root == null){
            return true;
        }
        
        if(root.data <= min || root.data >= max){
            return false;
        }
        
        return validate(root.left,min,root.data) && validate(root.right,root.data,max);
    }
    
    TreeNode lca(TreeNode root,int n1,int n2){
        
        if(root == null) return null;
        
        int val = root.data;
        if( n1 < val && n2 < val){
            return lca(root.left,n1,n2);
        }
        
        if(n1 > val && n2 > val){
            return lca(root.right,n1,n2);
        }
        
        return root;
    }
}



