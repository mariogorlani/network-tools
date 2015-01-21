package com.nsn.audit.utils;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;



import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;

public class NVDBReader {
	//HashMap<String,ArrayList<NE>> ringsList;
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
	 * Get performance data
	 */
	public ArrayList<String> getAllPerformanceData(String table, String counter){

		Connection conn;
		ArrayList<String> results = new ArrayList<String>();
		try {
			conn = DriverManager.getConnection(url, userName, password);
			Statement stat = conn.createStatement();
			String sql =  "( SELECT name AS Name, tp_name AS Port, cast(time_start AS date) AS Date , '"+counter+"' AS KPI "+ 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(0*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(0*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour00Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(0*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(0*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour00Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(0*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(0*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour00Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(0*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(0*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour00Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(1*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(1*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour01Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(1*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(1*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour01Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(1*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(1*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour01Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(1*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(1*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour01Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(2*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(2*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour02Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(2*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(2*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour02Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(2*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(2*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour02Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(2*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(2*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour02Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(3*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(3*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour03Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(3*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(3*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour03Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(3*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(3*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour03Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(3*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(3*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour03Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(4*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(4*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour04Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(4*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(4*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour04Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(4*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(4*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour04Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(4*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(4*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour04Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(5*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(5*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour05Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(5*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(5*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour05Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(5*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(5*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour05Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(5*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(5*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour05Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(6*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(6*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour06Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(6*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(6*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour06Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(6*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(6*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour06Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(6*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(6*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour06Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(7*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(7*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour07Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(7*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(7*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour07Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(7*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(7*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour07Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(7*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(7*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour07Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(8*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(8*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour08Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(8*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(8*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour08Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(8*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(8*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour08Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(8*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(8*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour08Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(9*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(9*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour09Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(9*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(9*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour09Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(9*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(9*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour09Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(9*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(9*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour09Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(10*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(10*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour10Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(10*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(10*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour10Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(10*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(10*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour10Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(10*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(10*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour10Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(11*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(11*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour11Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(11*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(11*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour11Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(11*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(11*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour11Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(11*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(11*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour11Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(12*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(12*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour12Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(12*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(12*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour12Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(12*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(12*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour12Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(12*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(12*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour12Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(13*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(13*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour13Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(13*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(13*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour13Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(13*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(13*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour13Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(13*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(13*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour13Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(14*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(14*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour14Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(14*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(14*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour14Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(14*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(14*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour14Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(14*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(14*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour14Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(15*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(15*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour15Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(15*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(15*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour15Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(15*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(15*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour15Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(15*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(15*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour15Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(16*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(16*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour16Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(16*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(16*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour16Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(16*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(16*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour16Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(16*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(16*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour16Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(17*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(17*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour17Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(17*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(17*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour17Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(17*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(17*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour17Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(17*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(17*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour17Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(18*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(18*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour18Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(18*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(18*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour18Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(18*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(18*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour18Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(18*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(18*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour18Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(19*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(19*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour19Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(19*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(19*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour19Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(19*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(19*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour19Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(19*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(19*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour19Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(20*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(20*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour20Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(20*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(20*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour20Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(20*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(20*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour20Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(20*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(20*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour20Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(21*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(21*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour21Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(21*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(21*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour21Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(21*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(21*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour21Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(21*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(21*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour21Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(22*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(22*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour22Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(22*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(22*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour22Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(22*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(22*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour22Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(22*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(22*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour22Min45 " + 
					"  , SUM(cast(CASE WHEN ((time_start<=dateadd(n,(23*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(23*60)+(15*0),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour23Min00, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(23*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(23*60)+(15*1),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour23Min15, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(23*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(23*60)+(15*2),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour23Min30, SUM(cast(CASE WHEN ((time_start<=dateadd(n,(23*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime))) and (time_end>=dateadd(n,(23*60)+(15*3),cast(DATEADD(day, -1, cast(GETDATE() AS date))AS datetime)))) THEN KPI END AS BigInt)) AS Hour23Min45 " + 
					"FROM " + 
					"( " + 
					"SELECT " + 
					table+".date_time AS time_start, " + 
					table+".date_time_end AS time_end, " + 
					table+".tp_name, " + 
					"	ne_working_mode_g.name, " + 
					"	sum(cast("+table+"."+counter+" as BigInt)) AS KPI " + 
					"FROM NetViewer.dbo."+table+" " + 
					"LEFT JOIN NetViewer.dbo.ne_working_mode_g " + 
					"	 ON ((NetViewer.dbo."+table+".ne_sub_id = NetViewer.dbo.ne_working_mode_g.sub_id) " + 
					"		AND (NetViewer.dbo."+table+".ne_working_mode_id = NetViewer.dbo.ne_working_mode_g.id)) " + 
					"WHERE (cast("+table+".date_time AS date) = cast(DATEADD(day, -1, GETDATE()) AS date)) " + 
					"GROUP BY "+table+".date_time, " + 
					"		 "+table+".date_time_end ,"+table+".tp_name, " + 
					"		 ne_working_mode_g.name " + 
					") AS foo1 " + 
					"GROUP BY cast([time_start] AS date), name, tp_name)";
			ResultSet rs = stat.executeQuery(sql);
			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columns = metaData.getColumnCount();
				while (rs.next()) {
					String record = new String();
					for (int i = 1; i <= columns; i++) {
						if (rs.getString(i)!=null) record = record + rs.getString(i);
						record= record+",";
					}
					results.add(record);
				}
			}
			stat.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;

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