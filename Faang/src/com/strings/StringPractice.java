package com.strings;

import java.util.Arrays;

public class StringPractice {

    public static void main(String[] args) {
        String s = "Vamsi is an    amazing  coder";

        String[] strings = s.split(" ");
        char[] c = new char[2];

        System.out.println(String.join("+",strings));
        String res = "";
        for(int i=strings.length - 1 ; i>= 0 ; i--){
            String ss = strings[i].toLowerCase().trim();
            if(!ss.isEmpty()){
                res += ss + " ";
            }
        }
        System.out.println(res.substring(0,res.length()-1));
    }

}
