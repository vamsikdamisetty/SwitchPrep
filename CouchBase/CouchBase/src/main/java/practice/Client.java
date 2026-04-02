package practice;



public class Client {
    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter();

        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1002"));

        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1001"));
        System.out.println(rateLimiter.allowUser("1001"));

        Thread.sleep(5000);

        System.out.println(rateLimiter.allowUser("1001"));
    }
}
