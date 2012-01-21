package com.brotherstuck.equalizer;

public class EQData {
	public String key;
	public String value;
	
	public EQData (String keyIn, String valueIn) {
		key=keyIn;
		value=valueIn;
	}
	
	public EQData () {
		key="";
		value="";		
	}	
	
	public String toString() {
		return key;
	}
}
