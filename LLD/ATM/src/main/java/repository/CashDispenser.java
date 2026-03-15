package repository;

import dispenser.DispenseChain;

public class CashDispenser {
    DispenseChain chain;

    public CashDispenser(DispenseChain chain){
        this.chain = chain;
    }

    public synchronized boolean canDispense(int amount){
        return chain.canDispense(amount);
    }

    public synchronized void dispense(int amount){
        this.chain.dispense(amount);
    }
}
