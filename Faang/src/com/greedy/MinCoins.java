package com.greedy;

import java.util.ArrayList;
import java.util.List;

/*
 * Time Complexity:O(V)
 * Space Complexity:O(1)
 */
public class MinCoins {
	public static void main(String[] args) {

		int value = 49;
		List<Integer> ans = new ArrayList<>();
		int coins[] = { 1, 2, 5, 10, 20, 50, 100, 500, 1000 };
		int n = coins.length;
		
		for (int i = n-1; i >= 0 ; i--) {
			
			while(coins[i] <= value) {
				value -= coins[i];
				ans.add(coins[i]);
			}
			
			if(value == 0) break;
		}

		System.out.println("The minimum number of coins is " + ans.size());
		System.out.println("The coins are ");
		ans.forEach(e->System.out.println(e + " "));
	}
}








