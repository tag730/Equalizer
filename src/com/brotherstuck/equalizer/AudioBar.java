package com.brotherstuck.equalizer;
import java.util.ArrayList;

public class AudioBar {
	public VerticalProgressBar bar;
	public ArrayList<Integer> progressHistory;
	private final int HISTORY_SIZE=20;
	
	public AudioBar(VerticalProgressBar mBar){
		bar = mBar;
		progressHistory = new ArrayList<Integer>();
	}
	
	public void updateProgress(int mProgress){
		int position=0;
		progressHistory.add(mProgress);
		if (progressHistory.size()>HISTORY_SIZE){
			progressHistory.remove(0);
		}
		
		for(int i=1; i<progressHistory.size();i++){
			if(progressHistory.get(i)>progressHistory.get(position)){
				position=i;
			}
		}
		
		if (position>0 && position < progressHistory.size()-1) {
			if (progressHistory.get(position)>progressHistory.get(position-1) &&
					progressHistory.get(position)>progressHistory.get(position+1)){
				bar.setSecondaryProgress(progressHistory.get(position));
			}
			else {
				bar.setSecondaryProgress(progressHistory.get(progressHistory.size()-1));
			}
		}
		else {
			bar.setSecondaryProgress(progressHistory.get(progressHistory.size()-1));
		}		
		
		bar.setProgress(Math.max(bar.getProgress()-3,mProgress));
		
	}	
}
