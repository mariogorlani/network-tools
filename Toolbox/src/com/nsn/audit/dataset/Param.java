package com.nsn.audit.dataset;

import java.sql.Timestamp;
import java.util.Arrays;

import com.nsn.audit.utils.GenericDefinitions;


public class Param {
	private String[] config;
	private Timestamp timestamp;
	private String[] expected;
	private String[] enums;

	public String[] getConfig() {
		return config;
	}

	public void setConfig(String[] config) {
		this.config = config;
	}

	@Override
	public String toString(){
		String returnString = "";
		for (int i = 0; i < config.length; i++) {
			   returnString+= config[i];
			   if (i<(config.length-1)) returnString += GenericDefinitions.RowSeparator;
			}
		return returnString;
	}

	public Timestamp getTimeStamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timeStamp) {
		this.timestamp = timeStamp;
	}

	public String[] getExpected() {
		return expected;
	}

	public void setExpected(String[] expected) {
		this.expected = expected;
	}
	
	public boolean isCompliant() {
		if (expected!=null)
			return Arrays.equals(config, expected);
		else return true;
	}

	public String[] getEnums() {
		return enums;
	}

	public void setEnums(String[] enums) {
		this.enums = enums;
	}
	
	
}
