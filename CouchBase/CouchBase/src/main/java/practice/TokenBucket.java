package practice;

public class TokenBucket {

    private final int capacity;

    private final int refillRate;

    double tokens;

    long lastUpdatedTime;

    public TokenBucket(int capacity,int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = refillRate;
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    public synchronized boolean allowUser() {
        refill();

        if(this.tokens >= 1){
            tokens--;
            return true;
        }else{
            System.out.println("Insufficient Token: Rate Limited");
            return false;
        }
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToLoad = (now - lastUpdatedTime )/1000 * refillRate;

        this.tokens = Math.min(capacity,tokens + tokensToLoad);
        lastUpdatedTime = now;
    }
}
