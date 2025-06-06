package com.coffeepoweredcrew.singleton;

/**
 * This class demonstrates singleton pattern using Double Checked Locking or "classic" singleton.
 * This is also a lazy initializing singleton.
 * Although this implementation solves the multi-threading issue with lazy initialization using volatile
 * and double check locking, the volatile keyword is guaranteed to work only after JVMs starting with
 * version 1.5 and later.
 */
public class LazyRegistryWithDCL {

    private LazyRegistryWithDCL() {
    	
    }
    
    private static volatile LazyRegistryWithDCL INSTANCE;//volatile is to make sure the instance is fetched from main memory and not cache
    
    public static LazyRegistryWithDCL getInstance() {
    	if(INSTANCE == null) {
    		synchronized (LazyRegistryWithDCL.class) {
				if(INSTANCE == null) {//this is Double check Locking as even after reaching inside synchronized the previous thread might have already instantiated
					INSTANCE = new LazyRegistryWithDCL();				
				}
			}
    	}
    	return INSTANCE;
    }
}
