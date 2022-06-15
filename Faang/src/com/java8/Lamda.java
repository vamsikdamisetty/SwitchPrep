package com.java8;

import java.util.function.BiConsumer;

public class Lamda {

	public static void main(String[] args) throws InterruptedException {
		sam s = (a,b) -> System.out.println(a+b);
		s.add(1, 5);
		
		Runnable r = ()->{ 
			for(int i=0;i<100;i++) 
				System.out.println(i); 
		};
		Thread t = new Thread(r);
		t.start();
		
		System.out.println("i1");
		System.out.println("i1");
		System.out.println("i1");
		System.out.println("i1");
		System.out.println("i1");
		System.out.println("i1");
		System.out.println("Interupt");
		
		
		BiConsumer<Integer, String> biConsumer = (a,b)-> System.out.println(a+b);
		biConsumer.accept(0, "Hero_Vamsi");
		
	}
	
	
}


interface sam{
	void add(int a,int b);
}

//interface mul
