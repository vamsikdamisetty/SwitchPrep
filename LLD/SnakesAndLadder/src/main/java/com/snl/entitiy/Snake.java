package com.snl.entitiy;

public class Snake extends BoardEntity{

    public Snake(int start, int end) {
        super(start, end);

        if(start <= end){
            throw new IllegalArgumentException("Snake has to pull the player down");
        }
    }
}
