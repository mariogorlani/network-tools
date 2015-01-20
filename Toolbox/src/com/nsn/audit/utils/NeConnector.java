package com.nsn.audit.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.nsn.audit.dataset.NE;
import com.nsn.audit.dataset.Param;
import com.nsn.audit.xml.XMLReader;
import com.nsn.audit.xml.XNE;
import com.nsn.audit.xml.XNEType;
import com.nsn.audit.xml.XParam;

public class NeConnector implements Runnable{
	protected DefaultUdpTransportMapping transport;
	protected Snmp snmpTransport;
	protected CommunityTarget comtarget;
	protected HashMap<String,XNEType> xNEs;
	protected ArrayList<NE> neList;
	//protected SQLConnection sqlCon;
	static Logger log = LogManager.getLogger("NeConnector");

	/**
	 * NeConnector takes care of the collection of parameters from NeList according to the xmlReader xNEs template
	 * @param xmlReader
	 */
	public NeConnector(XMLReader xmlReader,ArrayList<NE> neList) {
		// Create TransportMapping and Listen
		DefaultUdpTransportMapping transport;
		createComtarget();
		//this.sqlCon = new SQLConnection();
		this.xNEs = xmlReader.getxNEs();
		this.neList = neList;
		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
			// Create Snmp object for sending data to Agent
			snmpTransport = new Snmp(transport);
		} catch (IOException e) {
			log.error(e);
			System.exit(0);
		}
	}

	/**
	 * Create Target Address object - normal GET
	 */
	private void createComtarget() {
		comtarget = new CommunityTarget();
		comtarget.setVersion(GenericDefinitions.snmpVersion);
		comtarget.setAddress(new UdpAddress(GenericDefinitions.ipAddress + "/" + GenericDefinitions.port));
		comtarget.setRetries(GenericDefinitions.retries);
		comtarget.setTimeout(GenericDefinitions.timeout);
	}

	/**
	 * Validate IP Address
	 * @param ip
	 * @return true is valid IP has been passed
	 */
	public boolean validateIP(String ip){          
		String PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();             
	}

	/**
	 * Validate IP Address
	 * @param ip
	 * @return true is valid IP has been passed
	 */
	public boolean validateNE(NE ne) {
		return ne.getType().contains("FP")&&validateIP(ne.getIP())&&!ne.getConnStatus().equals(GenericDefinitions.Disabled);
	}


	// TODO: SNMP close must be set somewhere Definitions.snmp.close();

	/**
	 * Read list of OIDs value from comtarget.
	 * The result will be a list of strings with ; as column separator
	 * @param stringOIDs array string
	 * @return List<List<String>> result
	 */
	public String[] readSNMPTable(OID[] columnOid) {
		TableUtils tUtils = new TableUtils(snmpTransport, new DefaultPDUFactory());

		List<TableEvent> events = tUtils.getTable(comtarget, columnOid, null, null);
		String[] list = new String[events.size()];
		int i = 0;
		for (TableEvent event : events) {
			if (event.isError()) {
				log.error("readSNMPTable failed for " + comtarget.getAddress() + "; error is "+ event.getErrorMessage());
			} else{
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < event.getColumns().length; j++) {
					sb.append(event.getColumns()[j].getVariable().toString());
					if (j<event.getColumns().length-1) sb.append(GenericDefinitions.VarbindSeparator);
				} 
				list[i]=sb.toString();
			}

			i++;
		}
		return list;
	}

	// Read scalar
	public String readSNMPScalar(String oid) {
		String output = "";
		// Create the PDU object
		PDU pdu_GetOid = createPduGet(oid);
		try {
			ResponseEvent response_Event = snmpTransport.get(pdu_GetOid, comtarget);
			if (response_Event!=null){
				PDU pdu = response_Event.getResponse();
				if (pdu!=null) 
					output = pdu.getVariable(new OID(oid)).toString();
				output = output.trim();
			} else {
				log.error(comtarget.getAddress()+" cannot get "+oid);
			}
		} catch (Exception e) {
			log.error("readSNMPScalar failed for " +comtarget.getAddress()+"; OID: " + oid);
		}
		return output;
	}

	/**
	 * 	Prepare PDU for simple GET
	 * @param targetObj
	 * @return
	 */
	private PDU createPduGet(String targetObj) {
		PDU new_pdu = new PDU();
		new_pdu.add(new VariableBinding(new OID(targetObj)));
		new_pdu.setType(PDU.GET);
		new_pdu.setRequestID(new Integer32(1));
		return new_pdu;
	}



	private void printNEtoFiles(NE actualNE) {
		FilePrintHelper.getInstance().WriteToFile(actualNE);

	}

	/**
	 * get the NE template from the XML template files containing 
	 * params OID, order, version
	 * @param actualNE
	 * @return
	 */
	private HashMap<String, XParam> getParamsFromXML(NE actualNE) {
		try {
			XNEType xNEType = xNEs.get(actualNE.getType());
			actualNE.setSysName(readSNMPScalar(GenericDefinitions.systemName));
			String oidVersion = xNEType.getOidVersion();
			String svr = readSNMPScalar(oidVersion);
			actualNE.setSvr(svr);
			Iterator<Entry<String, XNE>> xVersions = xNEType.getVersions().entrySet().iterator();
			HashMap<String, XParam> tempParam = null;
			while (xVersions.hasNext()){
				Map.Entry<String, XNE> xVersion = (Map.Entry<String, XNE>)xVersions.next();
				if (svr.contains(xVersion.getKey())) tempParam = xVersion.getValue().getParams();
			}
			return tempParam;
		} catch(Exception e) {
			log.error(e);
			return null;
		}
	}

	private void collectFromNe(NE actualNE) {
		try{
			actualNE.setTime(new Timestamp(System.currentTimeMillis()));
			HashMap<String, Param> collectedParams = new HashMap<String, Param>();	
			HashMap<String, XParam> xParams =  getParamsFromXML(actualNE);
			Iterator<Entry<String, XParam>> xParamIt = xParams.entrySet().iterator();
			while (xParamIt.hasNext()){
				Map.Entry<String, XParam> xParam = (Map.Entry<String, XParam>)xParamIt.next();
				String[][] xOIDs = xParam.getValue().getOids();
				Param collectedParam = new Param();
				collectedParam.setTimestamp(new Timestamp(System.currentTimeMillis()));
				if (xParam.getValue().getType().equals("scalar")) {
					String result = readSNMPScalar(xOIDs[0][0]);
					String[] config = {"NIL"};
					if (result!=null) {
						if (xParam.getValue().getEnums()!=null)
							config[0] = xParam.getValue().getEnums().get(result);
						else
							config[0] = result;
						actualNE.setConnStatus(GenericDefinitions.Online);
					}
					collectedParam.setConfig(config);
					collectedParams.put(xParam.getKey(), collectedParam);
				}
				else if (xParam.getValue().getType().equals("table")) {
					OID[] columnOid = new OID[xOIDs.length];
					for (int i = 0; i < xOIDs.length; i++) {
						columnOid[i] = new OID(xOIDs[i][1]);
					}
					String[] result = readSNMPTable(columnOid);
					if (result!=null) {
						//if order is not fixed, let's order the result
						if (!xParam.getValue().getOrder()) 
							Arrays.sort(result);
						collectedParam.setConfig(result);
						actualNE.setConnStatus(GenericDefinitions.Online);
					}
					collectedParams.put(xParam.getKey(), collectedParam);
				}
				actualNE.setParams(collectedParams);
			}
		}catch (Exception e) {
			log.error("collectFromNE fails for " + actualNE.getName()+ "; Error is :"+e);
		}
	}		

	/**
	 * Collect the parameters from the network
	 * @param neList
	 */
	public void audit() {
		Iterator<NE> itr = neList.iterator();
		int count = 0;
		while (itr.hasNext()) {
			count++;
			NE actualNE = itr.next();
			log.info(Thread.currentThread().getName()+" starts audit for "+ actualNE.getName()+"; " + count +"/"+neList.size());
			try {
				if (validateNE(actualNE)) {
					comtarget.setAddress(new UdpAddress(actualNE.getIP() + "/" + GenericDefinitions.port));
					comtarget.setCommunity(new OctetString(xNEs.get(actualNE.getType()).getPublicCommunity()));
					collectFromNe(actualNE);
					log.info("Audit completed for "+ actualNE.getName() +"; svr: "+actualNE.getSvr());
				} else {
					log.info("Audit skipped for "+ actualNE.getName());
				}
			}
			catch (Exception e) {
				log.error("Audit failed for "+actualNE.getName() +" IP:"+actualNE.getIP()+".");
			}
			//printNEtoFiles(actualNE);
		}
	}

	@Override
	public void run() {
		audit();
	}
}
