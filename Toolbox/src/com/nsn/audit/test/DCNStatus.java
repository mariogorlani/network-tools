package com.nsn.audit.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Session;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.nsn.audit.utils.GenericDefinitions;

public class DCNStatus {
	private Session snmpTransport;
	private String [] servers = {
			"172.17.5.211",
			"172.17.5.212",
			"172.17.5.213",
			"172.17.5.214",
			"172.17.5.216",
			"172.17.5.217",
			"172.17.5.218",
			"172.17.5.219"
	};
	/*
	 * index, connstatus, enabled, Name, severity
	 * 
	 *  ConnStatus (INTEGER) {
	 *  disconnected(0),
	 *  connected(1),
	 *  onLine(2),
	 *  offLine(3)
	 *  }
	 *  
	 *  Enabled(Boolean) {
	 *  true,
	 *  false
	 *  }
	 *  
	 *   ItuAlarmPerceivedSeverity (INTEGER) {
	 *   cleared(1),
	 *   indeterminate(2),
	 *   critical(3),
	 *   major(4),
	 *   minor(5),
	 *   warning(6)
	 *   }
	 */

	private OID[] nvSvrNEMapTable = {
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.1"),
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.3"),
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.4"),
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.5"),
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.6")
	};

	static Logger log = LogManager.getLogger("DCNStatus");
	public String ipAddress="127.0.0.1";
	public String port="161";
	public int snmpVersion = SnmpConstants.version1;
	public int retries = 3;
	public OctetString readonlyCommunity = new OctetString("public");//no more in use, see devices.xml property
	public String readwriteCommunity = "sysmanager";
	public int timeout = 8000;


	public String[][] readSNMPTable(Target comtarget, OID[] columnOid) {
		TableUtils tUtils = new TableUtils(snmpTransport, new DefaultPDUFactory());

		List<TableEvent> events = tUtils.getTable(comtarget, columnOid, null, null);
		String[][] list = new String[events.size()][columnOid.length];
		int i = 0;
		for (TableEvent event : events) {
			if (event.isError()) {
				log.error("readSNMPTable failed for " + comtarget.getAddress() + "; error is "+ event.getErrorMessage());
			} else{
				for (int j = 0; j < event.getColumns().length; j++) {
					list[i][j] = event.getColumns()[j].getVariable().toString();
				} 
			}
			i++;
		}
		return list;
	}
	public static void main(String[] args) {
		DCNStatus dcn = new DCNStatus();
		dcn.start();
	}
	public DCNStatus () {
		DefaultUdpTransportMapping transport;
		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			snmpTransport = new Snmp(transport);
		} catch (IOException e) {
			log.error(e);
		}
	}

	public void start(){
		try {
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setVersion(snmpVersion);
			comtarget.setRetries(retries);
			comtarget.setTimeout(timeout);
			comtarget.setCommunity(readonlyCommunity);
			List<String[]> serversDCNStatus = new ArrayList<String[]>();
			Date date = new Date();
			SimpleDateFormat dateF = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
			for (int i = 0; i < servers.length; i++) {
				log.info(servers[i]+": " + new Timestamp(System.currentTimeMillis()));
				comtarget.setAddress(new UdpAddress(servers[i] + "/" + GenericDefinitions.port));
				serversDCNStatus.addAll(Arrays.asList(readSNMPTable(comtarget, nvSvrNEMapTable)));
			}
			int total = 0;
			int disconnected = 0;
			int connected = 0;
			int onLine = 0;
			int offLine = 0;
			int disabled = 0;
			int enabled = 0;
			int cleared = 0;
			int indeterminate = 0;
			int critical = 0;
			int major = 0;
			int minor = 0;
			int warning = 0;

			for (Iterator<String[]> it = serversDCNStatus.iterator(); it.hasNext();) {
				total++;
				String[] s = (String[]) it.next();
				log.info(Arrays.toString(s));
				if (s[1]!=null)
					switch (Integer.valueOf(s[1])) {
					case 0: disconnected++;
					break;
					case 1: connected++;
					break;
					case 2: onLine++;
					break;
					case 3: offLine++;
					break;
					}
				if (s[2]!=null)
					switch (Integer.valueOf(s[2])) {
					case 0: disabled++;
					break;
					case 1: enabled++;
					break;
					}
				if (s[4]!=null)
					switch (Integer.valueOf(s[4])) {
					case 1: cleared++;
					break;
					case 2: indeterminate++;
					break;
					case 3: critical++;
					break;
					case 4: major++;
					break;
					case 5: minor++;
					break;
					case 6: warning++;
					break;			
					}
			}
			log.info("total: " + total);
			log.info("disconnected: "+ disconnected +";");
			log.info("connecting: "+ connected +";");
			log.info("onLine: "+ onLine +";");
			log.info("wrong password: "+ offLine +";");
			log.info("------------------------------");
			log.info("disabled: "+ disabled +";");
			log.info("enabled: "+ enabled +";");
			log.info("------------------------------");
			log.info("cleared: "+ cleared +";");
			log.info("indeterminate: "+ indeterminate +";");
			log.info("critical: "+ critical +";");
			log.info("major: "+ major +";");
			log.info("minor: "+ minor +";");
			log.info("warning: "+ warning +";");

			PrintWriter DCNStatus = new PrintWriter(new FileOutputStream(new File("d:/misc/DCN_Status/BEP2_DCN_Status.csv"), true));
			DCNStatus.append(System.getProperty("line.separator")+dateF.format(date)+","+timeF.format(date)+","+total+","+disconnected+","+onLine+","+connected+","+offLine+","+disabled+","+enabled);
			DCNStatus.close();
		}
		catch(Exception e) {
			log.error(e);
		}
	}
}
