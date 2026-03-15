package service;

import constants.OperationType;
import dispenser.DispenseChain;
import dispenser.NotesDispenser10;
import dispenser.NotesDispenser100;
import dispenser.NotesDispenser50;
import repository.BankService;
import repository.CashDispenser;
import state.ATMState;
import state.IdleState;

public class ATMSystem {
    private static ATMSystem instance;
    private BankService bankRepo;
    private String accountId;
    private String cardId;
    private ATMState currentState;
    CashDispenser cashDispenser;

    private ATMSystem(){
        bankRepo = new BankService();

        this.currentState = new IdleState();

        //setup Dispense chain
        DispenseChain c1 = new NotesDispenser100(20);
        DispenseChain c2 = new NotesDispenser50(30);
        DispenseChain c3 = new NotesDispenser10(50);

        c1.setNextChain(c2);
        c2.setNextChain(c3);

        cashDispenser = new CashDispenser(c1);

    }

    public synchronized static ATMSystem getInstance(){
        if(instance == null){
            instance = new ATMSystem();
        }
        return instance;
    }

    public boolean validateCard(String cardId){
        String accId =  bankRepo.getAccountId(cardId);
        if(accId == null){
            return false;
        }
        this.cardId = cardId;
        this.accountId = accId;
        return true;
    }

    public boolean authenticate(String pin){
        return  bankRepo.authenticate(cardId,pin);
    }

    public void moveState(ATMState nextState){
        this.currentState = nextState;
    }

    public void withdrawCash(int amount){
        if(bankRepo.getAccountBalance(accountId) < amount){
            System.out.println("Error: Insufficient Funds");
            return;
        }

        if(!cashDispenser.canDispense(amount)){
            System.out.println("Error: Insufficient cash available in the ATM");
        }

        bankRepo.withDraw(accountId,amount);

        try {
            cashDispenser.dispense(amount);
        } catch (Exception e) {
            bankRepo.deposit(accountId, amount); // Deposit back if dispensing fails
        }

    }

    //Loading notes into ATM can also be added
    public void depositCash(int amount){
        this.bankRepo.deposit(accountId,amount);
    }

    public void insertCard(String cardNumber) {
        currentState.insertCard(this, cardNumber);
    }

    public void enterPin(String pin) {
        currentState.enterPin(this, pin);
    }

    public void selectOperation(OperationType op, int... args) { currentState.selectOperation(this, op, args); }

//    public Card getCard(String cardNumber) {
//        return bankRepo.getCard(cardNumber);
//    }
    public int getAccountBalance(){
        return bankRepo.getAccountBalance(accountId);
    }
    public void resetSystem(){
        this.cardId = null;
        this.accountId = null;
    }
}
