package com.nsn.audit.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;
import com.nsn.audit.test.Excel;
import com.nsn.audit.utils.MDBReader;
import com.nsn.audit.utils.NVDBReader;
import com.nsn.audit.utils.NeConnector;
import com.nsn.audit.xml.XMLReader;

/**
 * 1. Connect to NetViewer database to read the NE IPs
 * 2. Parse xml for NEs template
 * 3. Validate NE configuration against design template
 * 
 * @author Mario Gorlani 2014 mario.gorlani@gmail.com
 *
 */
public class NetworkAudit {

	NVDBReader nvDBReader;
	XMLReader xmlReader;
	NeConnector equipmentConnector;
	private String servers;
	private String xmlInput;
	private String path;
	static Logger log = LogManager.getLogger("Main");

	/**
	 * The application main entry point.
	 *
	 * @param args the command-line parameters
	 */
	public static void main(String args[]) {
		try {
			log.info("Network audit.");
			new NetworkAudit(args).start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public NetworkAudit(String[] args) {
		//servers = "uknetvah";
		servers = "uknetvab,uknetvac,uknetvad,uknetvae,uknetvag,uknetvah,uknetvai,uknetvaj";
		xmlInput = "devices.xml";
		path = "D:\\misc\\CRAMER\\";
		//TODO improve args parsing with options label
		if (args.length == 2) {
			servers = args[0];
			xmlInput = args[1];
		}
		log.info("Network Audit running on the servers: " + servers);
	} 

	/**
	 * Starts this application.
	 *
	 * @param args is the command-line arguments
	 */
	public  void start() throws Exception {
		log.info("started at: " + new Date());
		xmlReader = new XMLReader(xmlInput);
		//TODO add a statistics object to measure the remaining NEs to be collected and the timing of the collection
		HashMap<String, ArrayList<NE>> ringsNEList = createNEList(servers);
		int ringsNumber = ringsNEList.size();
		//ExecutorService executor = Executors.newFixedThreadPool(GenericDefinitions.maxThreadPool);
		log.info("Creating "+ ringsNumber+" threads");
		ExecutorService executor = Executors.newFixedThreadPool(ringsNumber);
		for (Map.Entry<String, ArrayList<NE>> ring : ringsNEList.entrySet()) {
			Runnable equipmentConnector = new NeConnector(xmlReader,ring.getValue());
			Thread worker = new Thread(equipmentConnector);
			worker.setName(ring.getKey());
			executor.execute(equipmentConnector);	
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			Thread.sleep(10);
		}
		log.info("end: " + new Date());
		log.info("Finished all threads");
		printAll(ringsNEList);
		//Excel.printAllExcel(xlsOutput,ringsNEList);

	}

	/**
	 *  returns the map name from the vwserver.ini in NetViewer folder
	 * @param server
	 * @return
	 */
	protected String getMapNamefromFile(String server) {
		Properties prop = new Properties();
		InputStream input = null;
		String filename = "\\\\"+server+"\\d$\\Program Files (x86)\\Nokia Siemens Networks\\NetViewer\\Vwserver.ini";
		String mapname = "";
		try {
			input = new FileInputStream(filename);
			// load a properties file
			prop.load(input);

			// get the property value and print it out
			mapname = prop.getProperty("Map");
			mapname= mapname.substring(0, mapname.lastIndexOf(".mdb"));
			log.debug("server:" +server+"; map:"+mapname);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapname;
	}


	protected HashMap<String, ArrayList<NE>> createNEList(String s) {
		HashMap<String,ArrayList<NE>> ringsNEList = new HashMap<String,ArrayList<NE>>();
		try {
			String[] servers = s.split(",");
			String mapName = "";
			for (int i = 0; i < servers.length; i++) {
				HashSet<String> ringsName = new HashSet<String>();
				HashMap<String, NE> neList = new HashMap<String,NE>();
				mapName = getMapNamefromFile(servers[i]);
				MDBReader mdb = new MDBReader(servers[i], mapName);
				NVDBReader nvDB = new NVDBReader(servers[i], mapName);
				neList = mdb.extractNEs();
				neList = mdb.extractVFEs(neList);
				neList = nvDB.extractType(neList);
				neList = nvDB.extractDisconnections(neList);
				Iterator<Entry<String, NE>> itr = neList.entrySet().iterator();
				while (itr.hasNext()) {
					//if (((Map.Entry<String, NE>)itr.next()).getValue().getLocation()!="")
					ringsName.add(((Map.Entry<String, NE>)itr.next()).getValue().getLocation());
				}
				for (String ring : ringsName) {
					ArrayList<NE> neListinRing = new ArrayList<NE>();
					itr = neList.entrySet().iterator();
					while (itr.hasNext()) {
						NE ne = ((Map.Entry<String, NE>)itr.next()).getValue();
						if (ne.getLocation().equals(ring)) neListinRing.add(ne);
					}
					ringsNEList.put(ring, neListinRing);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ringsNEList;
	}

	/**
	 * Print NE List with results
	 */
	public void printAll(HashMap<String, ArrayList<NE>> ringsNEList) {
		try {
			log.info("Print all called");
			PrintWriter NEs = new PrintWriter(path+"NEs.csv", "UTF-8");
			NEs.println("Date,Name,IP,Ring,Type,VFE,Status,Disconnections,ACM_Static_Tx_Profile,ACM_Most_Robust_profile,Synch");
			PrintWriter QoS = new PrintWriter(path+"QoS.csv", "UTF-8");
			QoS.println("Date,Name,IP,QoSCriteriaEnabled,IPPriority,WFQSchedulerScheme,StaticMulticastTableEn,StrictPriorityQueueNum,"+
					"WFQSchedulerQ8Weight,WFQSchedulerQ7Weight,WFQSchedulerQ6Weight,WFQSchedulerQ5Weight,WFQSchedulerQ4Weight,WFQSchedulerQ3Weight,"+
					"WFQSchedulerQ2Weight,WFQSchedulerQ1Weight,EtherType,JumboFrame,VLAN_Filtering,MAC_DB_Aging");
			PrintWriter RF = new PrintWriter(path+"RF.csv", "UTF-8");
			RF.println("Date,Name,IP,Tx,Rx,Max_Tx_Power,ATPC,Channel");
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			for (Map.Entry<String, ArrayList<NE>> ring : ringsNEList.entrySet()) {
				String ringName = ring.getKey();
				ArrayList<NE> neList = ring.getValue();
				Iterator<NE> itr = neList.iterator();
				log.info("Start printing for " + ringName);
				while (itr.hasNext()) {
					NE ne = itr.next();
					NEs.println(dateFormat.format(date)+","+ne.getName()+","+ne.getIP()+","+ringName+","+
							ne.getType()+","+
							ne.getVFE()+","+
							ne.getConnStatus()+","+
							ne.getDisconnections()+","+
							ne.getParam("ACM_Static_Tx_Profile")+","+
							ne.getParam("ACM_Most_Robust_profile")+","+
							ne.getParam("Synch"));

					if (ne.getType().contains("Radio")) {
						RF.println(dateFormat.format(date)+","+ne.getName()+","+ne.getIP()+","+
								ne.getParam("Tx")+","+
								ne.getParam("Rx")+","+
								ne.getParam("Max_Tx_Power")+","+
								ne.getParam("ATPC")+","+
								ne.getParam("Channel"));

						QoS.println(dateFormat.format(date)+","+ne.getName()+","+ne.getIP()+","+
								ne.getParam("QoSCriteriaEnabled")+","+
								ne.getParam("IPPriority")+","+
								ne.getParam("WFQSchedulerScheme")+","+
								ne.getParam("StaticMulticastTableEn")+","+
								ne.getParam("StrictPriorityQueueNum")+","+
								ne.getParam("WFQ_Q8Weight")+","+
								ne.getParam("WFQ_Q7Weight")+","+
								ne.getParam("WFQ_Q6Weight")+","+
								ne.getParam("WFQ_Q5Weight")+","+
								ne.getParam("WFQ_Q4Weight")+","+
								ne.getParam("WFQ_Q3Weight")+","+
								ne.getParam("WFQ_Q2Weight")+","+
								ne.getParam("WFQ_Q1Weight")+","+
								ne.getParam("EtherType")+","+
								ne.getParam("JumboFrame")+","+
								ne.getParam("VLAN_Filtering")+","+
								ne.getParam("MAC_DB_Aging")
								);
					}
				}
			}
			NEs.close();
			QoS.close();
			RF.close();
		} catch (Exception e) {
			log.error("PrintAll failed " + e);
		}
	}




}