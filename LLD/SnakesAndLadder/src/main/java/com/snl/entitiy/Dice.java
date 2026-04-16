package com.snl.entitiy;

public class Dice {

    private int min;
    private int max;

    public Dice(int min, int max) {
        this.min = min;
        this.max = max;

        if(min < 0 || max > 12) {
            throw new IllegalArgumentException("Dice selection is not supported");
        }
    }

    public int rollDice(){
        return (int)(Math.random() * (max - min + 1) + min);
    }
}
