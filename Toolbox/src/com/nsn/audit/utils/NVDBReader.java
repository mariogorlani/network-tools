package com.nsn.audit.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;

public class NVDBReader {
	HashMap<String,ArrayList<NE>> ringsList;
	String userName = "sa";
	String password = "Welc0me2NSN";
	String url;
	String server, mapName;
	static Logger log = LogManager.getLogger("NVDBReader");

	public NVDBReader(String s, String m) {
		try {
			server = s;
			mapName = m;
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			url = "jdbc:jtds:sqlserver://"+server+":1433/NetViewer;instance=MSSQLSERVER";
		} catch (Exception err) {
			log.debug("ERROR: " + err);
		}
	}

	public HashMap<String, ArrayList<NE>> getRingsList() {
		return ringsList;
	}

	public HashMap<String, NE> extractType(HashMap<String, NE> neList) {
		try {
			NE ne;
			int count=0;
			Connection conn = DriverManager.getConnection(url, userName, password);
			Statement stat = conn.createStatement();
			String sql = "SELECT distinct g.id, g.sub_id, g.name,g.path,g.address,t.name as type "+
					"FROM netviewer.dbo.ne_working_mode_g as g INNER JOIN netviewer.dbo.equipment_types as t ON g.equipment_type = t.ID "+
					"WHERE g.path like '\\"+mapName+"%'";
			ResultSet rs = stat.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					count++;
					ne = neList.get(rs.getString("name"));
					if (ne!=null){
						ne.setType(rs.getString("type"));
						ne.setLocation(rs.getString("path").split("\\\\")[2]);
						neList.put(rs.getString("name"),ne);
					}
					else
						log.info("extractType null for "+rs.getString("name") + " count "+ count);
				}
			}
			stat.close();
			conn.close();
		} catch (Exception err) {
			log.debug("ERRORs: " + err);
		}
		return neList;
	}
	
	public HashMap<String, NE> extractDisconnections(HashMap<String, NE> neList) {
		try {
			NE ne;
			int count=0;
			Connection conn = DriverManager.getConnection(url, userName, password);
			Statement stat = conn.createStatement();
			String sql =  "select nwm.name as name, COUNT(*) as Disconnections from events as evt" + 
					" LEFT JOIN ne_working_mode_g as nwm ON evt.ne_working_mode_id=nwm.id"+
					" LEFT JOIN equipment_types et on nwm.equipment_type=et.ID"+
					" where description ='NE Disconnected' and (cast(evt.date_time_log AS date) = cast(DATEADD(day, -1, GETDATE()) AS date))"+
					" group by nwm.name, et.name, cast(evt.date_time_log AS date) order by Disconnections desc;";
			ResultSet rs = stat.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					count++;
					log.debug(rs.getString("Disconnections")+" disconnections for NE "+rs.getString("name"));
					ne = neList.get(rs.getString("name"));
					if (ne!=null){
						ne.setDisconnections(rs.getInt("Disconnections"));

						neList.put(rs.getString("name"),ne);
					}
					else
						log.info("extractDisconnections found "+rs.getString("name") + " count "+ count+ " no more existing!");
				}
			}
			stat.close();
			conn.close();
		} catch (Exception err) {
			log.debug("ERRORs: " + err);
		}
		return neList;
	}

	/*
	 GetMapName
	 String sql = "SELECT [key],[value] FROM [NetViewer].[dbo].[generic_info] where [key] like 'MapName'";
	ResultSet rs = stat.executeQuery(sql);
	String mapName = "";
	if (rs != null) {
		while (rs.next()) {
			mapName=rs.getString("value");
		}
	}
	 */
}