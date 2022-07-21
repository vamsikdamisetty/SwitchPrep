package com.greedy;

import java.util.Arrays;
import java.util.Comparator;

public class FractionalKnapsack {
	
	/*
	 * Time Complexity: O(n log n + n). O(n log n) to sort the items and O(n) to iterate through all the items for calculating the answer.
	 * Space Complexity: O(1), no additional data structure has been used.
	 */
    public static void main(String args[])
    {
        int n = 3, weight = 50;
        Item arr[] = {new Item (100,20),new Item(60,10),new Item(120,30)};
        double ans = fractionalKnapsack(weight, arr, n);
        System.out.println("The maximum value is "+ans);
    }

	static double fractionalKnapsack(int W, Item arr[], int n) {

		Arrays.sort(arr, new ItemComparator());

		int currWeight = 0;
		double finalValue = 0.0;

		for (int i = 0; i < n; i++) {

			if (currWeight + arr[i].weight <= W) {
				currWeight += arr[i].weight;
				finalValue += arr[i].value;
			} else {
				int remaining = W - currWeight;
				finalValue += (double) arr[i].value /  arr[i].weight * remaining;
				break;
			}
		}
		return finalValue;
	}
}

class ItemComparator implements Comparator<Item> {
	public int compare(Item a, Item b) {
		Double r1 = (double) a.value / a.weight;
		Double r2 = (double) b.value / b.weight;

		return r1 > r2 ? -1 : r1 == r2 ? 0 : 1;
	}
}

class Item {
	int value, weight;

	Item(int x, int y) {
		this.value = x;
		this.weight = y;
	}
}