package com.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class Item{
	int val;
	int wt;
	double itemVal;

	Item(int val,int wt){
		this.val = val;
		this.wt = wt;
		itemVal = (double) val/wt;
	}

	double getItemVal(){
		return itemVal;
	}
}
public class FractionalKnapsack {
	
	/*
	 * Time Complexity: O(n log n + n). O(n log n) to sort the items and O(n) to iterate through all the items for calculating the answer.
	 * Space Complexity: O(1), no additional data structure has been used.
	 */
    public static void main(String args[])
    {
        int n = 3, capacity = 50;
//        Item arr[] = {new Item (100,20),new Item(60,10),new Item(120,30)};
//        double ans = fractionalKnapsack(weight, arr, n);
		int[] val = {100,60,120};
		int[] wt = {20,10,30};
        System.out.println("The maximum value is "+fractionalKnapsack(val,wt,capacity));
    }

	public static double fractionalKnapsack(int[] val, int[] wt, int capacity) {

		List<Item> list = new ArrayList<>();
		for(int i=0;i<val.length;i++){
			list.add(new Item(val[i],wt[i]));
		}

		list.sort(Comparator.comparingDouble(Item::getItemVal).reversed());

		double maxVal=0.0;
		for(Item item : list){
			if(capacity >= item.wt){
				capacity -= item.wt;
				maxVal += item.val;
			}else{
				maxVal += item.itemVal * capacity;
				break;
			}
		}
		return maxVal;
	}


/*
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
	}*/
}


/*
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
}*/