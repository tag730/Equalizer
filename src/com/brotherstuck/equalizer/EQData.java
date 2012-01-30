package com.brotherstuck.equalizer;

public class EQData {
	public String key;
	public String value;
	public boolean eqEnabled, bbEnabled, vEnabled;
	public int bbValue,vValue;
	
	public EQData (String keyIn, String valueIn, boolean eqOn, boolean bbOn, int bbVal, boolean vOn, int vVal) {
		key=keyIn;
		value=valueIn;
		eqEnabled=eqOn;
		bbEnabled=bbOn;
		bbValue=bbVal;
	    vEnabled=vOn;
	    vValue=vVal;
	}
	
	public EQData () {
		key="";
		value="";	
		eqEnabled=false;
		bbEnabled=false;
		bbValue=0;
		vEnabled=false;
		vValue=0;
	}	
	
	public String toString() {
		return key;
	}
}
