package practice;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private ConcurrentHashMap<String,TokenBucket> buckets = new ConcurrentHashMap();

    public boolean allowUser(String userId){
        buckets.computeIfAbsent(userId,
                (id)-> {

                    System.out.println(id);
                    return new TokenBucket(10,5);
                });

        return buckets.get(userId).allowUser();
    }


}
