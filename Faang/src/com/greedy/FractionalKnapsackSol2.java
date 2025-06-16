package com.greedy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FractionalKnapsackSol2 {

	/*
	 * Time Complexity: O(n + n log n + n). O(n log n) to sort the items and O(n) to iterate through all the items for calculating the answer.
	 * Space Complexity: O(n)
	 * 
	 * Intuition: Idea is to take the items based on the value per 1 wt 
	 * If a item has highest value for 1 unit of wt then that should be picked first
	 * Greedily we are choosing the item 
	 */
	double fractionalKnapsack(int[] values, int[] weights, int W) {

		List<KnapsackItem> list = new ArrayList<KnapsackItem>();
		for (int i = 0; i < weights.length; i++) {
			list.add(new KnapsackItem(values[i], weights[i]));
		}

		list.sort(Comparator.comparingDouble(KnapsackItem::getVpw).reversed());
		double maxValue = 0;
		for (KnapsackItem knapsack : list) {
			if (knapsack.weight <= W) {
				maxValue += knapsack.value;
				W -= knapsack.weight;
			} else {
				maxValue += (W * knapsack.getVpw());
				break;
			}
		}
		return maxValue;
	}
}

class KnapsackItem {
	int value;
	int weight;
	double vpw;

	public KnapsackItem(int value, int weight) {
		super();
		this.value = value;
		this.weight = weight;
		this.vpw = (double) value / weight;
	}

	public double getVpw() {
		return vpw;
	}

	public void setVpw(double vpw) {
		this.vpw = vpw;
	}

}