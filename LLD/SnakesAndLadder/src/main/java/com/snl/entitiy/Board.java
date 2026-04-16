package com.snl.entitiy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private int size;
    private Map<Integer,Integer> snakesAndLadders;

    public Board(int size,List<BoardEntity> boardEntities){
        this.size = size;
        snakesAndLadders = new HashMap<>();
        boardEntities.stream()
                .forEach(entity -> snakesAndLadders.put(entity.getStart(),entity.getEnd()));
    }

    //get final position
    public Integer getFinalPosition(int position){
        return snakesAndLadders.getOrDefault(position,position);
    }

    public int getSize() {
        return size;
    }
}
