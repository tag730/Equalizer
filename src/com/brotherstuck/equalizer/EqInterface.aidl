package com.brotherstuck.equalizer;

interface EqInterface {
	int getBandLevelLow();
	int getBandLevelHigh();
	int getNumberOfBands();
	int getCenterFreq(int band);
	int getBandLevel(int band);
	void setBandLevel(int band, int level);
	boolean isBassBoostEnabled();
	void setBassBoostEnabled(boolean isEnabled);
	int getBassBoostStrength();
	void setBassBoostStrength(int strength);
	boolean isVirtualizerEnabled();
	void setVirtualizerEnabled(boolean isEnabled);
	int getVirtualizerStrength();
	void setVirtualizerStrength(int strength);
	boolean isRunning();
}