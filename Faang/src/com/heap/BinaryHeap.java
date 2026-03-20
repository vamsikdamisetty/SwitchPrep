package com.heap;
import java.util.*;


class BinaryHeap {
    // Maximum elements that can be stored in heap
    static int capacity;

    // Current no of elements in heap
    static int size;

    // Array for storing the keys
    static int arr[];

    BinaryHeap(int cap) {
        // Assigning the capacity
        capacity = cap;

        // Initially size of heap is zero
        size = 0;

        // Creating an array
        arr = new int[capacity];
    }

    // Returns the parent of ith Node
    static int parent(int i) {
        return (i - 1) / 2;
    }

    // Returns the left child of ith Node
    static int left(int i) {
        return 2 * i + 1;
    }

    // Returns the right child of the ith Node
    static int right(int i) {
        return 2 * i + 2;
    }

    // Insert a new key x
    static void Insert(int x) {
        if (size == capacity) {
            System.out.println("Binary Heap Overflown");
            return;
        }

        // Insert new element at end
        arr[size] = x;

        // Store the index, for checking heap property
        int k = size;

        // Increase the size
        size++;

        // Fix the min heap property
        while (k != 0 && arr[parent(k)] > arr[k]) {
            int temp = arr[parent(k)];
            arr[parent(k)] = arr[k];
            arr[k] = temp;
            k = parent(k);
        }
    }

    static void Heapify(int ind) {
        // Right child
        int ri = right(ind);

        // Left child
        int li = left(ind);

        // Initially assume violated value is minimum
        int smallest = ind;

        if (li < size && arr[li] < arr[smallest])
            smallest = li;

        if (ri < size && arr[ri] < arr[smallest])
            smallest = ri;

        // If the Minimum among the three nodes is not the parent itself,
        // then swap and call Heapify recursively
        if (smallest != ind) {
            int temp = arr[ind];
            arr[ind] = arr[smallest];
            arr[smallest] = temp;
            Heapify(smallest);
        }
    }

    static int getMin() {
        return arr[0];
    }

    static int ExtractMin() {
        if (size <= 0)
            return Integer.MAX_VALUE;

        if (size == 1) {
            size--;
            return arr[0];
        }

        int mini = arr[0];

        // Copy last Node value to root Node
        arr[0] = arr[size - 1];

        size--;

        // Call heapify on root node
        Heapify(0);

        return mini;
    }

    static void Decreasekey(int i, int val) {
        // Updating the new value
        arr[i] = val;

        // Fixing the Min heap
        while (i != 0 && arr[parent(i)] > arr[i]) {
            int temp = arr[parent(i)];
            arr[parent(i)] = arr[i];
            arr[i] = temp;
            i = parent(i);
        }
    }

    static void Delete(int i) {
        Decreasekey(i, Integer.MIN_VALUE);
        ExtractMin();
    }

    static void print() {
        for (int i = 0; i < size; i++)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    public static void main(String args[]) {
        BinaryHeap h = new BinaryHeap(20);

        h.Insert(4);
        h.Insert(1);
        h.Insert(2);
        h.Insert(6);
        h.Insert(7);
        h.Insert(3);
        h.Insert(8);
        h.Insert(5);

        System.out.println("Min value is " + h.getMin());

        h.Insert(-1);
        System.out.println("Min value is " + h.getMin());

        h.Decreasekey(3, -2);
        System.out.println("Min value is " + h.getMin());

        h.ExtractMin();
        System.out.println("Min value is " + h.getMin());

        h.Delete(0);
        System.out.println("Min value is " + h.getMin());
    }
}

