package com.nsn.audit.performance;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.config.NetworkAudit;
import com.nsn.audit.dataset.NE;
import com.nsn.audit.utils.NVDBReader;
import com.nsn.audit.utils.NeConnector;
import com.nsn.audit.xml.XMLReader;

class ServerConnector implements Runnable {
	String server, map, dayoffset;
	ConcurrentHashMap<String, String> data;
	static Logger log = LogManager.getLogger("Main");

	public ServerConnector(String s, String m,  String dayoffset, ConcurrentHashMap<String, String> d ){
		this.server = s;
		this.map = m;
		this.data = d;
		this.dayoffset = dayoffset;
	}
	public void run() {
		NVDBReader nvdb = new NVDBReader(server,map);
		log.info("server: "+ server);
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTM_min","RLTM_min",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTM_max","RLTM_max",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_1","RLTS_1",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_1_th","RLTS_1_th",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_2","RLTS_2",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_2_th","RLTS_2_th",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_3","RLTS_3",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_3_th","RLTS_3_th",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_4","RLTS_4",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_4_th","RLTS_4_th",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","TLTS_1","TLTS_1",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","TLTS_2","TLTS_2",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_1","RX_profile_1",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_2","RX_profile_2",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_3","RX_profile_3",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_4","RX_profile_4",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_5","RX_profile_5",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_6","Rx_profile_6",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_7","Rx_profile_7",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_10","AirLoss",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_1","Tx_profile_1",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_2","Tx_profile_2",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_3","Tx_profile_3",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_4","Tx_profile_4",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_5","Tx_profile_5",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_6","Tx_profile_6",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_7","Tx_profile_7",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InOctets","InOctets",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InDroppedPkts","InDroppedPkts",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InGoodPkts","InGoodPkts",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutOctets","OutOctets",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutDroppedPkts","OutDroppedPkts",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutGoodPkts","OutGoodPkts",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Peak_Capacity","TX_Peak_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_TX_Peak_Capacity","Perc_TX_Peak_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Average_Capacity","TX_Average_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_TX_Average_Capacity","Perc_TX_Average_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Capacity_Hits","TX_Capacity_Hits",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Peak_Capacity","RX_Peak_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_RX_Peak_Capacity","Perc_RX_Peak_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Average_Capacity","RX_Average_Capacity",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Capacity_Hits","RX_Capacity_Hits",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","MissingPackets","MissingPackets",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","JtrBfrUnderruns","JtrBfrUnderruns",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","ES","CESOP_ES",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","SES","CESOP_SES",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","UAS","CESOP_UAS",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","FC","CESOP_FC",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","UAS","UAS",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","SES","SES",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","ES","ES",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","BBE","BBE",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","OI","OI",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts_green","ing_pckts_green",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts_yellow","ing_pckts_yellow",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts","ing_pckts",dayoffset));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rps","PSACW1","PSACW1",dayoffset));	
	}
}

public class DBPM {
	private String servers;
	private String maps;
	static Logger log = LogManager.getLogger("Main");

