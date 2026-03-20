package com.java.equalshashcodecontract;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BothImplementedInconsistently {
    public static void main(String[] args) {
        Set<Student> set = new HashSet<>();


        Student s1 = new Student(1, "Vamsi");
        Student s2 = new Student(1, "Krishna");

        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

        set.add(s1);
        set.add(s2);
            /*
                equals() → TRUE (same id)

                hashCode() → DIFFERENT (name differs)

                👉 Same object logically, but different buckets → broken map
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

        public boolean equals(Object o){
            Student s = (Student) o;
            return id == s.id; // only id
        }

        public int hashCode(){
            return Objects.hash(name);
        }
    }

}
