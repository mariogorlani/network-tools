package com.nsn.audit.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogWriter {
	static Logger log = LogManager.getLogger();
	public static void main(String[] args) {
		System.out.println("test done " + log.getName() +" ");
		log.info("test");
		log.error("error");
		log.debug("debug test");

	}

}
