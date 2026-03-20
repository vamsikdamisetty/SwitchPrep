package com.java.equalshashcodecontract;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HashcodeImplemented {

    public static void main(String[] args) {
        Set<Student> set = new HashSet<>();


        Student s1 = new Student(1, "Vamsi");
        Student s2 = new Student(1, "Vamsi");

        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

        set.add(s1);
        set.add(s2);
            /*
                Problem

                Same bucket (hashCode same) ✅

                But equals() = reference check ❌
                👉 Treated as different → duplicates allowed
             */
        System.out.println(set.size());
    }


    static class Student {
        int id;
        String name;

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int hashCode(){
            return Objects.hash(name,id);
        }
    }

}
