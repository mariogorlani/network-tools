package com.nsn.audit.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskExample extends TimerTask {

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+" Start time:" + new Date());
		doSomeWork();
		System.out.println(Thread.currentThread().getName()+" End time:" + new Date());
	}

	// simulate a time consuming task
	private void doSomeWork() {
		try {
			System.out.println(Thread.currentThread().getName()+" working for 10sec");
			Thread.sleep(10000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		TimerTask timerTask = new TimerTaskExample();
		// running timer task as daemon thread
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
		System.out.println("TimerTask begins! :" + new Date());
		// cancel after sometime
		try {
			System.out.println(Thread.currentThread().getName()+" going to sleep 20 sec");
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.cancel();
		System.out.println("TimerTask cancelled! :" + new Date());
		try {
			System.out.println(Thread.currentThread().getName()+" going to sleep 30 sec");
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
