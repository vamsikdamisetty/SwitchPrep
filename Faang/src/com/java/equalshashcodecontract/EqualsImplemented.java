package com.java.equalshashcodecontract;

import java.util.HashSet;
import java.util.Set;

public class EqualsImplemented {

        public static void main(String[] args) {
            Set<StudentEquals> set = new HashSet<>();


            StudentEquals s1 = new StudentEquals(1, "Vamsi");
            StudentEquals s2 = new StudentEquals(1, "Vamsi");

            System.out.println(s1.hashCode());
            System.out.println(s2.hashCode());

            System.out.println(s1.equals(s2));
            set.add(s1);
            set.add(s2);
            /*
                equals() says both are same ✅

                But hashCode() is different ❌
                👉 Goes to different bucket → lookup fails

                💡 Key Insight:
                HashMap → first hashCode(), then equals()
             */
            System.out.println(set.size()); // 2 beacuse it treats as two different objects
        }

    }

    class StudentEquals {
        int id;
        String name;

        StudentEquals(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public boolean equals(Object o){
            if(this == o) return true;
            if (!(o instanceof StudentEquals)) return false;
            StudentEquals s = (StudentEquals)o;
            return (s.id == this.id) && this.name.equals(s.name);
        }


    }
