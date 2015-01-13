package com.nsn.audit.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class MapReaderTest {
	public static void main(String[] args) throws Exception {
		Connection conn = getConnection();
		Statement st = conn.createStatement();
		st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT ne.NE_NAME, ne.ADDRESS as IP_ADDRESS,txt.TEXT_INFO, ne.NE_STATUS_ENABLED FROM NETWORK_ELEMENT AS ne"+ 
				" LEFT JOIN TEXT_INFO AS txt ON (ne.MAP_OWNER_ID=txt.MAP_OWNER_ID) AND ((abs(ne.POSITION_X-txt.POS_X)<90) And (abs(ne.POSITION_Y-txt.POS_Y)<20))"+
				" WHERE txt.TEXT_INFO like '%VFE%'"+
				" ORDER BY ne.NE_NAME");
		if (rs != null) {
			while (rs.next()) {
				System.out.println(rs.getRow()+"-->"+rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3));
				
			}

		}
		st.close();
		conn.close();
	}

	private static Connection getConnection() throws Exception {
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection conn=DriverManager.getConnection("jdbc:ucanaccess:////uknetvab/d$/Program Files (x86)/Nokia Siemens Networks/NetViewer/Map/north_slave1.mdb");
		//Connection conn=DriverManager.getConnection("jdbc:odbc:uknetvab");
		return conn;
	}
}