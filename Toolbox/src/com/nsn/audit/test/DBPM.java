package com.nsn.audit.test;

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
	String server, map, dayoffset, nes;
	ConcurrentHashMap<String, String> data;
	static Logger log = LogManager.getLogger("Main");

	public ServerConnector(String s, String m,  String dayoffset, String nes, ConcurrentHashMap<String, String> d ){
		this.server = s;
		this.map = m;
		this.data = d;
		this.dayoffset = dayoffset;
		this.nes = nes;
	}
	public void run() {
		NVDBReader nvdb = new NVDBReader(server,map);
		log.info("server: "+ server);
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTM_min","RLTM_min",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTM_max","RLTM_max",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_1","RLTS_1",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_1_th","RLTS_1_th",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_2","RLTS_2",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_2_th","RLTS_2_th",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_3","RLTS_3",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_3_th","RLTS_3_th",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_4","RLTS_4",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","RLTS_4_th","RLTS_4_th",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","TLTS_1","TLTS_1",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rspi","TLTS_2","TLTS_2",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_1","RX_profile_1",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_2","RX_profile_2",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_3","RX_profile_3",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_4","RX_profile_4",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","RX_profile_5","RX_profile_5",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_6","Rx_profile_6",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_7","Rx_profile_7",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Rx_profile_10","AirLoss",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_1","Tx_profile_1",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_2","Tx_profile_2",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_3","Tx_profile_3",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_4","Tx_profile_4",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_5","Tx_profile_5",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_6","Tx_profile_6",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_acm","Tx_profile_7","Tx_profile_7",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InOctets","InOctets",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InDroppedPkts","InDroppedPkts",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","InGoodPkts","InGoodPkts",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutOctets","OutOctets",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutDroppedPkts","OutDroppedPkts",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_ieee802","OutGoodPkts","OutGoodPkts",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Peak_Capacity","TX_Peak_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_TX_Peak_Capacity","Perc_TX_Peak_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Average_Capacity","TX_Average_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_TX_Average_Capacity","Perc_TX_Average_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","TX_Capacity_Hits","TX_Capacity_Hits",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Peak_Capacity","RX_Peak_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","Perc_RX_Peak_Capacity","Perc_RX_Peak_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Average_Capacity","RX_Average_Capacity",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_throughput","RX_Capacity_Hits","RX_Capacity_Hits",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","MissingPackets","MissingPackets",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","JtrBfrUnderruns","JtrBfrUnderruns",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","ES","CESOP_ES",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","SES","CESOP_SES",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","UAS","CESOP_UAS",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_cesop","FC","CESOP_FC",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","UAS","UAS",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","SES","SES",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","ES","ES",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","BBE","BBE",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_g826","OI","OI",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts_green","ing_pckts_green",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts_yellow","ing_pckts_yellow",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_services","ing_pckts","ing_pckts",dayoffset,nes));
		data.putAll(nvdb.getAllPerformanceData("pm_15m_rps","PSACW1","PSACW1",dayoffset,nes));	
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
		if (args.length == 2) {
			servers = args[0];
		}
		log.info("Network Audit running on the servers: " + servers);
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
		String nes = "'4924_ZA_11','4924_ZA_12','0434_ZA_12','0434_ZA_11','10104_ZC_12','37177_ZC_13','0047_ZEA_11','32552_ZC_12','32956_ZC_13','7844_ZC_11','4924_ZA_21','31514_ZC_11','4924_ZA_22','11144_ZDA_11','0370_ZC_11','0370_ZC_14','0370_ZC_13','0370_ZC_12','7844_ZEA_11','11144_ZC_12','11144_ZC_11','4924_ZC_120','4924_ZC_122','4924_ZC_121','31583_ZC_11','31583_ZC_13','31583_ZC_12','31583_ZC_14','4924_ZC_117','4924_ZC_116','4924_ZC_119','4924_ZC_118','4924_ZC_111','4924_ZC_110','4924_ZC_113','4924_ZC_112','4924_ZC_115','4924_ZC_114','4924_ZD_125','0370_ZDA_11','0370_ZDA_12','31583_ZDA_11','4924_ZDA_13','4924_ZDA_14','4924_ZDA_11','4924_ZDA_12','3125_ZDA_11','3287_ZC_11','33186_ZC_11','11158_ZC_11','11158_ZC_13','11158_ZC_12','11158_ZC_14','33186_ZDA_11','3287_ZDA_11','11158_ZDA_11','4924_ZC_12','4924_ZC_11','4924_ZC_19','4924_ZC_18','4924_ZC_17','4924_ZC_16','4924_ZC_15','4924_ZC_14','4924_ZC_13','3127_ZC_13','3127_ZC_11','3127_ZC_12','3127_ZDA_11','11098_ZC_11','11098_ZC_12','3125_ZC_11','31514_ZEA_11','11098_ZDA_11','0352_ZA_21','0352_ZA_22','0352_ZC_13','0352_ZC_12','0352_ZC_11','0034_ZFA_11','0352_ZDA_11','0034_ZC_11','0034_ZC_12','3140_ZC_13','3140_ZC_12','3140_ZC_11','3140_ZC_14','10660_ZA_12','10660_ZA_11','3140_ZA_11','3140_ZDA_11','10172_ZEA_11','4298_ZDA_11','0171_ZC_11','0171_ZC_12','10104_ZEA_11','35262_ZC_11','0171_ZA_11','0171_ZA_12','10649_ZEA_11','10104_ZC_11','10104_ZC_13','37177_ZC_11','37177_ZC_12','37177_ZC_14','5356_ZBP_13','0047_ZDA_11','4884_ZEA_11','32956_ZEA_11','32956_ZD_11','32956_ZD_12','0047_ZA_12','0047_ZA_11','31050_ZC_11','31050_ZC_12','31050_ZC_13','35262_ZFA_11','0171_ZEA_11','31050_ZEA_11','32552_ZEA_11','4885_ZC_11','10172_ZC_11','4298_ZC_11','0047_ZC_15','0047_ZC_16','0047_ZC_13','0047_ZC_14','0047_ZC_11','0047_ZC_12','37177_ZDA_11','0171_ZD_13','0171_ZD_14','32552_ZC_11','32552_ZC_13','5356_ZC_12','5356_ZC_11','1635_ZA_12','1635_ZA_11','5356_ZDA_11','5356_ZDA_12','32956_ZC_14','10660_ZEA_11','4884_ZC_11','4885_ZFA_11','10649_ZC_11','10660_ZC_12','10660_ZC_11','11160_ZEA_11','4952_ZEA_11','3128_ZFA_11','0033_ZDA_11','0033_ZDA_12','0033_ZDA_13','0434_ZC_11','0434_ZC_12','0434_ZC_13','3128_ZD_11','0435_ZDA_12','0435_ZDA_11','0435_ZC_14','0435_ZC_15','0435_ZC_12','0435_ZC_13','0435_ZC_11','4125_ZEA_11','0434_ZEA_11','6090_ZFA_11','10164_ZDA_11','72696_ZC_11','0033_ZC_11','0033_ZC_16','0033_ZC_12','0033_ZC_13','0033_ZC_14','10158_ZDA_11','11160_ZC_11','72696_ZDA_11','0435_ZD_18','0435_ZD_19','6090_ZC_12','6090_ZC_11','6090_ZC_13','10158_ZC_11','4952_ZC_11','10164_ZC_11','0435_ZA_12','0435_ZA_11','4125_ZD_11','4939_ZA_11','4939_ZA_12','3198_ZDA_11','3198_ZC_11','4135_ZD_13','31880_ZD_11','4135_ZEA_11','31880_ZEA_11','34251_ZEA_11','1723_ZC_11','0010_ZA_11','0010_ZA_12','0010_ZC_16','0010_ZC_15','0010_ZC_14','0010_ZC_13','0010_ZC_12','0010_ZC_11','31342_ZDA_11','1723_ZDA_11','1723_ZDA_12','31342_ZC_13','31342_ZC_12','31342_ZC_11','0010_ZDA_11','3550_ZC_11','0553_ZD_13','3550_ZEA_11','0553_ZC_11','0553_ZC_12','0553_ZEA_11','4972_ZC_11','0986_ZEA_11','3137_ZEA_11','6095_ZC_11','6095_ZC_12','6095_ZC_13','32926_ZDA_11','32926_ZC_11','32926_ZC_12','32926_ZC_13','32926_ZC_14','0986_ZC_11','3137_ZC_11','4972_ZEA_11','6095_ZDA_12','6095_ZDA_11','3140_ZEA_12','3140_ZD_15'";
		String daysoffset;
		PrintWriter PM = new PrintWriter("PM.csv", "UTF-8");
		PM.println("Name,Port,Date,KPI,Hour00Min00,Hour00Min15,Hour00Min30,Hour00Min45,Hour01Min00,Hour01Min15,Hour01Min30,Hour01Min45,Hour02Min00,Hour02Min15,Hour02Min30,Hour02Min45,Hour03Min00,Hour03Min15,Hour03Min30,Hour03Min45,Hour04Min00,Hour04Min15,Hour04Min30,Hour04Min45,Hour05Min00,Hour05Min15,Hour05Min30,Hour05Min45,Hour06Min00,Hour06Min15,Hour06Min30,Hour06Min45,Hour07Min00,Hour07Min15,Hour07Min30,Hour07Min45,Hour08Min00,Hour08Min15,Hour08Min30,Hour08Min45,Hour09Min00,Hour09Min15,Hour09Min30,Hour09Min45,Hour10Min00,Hour10Min15,Hour10Min30,Hour10Min45,Hour11Min00,Hour11Min15,Hour11Min30,Hour11Min45,Hour12Min00,Hour12Min15,Hour12Min30,Hour12Min45,Hour13Min00,Hour13Min15,Hour13Min30,Hour13Min45,Hour14Min00,Hour14Min15,Hour14Min30,Hour14Min45,Hour15Min00,Hour15Min15,Hour15Min30,Hour15Min45,Hour16Min00,Hour16Min15,Hour16Min30,Hour16Min45,Hour17Min00,Hour17Min15,Hour17Min30,Hour17Min45,Hour18Min00,Hour18Min15,Hour18Min30,Hour18Min45,Hour19Min00,Hour19Min15,Hour19Min30,Hour19Min45,Hour20Min00,Hour20Min15,Hour20Min30,Hour20Min45,Hour21Min00,Hour21Min15,Hour21Min30,Hour21Min45,Hour22Min00,Hour22Min15,Hour22Min30,Hour22Min45,Hour23Min00,Hour23Min15,Hour23Min30,Hour23Min45");
		for (int d = 1; d < 30; d++) {

			ConcurrentHashMap<String,String> all = new ConcurrentHashMap<String,String>();
			//all.put("Title","Name,Port,Date,KPI,Hour00Min00,Hour00Min15,Hour00Min30,Hour00Min45,Hour01Min00,Hour01Min15,Hour01Min30,Hour01Min45,Hour02Min00,Hour02Min15,Hour02Min30,Hour02Min45,Hour03Min00,Hour03Min15,Hour03Min30,Hour03Min45,Hour04Min00,Hour04Min15,Hour04Min30,Hour04Min45,Hour05Min00,Hour05Min15,Hour05Min30,Hour05Min45,Hour06Min00,Hour06Min15,Hour06Min30,Hour06Min45,Hour07Min00,Hour07Min15,Hour07Min30,Hour07Min45,Hour08Min00,Hour08Min15,Hour08Min30,Hour08Min45,Hour09Min00,Hour09Min15,Hour09Min30,Hour09Min45,Hour10Min00,Hour10Min15,Hour10Min30,Hour10Min45,Hour11Min00,Hour11Min15,Hour11Min30,Hour11Min45,Hour12Min00,Hour12Min15,Hour12Min30,Hour12Min45,Hour13Min00,Hour13Min15,Hour13Min30,Hour13Min45,Hour14Min00,Hour14Min15,Hour14Min30,Hour14Min45,Hour15Min00,Hour15Min15,Hour15Min30,Hour15Min45,Hour16Min00,Hour16Min15,Hour16Min30,Hour16Min45,Hour17Min00,Hour17Min15,Hour17Min30,Hour17Min45,Hour18Min00,Hour18Min15,Hour18Min30,Hour18Min45,Hour19Min00,Hour19Min15,Hour19Min30,Hour19Min45,Hour20Min00,Hour20Min15,Hour20Min30,Hour20Min45,Hour21Min00,Hour21Min15,Hour21Min30,Hour21Min45,Hour22Min00,Hour22Min15,Hour22Min30,Hour22Min45,Hour23Min00,Hour23Min15,Hour23Min30,Hour23Min45");
			ExecutorService executor = Executors.newFixedThreadPool(s.length);
			daysoffset = "-"+String.valueOf(d);
			for (int i = 0; i < s.length; i++) {
				Runnable sc = new ServerConnector(s[i],m[i],daysoffset,nes,all);
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

