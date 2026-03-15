package repository;

import entity.Account;
import entity.Card;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BankService {

    private Map<String, Account> accounts = new ConcurrentHashMap<>();
    private Map<String, Card> cards = new ConcurrentHashMap<>();

    public BankService(){
        //load cards and accounts

        accounts.put("ACC1",new Account("ACC1","Vamsi",1000));
        accounts.put("ACC2",new Account("ACC2","Krishna",2000));

        cards.put("1234-5678-9012-3456",new Card("1234-5678-9012-3456","1234","ACC1"));
        cards.put("123-456-782",new Card("123-456-782","1235","ACC2"));

    }

    public void checkBalance(String accountId){
        Account account =  accounts.getOrDefault(accountId,null);
        if(account != null){
            System.out.println("Hi " + account.getName() + "!  your account balance is : " + account.getBalance() );
        }
    }

    public int getAccountBalance(String accId){
        return this.accounts.get(accId).getBalance();
    }

    public void withDraw(String accountId,int amount){
        Account account =  accounts.getOrDefault(accountId,null);
        if(account != null){
            account.setBalance(account.getBalance() - amount);
        }
    }

    public void deposit(String accountId,int amount){
        Account account =  accounts.getOrDefault(accountId,null);
        if(account != null){
            account.setBalance(account.getBalance() + amount);
        }
    }

    public String getAccountId(String cardId){
        if(!cards.containsKey(cardId)){
            return null;
        }
        return cards.get(cardId).getAccountId();
    }

    public boolean authenticate(String cardId,String pin){
        Card card = cards.get(cardId);
        if(card == null){
            System.out.println("Error : Invalid Card");
            return false;
        }
        return pin.equals(card.getPin());

    }
}
