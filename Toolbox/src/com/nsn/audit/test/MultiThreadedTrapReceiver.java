package com.nsn.audit.test;
/*_############################################################################
_## 
_##  SNMP4J - MultiThreadedTrapReceiver.java  
_## 
_##  Copyright 2003-2006  Frank Fock and Jochen Katz (SNMP4J.org)
_##  
_##  Licensed under the Apache License, Version 2.0 (the "License");
_##  you may not use this file except in compliance with the License.
_##  You may obtain a copy of the License at
_##  
_##      http://www.apache.org/licenses/LICENSE-2.0
_##  
_##  Unless required by applicable law or agreed to in writing, software
_##  distributed under the License is distributed on an "AS IS" BASIS,
_##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
_##  See the License for the specific language governing permissions and
_##  limitations under the License.
_##  
_##########################################################################*/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.security.USM;
import org.snmp4j.smi.GenericAddress;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.io.IOException;

import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;


public class MultiThreadedTrapReceiver implements CommandResponder {

	// initialize Log4J logging
	/*
static {
  LogFactory.setLogFactory(new Log4jLogFactory());
  BER.setCheckSequenceLength(false);
}
	 */
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;

	private int n = 0;
	private long start = -1;
	
	private static Logger log = LogManager.getLogger("Main");


	public MultiThreadedTrapReceiver() {
		//  BasicConfigurator.configure();
	}

	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress","udp:172.17.5.100/162"));
		TransportMapping transport;
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress)listenAddress);
		}
		else {
			transport = new DefaultTcpTransportMapping((TcpAddress)listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		snmp.listen();
	}

	public void run() {
		try {
			init();
			snmp.addCommandResponder(this);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
		multithreadedtrapreceiver.run();
	}

	public void processPdu(CommandResponderEvent e) {
		if (start < 0) {
			start = System.currentTimeMillis()-1;
		}
		n++;
		if ((n % 100 == 1)) {
			log.info("Processed " +
					(new DecimalFormat("#.##").format((n / (double)(System.currentTimeMillis() - start)) * 1000))+"/s, total="+n);
		}
		IpAddress addr = ((IpAddress)e.getPeerAddress());
		if (addr.toString().contains("172.17.5")) {
			PDU pdu = new PDU(e.getPDU());
			if (pdu != null)
			{
				int pduType = pdu.getType();
				//List<VariableBinding> NeAlarmEntry = pdu.getBindingList(new OID("1.3.6.1.4.1.7437.2.1.1.1.3.4.4.1.1.1"));
				if ((pduType == PDU.TRAP) || (pduType == PDU.V1TRAP)) {
					
					List<VariableBinding> recovery = pdu.getBindingList(new OID("1.3.6.1.4.1.7437.2.1.1.1.6.9"));
					if (recovery!=null && recovery.get(0).getVariable().toString().equals("1")) 
						log.info("RECOVERY FOR "+pdu);
					else {
						List<VariableBinding> severity = pdu.getBindingList(new OID("1.3.6.1.4.1.7437.2.1.1.1.3.4.4.1.1.1.6"));
					//if (severity!=null &&  severity.get(0).getVariable().toInt()==5) System.out.println(pdu);
						Vector<? extends VariableBinding> vb = pdu.getVariableBindings();
						log.info("alarm: NE " + vb.get(0).getVariable()+ " "+vb.get(2).getVariable());
					}
					/*List<VariableBinding> neName = pdu.getBindingList(new OID("1.3.6.1.4.1.7437.2.1.1.1.3.3"));
					if (neName!=null) System.out.println("NE " + neName.get(0).getVariable().toString());*/

				}
			}
		}
	}
	protected static void printVariableBindings(PDU response) {
		for (int i=0; i<response.size(); i++) {
			VariableBinding vb = response.get(i);
			System.out.println(vb.toString());
		}
	}
}
