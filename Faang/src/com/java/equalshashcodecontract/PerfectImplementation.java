package com.java.equalshashcodecontract;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PerfectImplementation {
    public static void main(String[] args) {
        Set<Student> set = new HashSet<>();


        Student s1 = new Student(1, "Vamsi");
        Student s2 = new Student(1, "Vamsi");

        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

        set.add(s1);
        set.add(s2);
            /*
                equals() → TRUE (same id)

                hashCode() → SAME

                👉 Same object logically, and hashcode also matches
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
            if (this == o) return true;
            if (!(o instanceof Student)) return false;
            Student s = (Student) o;
            return id == s.id && Objects.equals(name, s.name);
        }

        public int hashCode(){
            return Objects.hash(id, name);
        }
    }

}
