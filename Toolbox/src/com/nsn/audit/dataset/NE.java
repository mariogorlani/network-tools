package com.nsn.audit.dataset;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.nsn.audit.utils.GenericDefinitions;

public class NE {
	private int ID;
	private int SUB_ID;
	private Timestamp time;
	private String name;
	private String location;
	private String IP;
	private String svr;
	private String type;
	private String sysName;
	private HashMap<String,Param> params;
	private String connStatus;
	private String VFE;
	private int disconnections;
	private int	retry;


	public NE(String name, String IP)
	{
		this.name = name;
		this.IP = IP;
		this.retry = 0;
		this.location = "";
		this.svr = "";
		this.type = "";
		this.connStatus = GenericDefinitions.Disconnected;
		this.VFE = "";
		this.disconnections = 0;
	}
	public int getID() {
		return ID;
	}
	public int getSUB_ID() {
		return SUB_ID;
	}

	public String getIP()
	{
		return this.IP;
	}
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIP(String iP) {
		IP = iP;
	}
	public String getName()
	{
		return name;
	}

	public HashMap<String, Param> getParams() {
		return params;
	}

	public String getParam(String name) {
		if (params!=null){
			Param p = params.get(name);
			if (p!=null) return p.toString(); 
			else return "";
		} else return "";
	}

	public void setParams(HashMap<String, Param> params) {
		this.params = params;
	}

	public String getSvr() {
		return svr;
	}

	public String getSysName() {
		return sysName;
	}



	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}
	public void setSvr(String svr) {
		this.svr = svr;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getConnStatus() {
		return connStatus;
	}
	public void setConnStatus(String connStatus) {
		this.connStatus = connStatus;
	}
	public String getVFE() {
		return VFE;
	}
	public void setVFE(String VFE) {
		this.VFE = VFE;
	}
	public int getDisconnections() {
		return disconnections;
	}
	public void setDisconnections(int d) {
		this.disconnections = d;
	}
	public String toString()
	{
		String returnString = this.name+ "," +this.IP+ ",";
		try {
			Iterator<Entry<String, Param>> it = this.getParams().entrySet().iterator();
			int size = this.getParams().size();
			int i = 0;
			while (it.hasNext()) {
				i++;
				Map.Entry<String, Param> pairs = it.next();
				returnString += pairs.getKey() + "=" + pairs.getValue().toString();
				if (i<size) returnString += ",";
			}
			return returnString;
		}
		catch(Exception e) {
			returnString += "disconnected";
			return returnString;
		}
	}
}

