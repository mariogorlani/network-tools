package com.nsn.audit.xml;

import java.util.HashMap;

public class XNE {

	private HashMap<String, XParam> params;

	public HashMap<String, XParam> getParams() {
		return params;
	}
	public void setParams(HashMap<String, XParam> params) {
		this.params = params;
	}
	public String toString(){
		return params.toString();
	}
}
