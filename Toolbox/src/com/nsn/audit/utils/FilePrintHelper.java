package com.nsn.audit.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;

public class FilePrintHelper {
	static Logger log = LogManager.getLogger("FilePrintHelper");
	public static PrintWriter nes,qos,rf = null;
	public static FilePrintHelper instance = null;
	public static Date today;
	public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private FilePrintHelper() throws IOException{
		try {
			log.info("FilePrintHelper created");
			nes = new PrintWriter(new FileWriter("NEs.csv", false));
			nes.println("Date,Name,IP,Ring,Type,VFE,Status,Disconnections,ACM_profile,Most_Robust_Profile,Synch");
			PrintWriter qos = new PrintWriter(new FileWriter("QoS.csv", false));
			PrintWriter rf = new PrintWriter(new FileWriter("RF.csv", false));
			today = new Date();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static FilePrintHelper getInstance(){
		try {
			if(instance == null) {
				synchronized(FilePrintHelper.class){
					instance = new FilePrintHelper();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}
	public void WriteToFile(NE ne){
		
		synchronized(instance){
			nes.println(dateFormat.format(today)+","+ne.getName()+","+ne.getIP()+","+ne.getLocation()+","+ne.getType()+","+ne.getVFE()+","+ne.getConnStatus()+","+ne.getDisconnections()+
					","+ne.getParam("ACM")+","+ne.getParam("Most_Robust_Profile")+","+ne.getParam("Synch"));

		}
	}
}