	public static void main(String[] args) {
		try {
			log.info("Network audit.");
			new DBPM(args).start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public DBPM(String[] args) {
		//servers = "uknetvah";
		servers = "uknetvab,uknetvac,uknetvad,uknetvae,uknetvag,uknetvah,uknetvai,uknetvaj";
		maps="NorthServer1,NorthServer2,NorthServer3,NorthServer4,SouthServer1,SouthServer2,SouthServer3,SouthServer4";
		//servers="uknetvac";
		//maps="NorthServer2";
		if (args.length == 2) {
			servers = args[0];
		}
		log.info("PM collection running for servers: " + servers);
	} 


	/**
	 * Starts this application.
	 *
	 * @param args is the command-line arguments
	 */
	public  void start() throws Exception {
		log.info("started at: " + new Timestamp(System.currentTimeMillis()));
		String[] s = servers.split(",");
		String[] m = maps.split(",");
		String daysoffset;
		PrintWriter PM = new PrintWriter("PM.csv", "UTF-8");
		PM.println("Name,Port,Date,KPI,Hour00Min00,Hour00Min15,Hour00Min30,Hour00Min45,Hour01Min00,Hour01Min15,Hour01Min30,Hour01Min45,Hour02Min00,Hour02Min15,Hour02Min30,Hour02Min45,Hour03Min00,Hour03Min15,Hour03Min30,Hour03Min45,Hour04Min00,Hour04Min15,Hour04Min30,Hour04Min45,Hour05Min00,Hour05Min15,Hour05Min30,Hour05Min45,Hour06Min00,Hour06Min15,Hour06Min30,Hour06Min45,Hour07Min00,Hour07Min15,Hour07Min30,Hour07Min45,Hour08Min00,Hour08Min15,Hour08Min30,Hour08Min45,Hour09Min00,Hour09Min15,Hour09Min30,Hour09Min45,Hour10Min00,Hour10Min15,Hour10Min30,Hour10Min45,Hour11Min00,Hour11Min15,Hour11Min30,Hour11Min45,Hour12Min00,Hour12Min15,Hour12Min30,Hour12Min45,Hour13Min00,Hour13Min15,Hour13Min30,Hour13Min45,Hour14Min00,Hour14Min15,Hour14Min30,Hour14Min45,Hour15Min00,Hour15Min15,Hour15Min30,Hour15Min45,Hour16Min00,Hour16Min15,Hour16Min30,Hour16Min45,Hour17Min00,Hour17Min15,Hour17Min30,Hour17Min45,Hour18Min00,Hour18Min15,Hour18Min30,Hour18Min45,Hour19Min00,Hour19Min15,Hour19Min30,Hour19Min45,Hour20Min00,Hour20Min15,Hour20Min30,Hour20Min45,Hour21Min00,Hour21Min15,Hour21Min30,Hour21Min45,Hour22Min00,Hour22Min15,Hour22Min30,Hour22Min45,Hour23Min00,Hour23Min15,Hour23Min30,Hour23Min45");
		for (int d = 1; d < 30; d++) {

			ConcurrentHashMap<String,String> all = new ConcurrentHashMap<String,String>();
			//all.put("Title","Name,Port,Date,KPI,Hour00Min00,Hour00Min15,Hour00Min30,Hour00Min45,Hour01Min00,Hour01Min15,Hour01Min30,Hour01Min45,Hour02Min00,Hour02Min15,Hour02Min30,Hour02Min45,Hour03Min00,Hour03Min15,Hour03Min30,Hour03Min45,Hour04Min00,Hour04Min15,Hour04Min30,Hour04Min45,Hour05Min00,Hour05Min15,Hour05Min30,Hour05Min45,Hour06Min00,Hour06Min15,Hour06Min30,Hour06Min45,Hour07Min00,Hour07Min15,Hour07Min30,Hour07Min45,Hour08Min00,Hour08Min15,Hour08Min30,Hour08Min45,Hour09Min00,Hour09Min15,Hour09Min30,Hour09Min45,Hour10Min00,Hour10Min15,Hour10Min30,Hour10Min45,Hour11Min00,Hour11Min15,Hour11Min30,Hour11Min45,Hour12Min00,Hour12Min15,Hour12Min30,Hour12Min45,Hour13Min00,Hour13Min15,Hour13Min30,Hour13Min45,Hour14Min00,Hour14Min15,Hour14Min30,Hour14Min45,Hour15Min00,Hour15Min15,Hour15Min30,Hour15Min45,Hour16Min00,Hour16Min15,Hour16Min30,Hour16Min45,Hour17Min00,Hour17Min15,Hour17Min30,Hour17Min45,Hour18Min00,Hour18Min15,Hour18Min30,Hour18Min45,Hour19Min00,Hour19Min15,Hour19Min30,Hour19Min45,Hour20Min00,Hour20Min15,Hour20Min30,Hour20Min45,Hour21Min00,Hour21Min15,Hour21Min30,Hour21Min45,Hour22Min00,Hour22Min15,Hour22Min30,Hour22Min45,Hour23Min00,Hour23Min15,Hour23Min30,Hour23Min45");
			ExecutorService executor = Executors.newFixedThreadPool(s.length);
			daysoffset = "-"+String.valueOf(d);
			for (int i = 0; i < s.length; i++) {
				Runnable sc = new ServerConnector(s[i],m[i],daysoffset,all);
				Thread worker = new Thread(sc);
				worker.setName(s[i]);
				executor.execute(sc);	
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				Thread.sleep(10);
			}
			Iterator it = all.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
				PM.println(pairs.getValue());
				it.remove(); 
			}
		}
		PM.close();
		log.info("end: " + new Date());
		log.info("Finished all threads");
	}
}

