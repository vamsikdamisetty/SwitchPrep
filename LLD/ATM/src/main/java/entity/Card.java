package entity;

public class Card {

    private String cardId;
    private String pin;
    private String AccountId;

    public Card(String cardId, String pin, String accountId) {
        this.cardId = cardId;
        this.pin = pin;
        AccountId = accountId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
