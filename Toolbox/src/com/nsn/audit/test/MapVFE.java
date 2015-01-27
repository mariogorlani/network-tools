package com.nsn.audit.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import com.nsn.audit.utils.MDBReader;

public class MapVFE {

	public static void main(String[] args) {
		System.out.println(new Timestamp(System.currentTimeMillis()));
		ArrayList<String> all = new ArrayList<String>();
		MDBReader mdb = new MDBReader("uknetvab", "north_slave1");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvac", "north_slave2");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvad", "north_slave3");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvae", "north_slave4");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvag", "south_slave1");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvah", "south_slave2");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvai", "south_slave3");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		mdb = new MDBReader("uknetvaj", "south_slave4");
		System.out.println(new Timestamp(System.currentTimeMillis()));
		all.addAll(mdb.extractMapVFEs());
		for (Iterator iterator = all.iterator(); iterator.hasNext();) {
			System.out.println((String) iterator.next());
		}
	}

}
