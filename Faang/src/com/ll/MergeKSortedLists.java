package com.ll;

import java.util.List;
import java.util.PriorityQueue;

public class MergeKSortedLists {
    public Node mergeTwoLists(Node list1, Node list2) {

        Node dummy = new Node(0);
        Node temp = dummy;
        while(list1 != null && list2!= null) {
            if(list1.data <= list2.data) {
                temp.next = list1;
                list1 = list1.next;
            }else {
                temp.next = list2;
                list2 = list2.next;
            }
            temp = temp.next;
        }

        if(list1 != null) {
            temp.next = list1;
        }

        if(list2 != null) {
            temp.next = list2;
        }

        return dummy.next;
    }

    Node mergeKList(List<Node> lists){

        Node head = lists.get(0);
        for(int i=1;i<lists.size();i++){
            head = mergeTwoLists(head, lists.get(i));
        }
        return head;
    }

    /*
        Idea is to use MinHeap
        Idea

                Put the first node of each list into a Min Heap (PriorityQueue).

                Extract the minimum node from the heap.

                Add it to the result list.

                If the extracted node has a next node, push that next node into the heap.

                Repeat until heap becomes empty.

            Time Complexity

                Heap size ≤ K

                Each node inserted/removed once

                O(N log K)
                Where:

                N = total nodes

                K = number of lists
     */
    Node mergeKListWithMinHeap(List<Node> lists){
        PriorityQueue<Node> minHeap = new PriorityQueue<>((a, b) -> a.data - b.data);

        for(Node node:lists){
            if (node != null) {
                minHeap.add(node);
            }
        }

        Node dummy = new Node(0);
        Node tail = dummy;

        while(!minHeap.isEmpty()){
            Node minNode = minHeap.poll();
            tail.next = minNode;
            tail = tail.next;

            if(minNode.next != null){
                minHeap.add(minNode.next);
            }
        }
        return dummy.next;

    }

}

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
