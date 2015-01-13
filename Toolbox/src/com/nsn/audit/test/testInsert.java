package com.nsn.audit.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nsn.audit.dataset.NE;

public class testInsert {


	public static void main(String[] args) {
		String userName = "sa";
		String password = "Welc0me2NSN";
		String database = "uknetvra";
		String url = "jdbc:jtds:sqlserver://"+database+":1433/Collector;instance=MSSQLSERVER";
		Connection con;
		PreparedStatement pstNE;
		PreparedStatement pstMaxID;
		try
		{
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			con = DriverManager.getConnection(url, userName, password);
			String sqlID = "SELECT MAX(ID) ID from NE";
			pstMaxID = con.prepareStatement(sqlID);

			String sqlNE = "insert into NE (ID, name, IP, type, svr, location, sysname) SELECT "+
					//"'"+ne.getName()+"','"+ne.getIP()+"','"+ne.getType()+"','"+ne.getSvr()+"','"+ne.getLocation()+"','"+ne.getSysName()+"'"+
					"?,?,?,?,?,?,? "+
					" WHERE NOT EXISTS (SELECT 1 FROM NE WHERE name = ? AND IP = ? AND type = ? AND svr = ? AND location =? AND sysname = ?)";
			pstNE = con.prepareStatement(sqlNE);
			String sqlParam="insert into ";
			int ID=0;
			for (int i = 0; i < 10; i++) {

				ResultSet rsID = pstMaxID.executeQuery();
				if (rsID!=null)
					while (rsID.next()) {
						ID=Integer.valueOf(rsID.getString("ID")).intValue();
					}
				ID++;
				pstNE.setString(1, String.valueOf(ID));
				pstNE.setString(2, "TEST_NE_NAME_"+String.valueOf(ID));
				pstNE.setString(3, "TEST_NE_NAME");
				pstNE.setString(4, "TEST_NE_NAME");
				pstNE.setString(5, "TEST_NE_NAME");
				pstNE.setString(6, "TEST_NE_NAME");
				pstNE.setString(7, "TEST_NE_NAME");
				pstNE.setString(8, "TEST_NE_NAME_"+String.valueOf(ID));
				pstNE.setString(9, "TEST_NE_NAME");
				pstNE.setString(10,"TEST_NE_NAME");
				pstNE.setString(11, "TEST_NE_NAME");
				pstNE.setString(12, "TEST_NE_NAME");
				pstNE.setString(13, "TEST_NE_NAME");
				boolean status = pstNE.execute();
			}


		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
}
