package com.nsn.audit.xml;

import java.util.Arrays;
import java.util.HashMap;


public class XParam {
	private String type;
	private boolean order;
	private String[][] oids;
	private String[] expected;
	private HashMap<String, String> enums;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * Oids array: 0=column name; 1=oid;
	 * @return
	 */
	public String[][] getOids() {
		return oids;
	}
	public void setOids(String[][] oids) {
		this.oids = oids;
	}
	public boolean getOrder() {
		return order;
	}
	public void setOrder(boolean order) {
		this.order = order;
	}
	public String[] getExpected() {
		return expected;
	}
	public void setExpected(String[] expected) {
		this.expected = expected;
	}

	public HashMap<String, String> getEnums() {
		return enums;
	}
	public void setEnums(HashMap<String, String> enums) {
		this.enums = enums;
	}
	public String toString(){
		return "Type: "+getType()+" order: "+getOrder()+" oids: " + Arrays.toString(getOids()) + " expected: " + Arrays.toString(getExpected());
	}
}
