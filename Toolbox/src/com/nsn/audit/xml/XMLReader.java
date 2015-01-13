package com.nsn.audit.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;

public class XMLReader {
	private HashMap<String,XNEType> xNEs;
	static Logger log = LogManager.getLogger("XMLReader");

	public XMLReader(String xmlInput) throws Exception{
		this.xNEs = new HashMap<String, XNEType>();
		File fXmlFile = new File(xmlInput);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList devices = doc.getElementsByTagName("device");

		for (int temp = 0; temp < devices.getLength(); temp++) {
			Node device = devices.item(temp);
			if (device.getNodeType() == Node.ELEMENT_NODE) {
				Element eDevice = (Element) device;
				XNEType xNEType = new XNEType();
				xNEType.setFamily(eDevice.getAttribute("family"));
				xNEType.setOidVersion(eDevice.getAttribute("oidsvr"));
				xNEType.setPublicCommunity(eDevice.getAttribute("publicCommunity"));
				HashMap<String,XNE> xVersions = new HashMap<String, XNE>();
				NodeList eVersions = eDevice.getElementsByTagName("version");
				for (int t = 0; t < eVersions.getLength(); t++) {
					Node version = eVersions.item(t);
					XNE xNE = new XNE();
					if (version.getNodeType() == Node.ELEMENT_NODE) {
						Element eVersion = (Element)version;
						NodeList eParams = eVersion.getElementsByTagName("param"); 
						HashMap<String, XParam> xParams = new HashMap<String, XParam>();
						for (int j = 0; j < eParams.getLength(); j++) {
							Element eParam = (Element)eParams.item(j);
							XParam xParam = new XParam();
							if (eParam.getAttribute("type").equals("scalar")) 
							{
								xParam.setType("scalar");
								xParam.setOrder(false);
								String[][] oids = new String[1][1];
								oids[0][0] = getValue("oid", eParam);
								xParam.setOids(oids);
								String[] expected = new String[1];
								//expected[0] = getValue("expected", eParam);
								xParam.setExpected(expected);

							} else if (eParam.getAttribute("type").equals("table")) 
							{
								xParam.setType("table");
								xParam.setOrder(eParam.getAttribute("order").equals("yes"));
								NodeList columns = eParam.getElementsByTagName("column");
								String[][] oids = new String[columns.getLength()][2];
								for (int c = 0; c < columns.getLength(); c++) {
									Element column = (Element)columns.item(c);
									oids[c][0] = getValue("name", column);
									oids[c][1] = getValue("oid", column);
								}
								xParam.setOids(oids);
							} 

							NodeList enumsNL = eParam.getElementsByTagName("enum");
							if (enumsNL.getLength()>0){
								HashMap<String, String> enums = new HashMap<String, String>();
								for (int c = 0; c < enumsNL.getLength(); c++) {
									Element en = (Element)enumsNL.item(c);
									enums.put(en.getAttribute("value"), en.getFirstChild().getNodeValue());
								}
								xParam.setEnums(enums);
							}
							xParams.put(eParam.getAttribute("name"), xParam );
						}
						xNE.setParams(xParams);
						xVersions.put(eVersion.getAttribute("svr"),xNE);;
					}
					xNEType.setVersions(xVersions);
					log.info(xNEType.toString());
				}
				xNEs.put(eDevice.getAttribute("type"),xNEType);	
			}
		}
	}

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	public HashMap<String, XNEType> getxNEs() {
		return xNEs;
	}
}