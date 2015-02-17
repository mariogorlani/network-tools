package com.nsn.audit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
			HashMap<String,String[]> mapsOwner = new HashMap<String,String[]>();
			Connection conn=DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			st = conn.createStatement();
			String sql = "SELECT ne.NE_NAME as name,  ne.ADDRESS as IP, MAP_OWNER_ID as map, ne.NE_STATUS_ENABLED as ConnStatus FROM NETWORK_ELEMENT AS ne";
			ResultSet rsNE = st.executeQuery(sql);
			sql = "select ID_POS as id, MAP_NAME, MAP_OWNER_ID as owner from MAP";
			ResultSet rsMAP = st.executeQuery(sql);
			if (rsMAP!=null){
				while (rsMAP.next()){
					mapsOwner.put(rsMAP.getString(1),new String[]{rsMAP.getString(2),rsMAP.getString(3)});
				}
			}
			if (rsNE != null) {
				while (rsNE.next()) {
					count++;
					ne = new NE(rsNE.getString("name"),rsNE.getString("IP"));
					ne.setConnStatus(rsNE.getBoolean("ConnStatus")?GenericDefinitions.Online:GenericDefinitions.Disabled);
					neList.put(rsNE.getString("name"),ne);
					
					if (!rsNE.getBoolean("ConnStatus")) disabledNes++;
					String id = mapsOwner.get((rsNE.getString("map")))[1];
					String location = mapsOwner.get((rsNE.getString("map")))[0];
					while (!id.equals("1")){
						location = mapsOwner.get(id)[0];
						id = mapsOwner.get(id)[1];
					}
					ne.setLocation(server+"/"+location);
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

			String sql = "select t.text_info as vfe, odu1, odu2 " + 
					"from text_info as t " + 
					"inner join ( " + 
					"select nt1.ne_name as odu1, nt2.ne_name as odu2, " + 
					"int((nt1.position_x + nt2.position_x) / 2) as avgx, " + 
					"int((nt1.position_y + nt2.position_y)/2) as avgy,nt1.map_owner_id as map " + 
					"from ((links as l " + 
					"inner join network_element as nt1 on l.ne_1 = nt1.id_pos) " + 
					"inner join network_element as nt2 on l.ne_2 = nt2.id_pos) " + 
					") as sb " + 
					"on (abs(t.POS_X-sb.avgx)<60) and (abs(t.POS_Y-sb.avgy)<40) and t.MAP_OWNER_ID=sb.map " + 
					"where t.text_info like 'vfe%'";
			ResultSet rsVFE = st.executeQuery(sql);
			if (rsVFE != null) {
				count=0;
				while (rsVFE.next()) {
					count++;
					ne = neList.get(rsVFE.getString("odu1"));
					if (ne!=null){
						ne.setVFE(rsVFE.getString("vfe")); 
						neList.put(rsVFE.getString("odu1"),ne);
					}
					else
						log.debug(rsVFE.getString("vfe")+" doesn't match any NE for "+ rsVFE.getString("odu1"));

					ne = neList.get(rsVFE.getString("odu2"));
					if (ne!=null){
						ne.setVFE(rsVFE.getString("vfe")); 
						neList.put(rsVFE.getString("odu2"),ne);
					}
					else
						log.debug(rsVFE.getString("vfe")+" doesn't match any NE for "+ rsVFE.getString("odu2"));
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

	public ArrayList<String> extractMapVFEs() {
		ArrayList<String> results = new ArrayList<String>();
		try {
			Connection conn=DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			stat = conn.createStatement();
			String sql = "SELECT TEXT_INFO, MAP_NAME from TEXT_INFO LEFT JOIN MAP ON (TEXT_INFO.MAP_OWNER_ID=MAP.ID_POS) where text_info like 'VFE%'"; 
			ResultSet rs = stat.executeQuery(sql);
			if (rs != null) 
				while (rs.next())
					results.add(this.server+","+rs.getString("TEXT_INFO")+","+rs.getString("MAP_NAME"));
			stat.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
}	