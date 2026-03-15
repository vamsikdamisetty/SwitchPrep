package state;

import constants.OperationType;
import service.ATMSystem;

public class InsertedState implements ATMState{

    @Override
    public void insertCard(ATMSystem atmSystem, String cardId) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void enterPin(ATMSystem atmSystem, String pin) {
        if(atmSystem.authenticate(pin)){
            System.out.println("Pin validated Successfully");
            atmSystem.moveState(new AuthenticatedState());
        }else{
            System.out.println("Enter a valid PIN");
            eject(atmSystem);
        }
    }

    @Override
    public void selectOperation(ATMSystem atmSystem, OperationType operation, int... args) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void eject(ATMSystem atmSystem) {
        System.out.println("Ending session. Card has been ejected. Thank you for using our ATM.\n");
        atmSystem.moveState(new IdleState());
        atmSystem.resetSystem();
    }
}
