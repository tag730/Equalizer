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
		int tempSecondaryProgress;
		progressHistory.add(mProgress);
		if (progressHistory.size()>HISTORY_SIZE){
			progressHistory.remove(0);
		}
		
		for(int i=1; i<progressHistory.size();i++){
			if(progressHistory.get(i)>=progressHistory.get(position)){
				position=i;
			}
		}
		

		tempSecondaryProgress=progressHistory.get(position);
		if (position==0 && progressHistory.size()>1){
			progressHistory.clear();
		} 
		
//		tempSecondaryProgress=progressHistory.get(position);			
//		if (position==0){
//			progressHistory.clear();
//		}
		
		bar.setSecondaryProgress(Math.max(bar.getSecondaryProgress()-3, tempSecondaryProgress));
		bar.setProgress(Math.max(bar.getProgress()-5,mProgress));
		
	}	
}
