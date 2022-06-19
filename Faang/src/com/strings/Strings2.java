package com.strings;

import java.util.ArrayList;
import java.util.List;

public class Strings2 {

    public static void main(String args[]) {
        String text = "aaabcxyzaaaabczaaczabbaaaaaabc";
        String pattern = "aaabc";
        Strings2 s = new Strings2();
        List<Integer> result = s.matchPattern(text.toCharArray(), pattern.toCharArray());
        result.forEach(System.out::println);
    }
    
    /**
     * Returns list of all indices where pattern is found in text.
     */
    public List<Integer> matchPattern(char text[], char pattern[]) {
        char newString[] = new char[text.length + pattern.length + 1];
        int i = 0;
        for(char ch : pattern) {
            newString[i] = ch;
            i++;
        }
        newString[i] = '$';
        i++;
        for(char ch : text) {
            newString[i] = ch;
            i++;
        }
        List<Integer> result = new ArrayList<>();
        int Z[] = calculateZ(newString);

        for(i = 0; i < Z.length ; i++) {
            if(Z[i] == pattern.length) {
                result.add(i - pattern.length - 1);
            }
        }
        return result;
    }

    /*
     * Z algorithm to pattern matching
	 *
	 * Time complexity - O(n + m)
	 * Space complexity - O(n + m)
     */
	public int[] calculateZ(char[] input) {
		
		int[] z = new int[input.length];
		
		int l = 0,r =0;
		
		for(int k = 1;k<input.length;k++) {
			
			if(k > r) {
				l=r=k;
				while(r < input.length && input[r] == input[r-l]) {
					r++;
				}
				z[k] = r-l;
				r--;
			}else {
				int k1 = k - l;
				
				if(z[k1] < r - k + 1) {
					z[k] = z[k1];
				}else {
					l = k;
					while(r < input.length && input[r] == input[r-l]) {
						r++;
					}
					z[k] = r-l;
					r--;
				}
			}
		}
		
		return z;
	}
}
