package state;

import constants.OperationType;
import service.ATMSystem;

public interface ATMState {
    void insertCard(ATMSystem atmSystem, String cardId);
    void enterPin(ATMSystem atmSystem, String pin);
    void selectOperation(ATMSystem atmSystem, OperationType operation, int... args);
    void eject(ATMSystem atmSystem);
}
