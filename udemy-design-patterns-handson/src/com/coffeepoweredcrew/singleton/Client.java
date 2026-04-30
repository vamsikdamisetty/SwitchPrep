package com.coffeepoweredcrew.singleton;

public class Client {

	/*
	If I need lazy initialization with zero synchronization overhead, I’d use Initialization-on-Demand Holder idiom.
	 */
	public static void main(String[] args) {
		LazyRegistryIODH singleton;
		
		
		singleton = LazyRegistryIODH.getInstance();
		LazyRegistryIODH singleton2 = LazyRegistryIODH.getInstance();
		System.out.println(singleton == singleton2);
	}

}
