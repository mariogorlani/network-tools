package com.nsn.audit.test;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrecyTest {

	public final static int THREADS = 3;

	public static Map<String, Integer> threadSafeMap = null;
	public static long averageTime = 0;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			//threadSafeMap = new Hashtable<String, Integer>();
			// threadSafeMap = Collections
			// .synchronizedMap(new HashMap<String, Integer>());
			threadSafeMap = new ConcurrentHashMap<String, Integer>();

			long time = System.nanoTime();
			ExecutorService service = Executors.newFixedThreadPool(THREADS);
			String s = "thread name ";
			for (int j = 0; j < THREADS; j++) {
				System.out.println("Running " + s + i +"."+j);
				service.execute(new Runnable() {
					@Override
					public void run() {
						
						for (int i = 0; i < 1000000; i++) {
							// Test to get and insert a random 
							//Integer element.
							Integer num = (int) Math.ceil(
									Math.random() * 100000);
							Integer value = threadSafeMap.get(String
									.valueOf(num));
							threadSafeMap.put(String.valueOf(num), num);
						}
					}
				});
			}

			// Make sure the executor accept no new threads.
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			long timeUsed = (System.nanoTime() - time) / 1000000L;
			averageTime += timeUsed;
			System.out.println("Allthreads are completed in "
					+ timeUsed + " ms");
		}
		System.out.println("The average time is " + averageTime / 10 + " ms");
	}
}
