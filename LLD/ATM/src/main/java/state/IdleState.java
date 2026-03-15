package state;

import constants.OperationType;
import service.ATMSystem;

public class IdleState implements  ATMState{
    @Override
    public void insertCard(ATMSystem atmSystem, String cardId) {
        if(atmSystem.validateCard(cardId)){
            System.out.println("Card Validated Successfully");
            atmSystem.moveState(new InsertedState());
        }else{
            eject(atmSystem);
        }
    }

    @Override
    public void enterPin(ATMSystem atmSystem, String pin) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void selectOperation(ATMSystem atmSystem, OperationType operation, int... args) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void eject(ATMSystem atmSystem) {
        System.out.println("Ending session. Card has been ejected. Thank you for using our ATM.\n");
        atmSystem.resetSystem();
    }
}
