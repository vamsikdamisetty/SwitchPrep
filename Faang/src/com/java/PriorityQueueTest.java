package com.java;

import java.util.PriorityQueue;

public class PriorityQueueTest {

    public static void main(String[] args) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        minHeap.add(5);
        minHeap.add(54);
        minHeap.add(76);
        minHeap.add(3);

        /*
        Default order is to poll min items first
         */
        while (!minHeap.isEmpty()){
            System.out.println(minHeap.poll());
        }

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a,b)->b-a);

        maxHeap.add(5);
        maxHeap.add(54);
        maxHeap.add(76);
        maxHeap.add(3);

        System.out.println("Printing max Heap");
        /*
        Default order is to poll min items first
         */
        while (!maxHeap.isEmpty()){
            System.out.println(maxHeap.poll());
        }

        PriorityQueue<Result> priorityQueue = new PriorityQueue<>((a,b)->b.score-a.score);

        priorityQueue.add(new Result("Vam1",6));

        priorityQueue.add(new Result("Vam2",10));

        priorityQueue.add(new Result("Vam3",17));

        priorityQueue.add(new Result("Vam4",3));

        while (!priorityQueue.isEmpty()){
            System.out.println(priorityQueue.poll());
        }
    }

    static class Result{
        String docID;
        int score;
        Result(String docID,int score){
            this.docID = docID;
            this.score = score;
        }
        public String toString(){
            return "DocID : " + docID + "\tScore: " + score;
        }
    }
}
