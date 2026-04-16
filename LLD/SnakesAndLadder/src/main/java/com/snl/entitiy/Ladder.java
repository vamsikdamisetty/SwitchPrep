package com.snl.entitiy;

public class Ladder extends BoardEntity{


    public Ladder(int start, int end) {
        super(start, end);

        if(start >= end){
            throw new IllegalArgumentException("Ladder cannot to pull the player down");
        }
    }
}
