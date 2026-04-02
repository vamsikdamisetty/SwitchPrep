package com.cb.producerconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/*
Producer Consumer standalone implementation
using BlockingQueue
 */
public class ShareResource {

    BlockingQueue<Integer> shareQueue = new LinkedBlockingQueue<>(2);

    public void produce(int data) throws InterruptedException {
        // Put waits till capacity is available
        shareQueue.put(data);
        System.out.println("produced "  + data);
    }

    public void consume(){
        try {
            //talke waits till item is available to consume
            Integer taken = shareQueue.take();
            System.out.println("Consumed" + taken);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


    public static void main(String[] args) throws InterruptedException {

        ShareResource shareResource = new ShareResource();
         Thread producer = new Thread(
             ()->{
             for(int i=0;i<10;i++){

                 try {
                     shareResource.produce(i);
                     Thread.sleep(100);
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
             }
         } ) ;

         producer.start();
         Thread consumer = new Thread(()->{
             for(int i=0;i<10;i++){
                 shareResource.consume();
                 try {
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
             }
         });

         consumer.start();

         producer.join();
         consumer.join();
    }
}
