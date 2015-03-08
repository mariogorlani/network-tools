package com.nsn.audit.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import com.nsn.audit.dataset.NE;

public class SNMPConnection {

	static Logger log = LogManager.getLogger("SNMPConnection");
	private Session snmpTransport;
	private String ipAddress="127.0.0.1";
	private String port="161";
	private int snmpVersion = SnmpConstants.version1;
	private int retries = 3;
	private OctetString readonlyCommunity = new OctetString("public");//no more in use, see devices.xml property
	private String readwriteCommunity = "sysmanager";
	private int timeout = 8000;


	private OID[] nvSvrNEMapTable = {
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.1"),//NE index
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.3"),//NE connStatus (disconnected(0), connected(1), onLine(2), offLine(3))
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.4"),//NE Enabled 0,1
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.5"),//NE Name
			new OID(".1.3.6.1.4.1.7437.2.1.1.1.3.3.1.6") //NE Severity 
	};

	public SNMPConnection(String server) {
		DefaultUdpTransportMapping transport;
		try {
			transport = new DefaultUdpTransportMapping();
			ipAddress = server;
			transport.listen();
			snmpTransport = new Snmp(transport);
		} catch (IOException e) {
			log.error(e);
		}
	}

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

	public HashMap<String, NE> queryStatus(HashMap<String, NE> neList){
		CommunityTarget comtarget = new CommunityTarget();
		comtarget.setVersion(snmpVersion);
		comtarget.setRetries(retries);
		comtarget.setTimeout(timeout);
		comtarget.setCommunity(readonlyCommunity);
		List<String[]> serversDCNStatus = new ArrayList<String[]>();
		log.info(ipAddress+": polling SNMP interface for NE status");
		comtarget.setAddress(new UdpAddress(ipAddress + "/" + GenericDefinitions.port));
		serversDCNStatus.addAll(Arrays.asList(readSNMPTable(comtarget, nvSvrNEMapTable)));

		for (Iterator<String[]> it = serversDCNStatus.iterator(); it.hasNext();) {
			String[] s = (String[]) it.next();
			String connStatus = "";
			//log.info(Arrays.toString(s));
			switch (Integer.valueOf(s[1])) {
			case 0: connStatus = GenericDefinitions.Disconnected;
			break;
			case 1: connStatus = "connecting";
			break;
			case 2: connStatus = GenericDefinitions.Online;
			break;
			case 3: connStatus = GenericDefinitions.Offline;
			break;
			}
			if (s[3]!=null) {
				NE ne = neList.get(s[3]);
				if (ne!=null) ne.setConnStatus(connStatus);
				else log.info("Cannot get NE name: "+ s[3]);
			}
		}
		return neList;
	}
}
