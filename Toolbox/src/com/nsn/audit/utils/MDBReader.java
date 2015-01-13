package com.nsn.audit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;

public class MDBReader {
	static Logger log = LogManager.getLogger();
	String server, mapName, url;

	public MDBReader(String s,String m){
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			server = s;
			mapName = m;
			url = "jdbc:ucanaccess:////"+server+"/d$/Program Files (x86)/Nokia Siemens Networks/NetViewer/Map/"+mapName+".mdb";
		}
		catch(Exception err) {
			log.debug("ERROR: " + err);
		}
	}
	/**
	 * Query server MDB map database to return Name, IP, VFENumber, ConnStatus
	 * @return
	 */
	public HashMap<String,NE> extractNEs() {
		HashMap<String, NE> neList = new HashMap<String, NE>();
		try {
			NE ne;
			int count = 0;
			int disabledNes = 0;
			Connection conn=DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT ne.NE_NAME as name,  ne.ADDRESS as IP, ne.NE_STATUS_ENABLED as ConnStatus FROM NETWORK_ELEMENT AS ne");
			if (rs != null) {
				while (rs.next()) {
					count++;
					ne = new NE(rs.getString("name"),rs.getString("IP"));
					ne.setConnStatus(rs.getBoolean("ConnStatus")?GenericDefinitions.Online:GenericDefinitions.Disabled);
					neList.put(rs.getString("name"),ne);
					if (!rs.getBoolean("ConnStatus")) disabledNes++;
				}
			}
			log.info("extractNEs: "+ server+ " map file contains "+count+"; including " +disabledNes +" disabled NEs");
			st.close();
			conn.close();
		} catch (Exception err) {
			err.printStackTrace();
			log.debug("extractNEs: " + err.getMessage());
		}
		return neList;

	}

	/**
	 * Query server MDB map database to return Name, IP, VFENumber, ConnStatus
	 * @return
	 */
	public HashMap<String,NE> extractVFEs(HashMap<String, NE> neList) {

		try {
			NE ne;
			int count = 0;
			Connection conn=DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT ne.NE_NAME as name, txt.TEXT_INFO as vfe FROM NETWORK_ELEMENT AS ne"+ 
					" LEFT JOIN TEXT_INFO AS txt ON (ne.MAP_OWNER_ID=txt.MAP_OWNER_ID) AND ((abs(ne.POSITION_X-txt.POS_X)<90) And (abs(ne.POSITION_Y-txt.POS_Y)<20))"+
					" WHERE txt.TEXT_INFO like '%VFE%'");
			if (rs != null) {
				while (rs.next()) {
					count++;
					ne = neList.get(rs.getString("name"));
					if (ne!=null){
						ne.setVFE(rs.getString("vfe")); 
						neList.put(rs.getString("name"),ne);
					}
					else
						log.debug("null for "+rs.getString("name") + " count "+ count);
				}
				log.info("VFE for "+server+" are "+ count);
			}
			st.close();
			conn.close();
		} catch (Exception err) {
			err.printStackTrace();
			log.debug("extractVFEs ERRORs: " + err.getMessage());
		}
		return neList;
	}
}