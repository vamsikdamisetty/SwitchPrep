package com.java.equalshashcodecontract;

import java.util.HashSet;
import java.util.Set;

public class EqualsHashCodeContractNone {

    public static void main(String[] args) {
        Set<StudentEquals> set = new HashSet<>();


        StudentEquals s1 = new StudentEquals(1, "Vamsi");
        StudentEquals s2 = new StudentEquals(1, "Vamsi");

        set.add(s1);
        set.add(s2);

        System.out.println(set.size()); // 2 beacuse it treats as two different objects
    }

}

class Student {
    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
