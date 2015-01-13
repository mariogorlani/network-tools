package com.nsn.audit.xml;

import java.util.HashMap;

public class XNEType {
private String family;
private String oidSVR;
private String publicCommunity;
private HashMap<String,XNE> versions;

public HashMap<String,XNE> getVersions() {
	return versions;
}

public void setVersions(HashMap<String,XNE> versions) {
	this.versions = versions;
}

public String getFamily() {
	return family;
}

public void setFamily(String family) {
	this.family = family;
}

public String getOidVersion() {
	return oidSVR;
}

public void setOidVersion(String oidVersion) {
	this.oidSVR = oidVersion;
}

public String getPublicCommunity() {
	return publicCommunity;
}

public void setPublicCommunity(String publicCommunity) {
	this.publicCommunity = publicCommunity;
}

public String toString() {
	return "Family:" + family + " Version:" + versions;
}
}
