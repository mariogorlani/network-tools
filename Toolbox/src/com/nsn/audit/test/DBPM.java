package com.nsn.audit.test;

import java.util.ArrayList;
import java.util.Iterator;

import com.nsn.audit.utils.NVDBReader;

public class DBPM {

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		NVDBReader nvdb = new NVDBReader("uknetvab", "NorthServer1");
		System.out.println(System.currentTimeMillis());
		ArrayList<String> all = nvdb.getAllPerformanceData("pm_15m_rspi","RLTM_min");
		for (Iterator iterator = all.iterator(); iterator.hasNext();) {
			System.out.println((String) iterator.next());
			
		}
		System.out.println(System.currentTimeMillis());
	}

}
