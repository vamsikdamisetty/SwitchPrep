package com.stackandqueue;

import java.util.HashMap;
import java.util.Map;

// Least Recently Used (LRU) cache.
/*
 * get - > O(1)
 * put - > O(1)
 */
public class LRU {

	public static void main(String[] args) {
		LRUCache lru = new LRUCache(2);
		
		System.out.println("lru.get(1) : " + lru.get(1)); 
		lru.put(1, 5);
		lru.put(2, 6);
		lru.put(2, 15);
		
		System.out.println("lru.put(1, 5);\r\n" + 
				"lru.put(2, 6);\r\n" + 
				"lru.put(2, 15);\r\n" );
		
		System.out.println("lru.get(2):" + lru.get(2));
		
		System.out.println("lru.put(3, 20);");
		
		lru.put(3, 20);
		System.out.println("lru.get(2):"+lru.get(1));
	}
}


class LRUCache {

    int capacity;
    Node head,tail;
    Map<Integer,Node> map = new HashMap<>();
    
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node(0,0);
        tail = new Node(0,0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if(map.containsKey(key)){
            Node node = map.get(key);
            remove(node);
            insert(node);
            return node.value;
        }else{
            return -1;
        }
    }
    
    public void put(int key, int value) {
        
        if(map.containsKey(key)){
            remove(map.get(key));
        }
        
        if(map.size() == capacity){
            remove(tail.prev);
        }
        
        insert(new Node(key,value));
    }
    
    void remove(Node node){
        map.remove(node.key);
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }
    
    void insert(Node node){
        map.put(node.key,node);
        Node nodeNext = head.next;
        head.next = node;
        node.prev = head;
        node.next = nodeNext;
        nodeNext.prev = node;
    }
    
    class Node{
        int key,value;
        Node prev,next;
        
        Node(int _key,int _value){
            key = _key;
            value = _value;
        }
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */