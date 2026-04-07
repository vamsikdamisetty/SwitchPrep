package com.trees;

import java.util.*;

public class BT2 {


    /*
    Zigzag traversal is a modification of the traditional level order traversal in a binary tree.
    Level Order Traversal explores does at each level from left or right but zigzag traversal adds a twist by alternating the direction of exploration.

    At odd levels, we proceed from left to right but for even levels the order is reversed, from right to left.
    This is achieved by introducing a `leftToRight` flag which controls the order in which nodes are processed at each level.

    When `leftToRight` is true, nodes are inserted into the level array from left to right and when its false, nodes are inserted right to left.
     */
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();

        if(root == null) return res;


        boolean leftToRight = true;

        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        while(!queue.isEmpty()){
            int levelSize = queue.size();
            Integer[] level = new Integer[levelSize];
            for(int i=0;i<levelSize;i++){
                TreeNode node = queue.poll();

                int index = (leftToRight) ? i:levelSize-1-i;

                level[index] = node.data;

                if(node.left != null) queue.offer(node.left);

                if(node.right != null) queue.offer(node.right);
            }
            leftToRight = !leftToRight;
            res.add(Arrays.asList(level));

        }
        return res;
    }

    /*
class Node{
    int data;
    Node left;
    Node right;
    Node(int data){
        this.data = data;
        left=null;
        right=null;
    }
}
*/

    class Solution {
        // Function to return a list of nodes visible from the top view
        // from left to right in Binary Tree.
         ArrayList<Integer> topView(TreeNode root) {
            ArrayList<Integer> ans = new ArrayList<>();

            if (root == null) {
                return ans;
            }
            // Map to store the top view nodes
            // based on their vertical positions
            Map<Integer, Integer> mpp = new TreeMap<>();

            // Queue for BFS traversal, each element
            // is a pair containing node
            // and its vertical position
            Queue<Pair<TreeNode, Integer>> q = new LinkedList<>();

            q.add(new Pair<>(root, 0));

            while (!q.isEmpty()) {

                Pair<TreeNode, Integer> pair = q.poll();
                TreeNode node = pair.getKey();
                int line = pair.getValue();

                // If the vertical position is not already
                // in the map, add the node's data to the map
                mpp.putIfAbsent(line, node.data);
                //For bottom view -> update everytime, so last one would retained


                // Process left child
                if (node.left != null) {
                    // Push the left child with a decreased
                    // vertical position into the queue
                    q.add(new Pair<>(node.left, line - 1));
                }

                // Process right child
                if (node.right != null) {
                    // Push the right child with an increased
                    // vertical position into the queue
                    q.add(new Pair<>(node.right, line + 1));
                }
            }

            for (int value : mpp.values()) {
                ans.add(value);
            }

            return ans;

        }
    }
    class Pair<U, V> {
        public final U key;
        public final V value;

        public Pair(U first, V second) {
            this.key = first;
            this.value = second;
        }

        public U getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }


    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        dfs(root,0,res);
        return res;
    }

    /*
        PreOrder but move right first
        Every time res.size == level
        we are just starting traversing the level, Hence add the node
     */
    void dfs(TreeNode root,int level,List<Integer> res){
        if(root == null) return;

        if(res.size() == level){
            res.add(root.data);
        }

        dfs(root.right,level+1,res);
        dfs(root.left,level+1,res);
    }



    /*
        The algorithm solves two different questions at the same time in a single DFS pass:

    "What's the best path passing through this node?" —
    This is the global answer. A path through a node can use both its left and right subtrees, forming an arch shape.
    This gets recorded into maxPathSum[0].

    "What's the best I can contribute to my parent?" —
    A node can only extend in one direction upward (either left or right subtree, not both).
    So it returns root.val + max(left, right).

     */
    public int maxPathSum(TreeNode root) {
        int[] maxPath = {Integer.MIN_VALUE};
        maxPath(root,maxPath);

        return maxPath[0];
    }

    int maxPath(TreeNode root,int[] maxPath){
        if(root == null) return 0;

        //The Math.max(0, ...) trick means: "If a subtree gives a negative contribution, just ignore it entirely."
        int left = Math.max(0,maxPath(root.left,maxPath));

        int right = Math.max(0,maxPath(root.right,maxPath));

        maxPath[0] = Math.max(maxPath[0],left + right + root.data);

        return root.data + Math.max(left,right);
    }
}
