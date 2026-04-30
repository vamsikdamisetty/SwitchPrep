package com.coffeepoweredcrew.singleton;

/**
 * Singleton pattern using lazy initialization holder class. This ensures that, we have a lazy initialization
 * without worrying about synchronization.
 * <p>
 * This is a thread-safe lazy singleton using the Initialization-on-Demand Holder idiom.
 * It leverages JVM class loading guarantees to create the instance only when needed, without synchronization overhead.
 *
 * Using a synchronized method makes the singleton thread-safe but introduces locking overhead on every access.
 * Even after initialization, threads must compete for the lock, which affects performance.
 * The IODH idiom avoids this by leveraging JVM class initialization guarantees.
 *
 * Inner classes are compiled into separate .class files
 * They are NOT loaded with the outer class
 * They are loaded only when first actively used
 * Follows standard JVM lazy class loading
 */
public class LazyRegistryIODH {

	private LazyRegistryIODH() {
		System.out.println("In LazyRegistryIODH singleton");
	}

	/*
		Before Java 16
		To access inner class we need the instance of parent class
	 */
	private static class RegistryHolder {
		static LazyRegistryIODH INSTANACE = new LazyRegistryIODH();
	}


	public static LazyRegistryIODH getInstance() {
		return RegistryHolder.INSTANACE;
	}
	/*
	When this method is called:

		JVM loads RegistryHolder

		Static variable initializes

		Singleton instance is created

		Returned to caller

		If multiple threads call this:

		JVM ensures class initialization happens once

		So only one instance is created
	 */
}
