package com.nsn.audit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsn.audit.dataset.NE;
import com.nsn.audit.dataset.Param;

public class SQLConnection {
	private String userName = "sa";
	private String password = "Welc0me2NSN";
	private String database = "uknetvra";
	private String url = "jdbc:jtds:sqlserver://"+database+":1433/Collector;instance=MSSQLSERVER";
	private Connection con;
	private PreparedStatement pstNE;
	private PreparedStatement pstParam;
	static Logger log = LogManager.getLogger("SQLConnection");

	public SQLConnection()
	{
		try
		{
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			this.con = DriverManager.getConnection(url, userName, password);
			String sqlNE = "INSERT INTO NE (ID, SUB_ID, T1, name, IP, type, svr, location, sysname) SELECT "+
					"?,?,?,?,?,?,?,?,? "+
					" WHERE NOT EXISTS (SELECT 1 FROM NE WHERE name = ? AND IP = ? AND type = ? AND svr = ? AND location =? AND sysname = ?)";
			pstNE = con.prepareStatement(sqlNE);
			String sqlParam="INSERT INTO Params(NE_ID, NE_SUB_ID, T1, Name, Value) SELECT ?,?,?,?,?";
			pstParam = con.prepareStatement(sqlParam);
		}
		catch (Exception err) {
			log.error("SQLConnectio error: " + err);
		}
	}

	protected void insertNE(NE ne) {
		//String = new ArrayList<NE>();
		try {
			pstNE.setInt(1, ne.getID());
			pstNE.setInt(2, ne.getSUB_ID());
			pstNE.setTimestamp(3, ne.getTime());
			pstNE.setString(4, ne.getName());
			pstNE.setString(5, ne.getIP());
			pstNE.setString(6, ne.getType());
			pstNE.setString(7, ne.getSvr());
			pstNE.setString(8, ne.getLocation());
			pstNE.setString(9, ne.getSysName());
			
			pstNE.setString(10, ne.getName());
			pstNE.setString(11, ne.getIP());
			pstNE.setString(12, ne.getType());
			pstNE.setString(13, ne.getSvr());
			pstNE.setString(14, ne.getLocation());
			pstNE.setString(15, ne.getSysName());
			pstNE.execute();
			for (Map.Entry<String, Param> entry : ne.getParams().entrySet()) {
			    pstParam.setInt(1, ne.getID());
			    pstParam.setInt(2, ne.getSUB_ID());
			    pstParam.setTimestamp(3, entry.getValue().getTimeStamp());
			    pstParam.setString(4, entry.getKey());
			    pstParam.setString(5, entry.getValue().toString());
			    pstParam.execute();
			} 

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void insertParam(){

	}

	protected void closeCon() {
		try {
			this.con.close();
		} catch (SQLException e) {
			log.error("connection to SQL not closed");
			e.printStackTrace();
		}
	}
}
