package state;

import constants.OperationType;
import service.ATMSystem;

public class AuthenticatedState implements ATMState{
    @Override
    public void insertCard(ATMSystem atmSystem, String cardId) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void enterPin(ATMSystem atmSystem, String pin) {
        System.out.println("Operation not allowed");
    }

    @Override
    public void selectOperation(ATMSystem atmSystem, OperationType operation, int... args) {
        switch (operation){
            case DEPOSIT:
                if (args.length == 0 || args[0] <= 0) {
                    System.out.println("Error: Invalid deposit amount specified.");
                    break;
                }
                int amountToDeposit = args[0];
                System.out.println("Processing deposit for $" + amountToDeposit);
                atmSystem.depositCash(amountToDeposit);
                break;
            case WITHDRAWL:
                if (args.length == 0 || args[0] <= 0) {
                    System.out.println("Error: Invalid withdrawal amount specified.");
                    break;
                }
                int amountToWithdraw = args[0];

                double accountBalance = atmSystem.getAccountBalance();

                if (amountToWithdraw > accountBalance) {
                    System.out.println("Error: Insufficient balance.");
                    break;
                }

                System.out.println("Processing withdrawal for $" + amountToWithdraw);
                // Delegate the complex withdrawal logic to the ATM's dedicated method
                atmSystem.withdrawCash(amountToWithdraw);
                break;
            case CHECK_BALANCE:
                int acountBalance = atmSystem.getAccountBalance();
                System.out.println("Account Balance: "+ acountBalance);
                break;
            default:
                System.out.println("InvalidState");
                eject(atmSystem);
        }

        // End the session after one transaction
        System.out.println("Transaction complete.");
        eject(atmSystem);
    }

    @Override
    public void eject(ATMSystem atmSystem) {
        System.out.println("Ending session. Card has been ejected. Thank you for using our ATM.\n");
        atmSystem.moveState(new IdleState());
        atmSystem.resetSystem();
    }
}
