package com.trees;

public class BT1 {

	public static void main(String[] args) {
		TreeNode root = new TreeNode(1);
		
		root.left = new TreeNode(2);
		root.left.right = new TreeNode(4);
		
		root.right = new TreeNode(3);
		root.right.left = new TreeNode(5);
		root.right.left.left = new TreeNode(7);
//		root.right.left.left.left = new TreeNode(8);
//		root.right.left.left.left.right = new TreeNode(9);
		
		root.right.right = new TreeNode(6);
		
		
		System.out.println("Max Depth of a tree");
		BT1 bt = new BT1();
		System.out.println("Max Depth:"+ bt.maxDepth(root));
		
		// Time,Spcae :: O(n)
		System.out.println("IS the tree Balanced ? " + bt.balancedTree(root));

		// Time,Spcae :: O(n)		
		System.out.println("Diameter of a tree :: " + bt.diameterOfBinaryTree(root));
	}
	
	/*
	 * Intuition is to find the height of everynode and and use it to help parent node in finding height
	 * MAXDEPTH / Height of tree
	 */
    public int maxDepth(TreeNode root) {
        if(root == null) return 0;
        
        return 1+ Math.max(maxDepth(root.left),maxDepth(root.right));
    }
    
    public boolean balancedTree(TreeNode root) {
    	return dfsHeight(root) == -1 ? false : true;
    }
    
    /*
     * As while finding max height we find the hright of every node , so here we are using it to check balanced tree condition 
     * ie., Math.abs(left - right) > 1
     * Intuition is to keep returning -1 when any of the node fails to be binary tree and stop execution
     */
    public int dfsHeight(TreeNode root) {
    	
    	if(root == null) return 0;
    	
    	int left = dfsHeight(root.left);
    	if(left == -1) return -1;
    	
    	int right = dfsHeight(root.right);
    	if(right == -1) return -1;
    	
    	if(Math.abs(left - right) > 1) return -1;
    	
    	return 1+Math.max(left, right);
    }
    
    
    public int diameterOfBinaryTree(TreeNode root) {
        int[] diameter = {0};
        findDiameter(root,diameter);
        return diameter[0];
    }
    
    /*
     * Intuition is while finding left maxheight and right maxheight of a node,
     * Maintain a diameter variable to keep checking for maximum diameter by adding heights of left and right
     * 
     *  IN Java variables cannot be passed as reference ,
     *  So array of size 1 is used
     * 
     */
    int findDiameter(TreeNode root,int[] diameter){
        if(root == null) return 0;
        
        int left = findDiameter(root.left,diameter);
        int right = findDiameter(root.right,diameter);
        
        diameter[0] = Math.max(diameter[0],left + right);
        
        return 1 + Math.max(left,right);
    }
}
