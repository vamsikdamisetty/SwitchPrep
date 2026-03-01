package com.strings;

import java.util.*;

public class GroupAnagrams {
    public static List<List<String>> groupAnagrams(String[] strs) {

        Map<String, List<String>> mp = new HashMap<>();

        for(String s:strs){
            char[] charArr = s.toCharArray();

            int[] freq = new int[26];

            for(char c:charArr){
                freq[c - 'a']++;
            }

            // It converts the array into a string representation:
            // which would be same for all the anagrams
            //"[1, 0, 0, 0, 1, 0, ..., 1, ...]"
            // This becomes our hash to group anagrams
            String key = Arrays.toString(freq);

            if(!mp.containsKey(key)){
                List<String> list = new ArrayList<>();
                mp.put(key,list);
            }
            mp.get(key).add(s);
        }
        return new ArrayList<>(mp.values());
    }


    public static void main(String[] args) {
        String[] strs = new String[]{"eat","tea","tan","ate","nat","bat"};
        System.out.println(groupAnagrams(strs));
    }
}
