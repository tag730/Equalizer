package com.brotherstuck.equalizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AudioEqualizerActivity extends Activity {
	
	public static final String PREFS_NAME = "AudioEQPrefsFile";
	public static final String PRESETS_NAME = "CustomPresetsFile";
	ArrayList<EQData> customPresets;
	ArrayList<VerticalSeekBar> mBands;
	
	// Using LinearLayout instead of R.layout.main (main.xml)
	LinearLayout mLinearLayout;
	Spinner presetSpinner;
	
	// mService is the object for using interface methods
	EqInterface mService = null;
	
	// Boolean for whether we are bound to the EqService or not
	private boolean mIsBound;
	
	@Override
	public void onPause() {
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		String properties=null;
		try {
			properties = mService.getProperties();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if(!(properties==null)){
			editor.putString("lastEqSettings",properties);
			editor.commit();			
		}
		
		saveCustomPresets();
		// If our Activity is closing, we don't want to kill the Service, but we will unbind from it
		if(mIsBound){
            // Detach our existing connection.
            unbindService(mConnection);
            // Set to "not bound"
            mIsBound = false;
		}
		
		// Call parent class's "onPause()"
		super.onPause();
	}
	
	private void saveCustomPresets() {
		String customPresetString="";
		for(int i=0; i<customPresets.size();i++){
			customPresetString+=customPresets.get(i).key+"#"+customPresets.get(i).value+"$";
		}
		Toast.makeText(this, customPresetString, Toast.LENGTH_LONG);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PRESETS_NAME, customPresetString);
		editor.commit();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call parent class's "onCreate(Bundle)" method
    	super.onCreate(savedInstanceState);
        
    	// First, we start the service (if it wasn't started on boot)
        startService(new Intent(EqService.class.getName()));
        
        // Next, we bind to the service to interact with it with our UI
        bindService(new Intent(EqService.class.getName()),
                mConnection, Context.BIND_AUTO_CREATE);
        
        customPresets=new ArrayList<EQData>();
        mBands = new ArrayList<VerticalSeekBar>();
                
        // Set to "bound"
        mIsBound = true;        
    }
    
    private void populateCustomPresets() {
		// To populate the stored values for key-value pair custom presets
    	// from internal storage
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
    	String customPresetString = settings.getString(PRESETS_NAME, "");
    	String[] eqData;
    	String[] keyValuePair;
    	String eqDataDelim="[$]+";
    	String keyValuePairDelim="[#]+";
		if(!customPresetString.equals("")) {
			eqData=customPresetString.split(eqDataDelim);
			for(int i=0; i<eqData.length; i++){
				keyValuePair=eqData[i].split(keyValuePairDelim);
				if(keyValuePair.length==2){
					customPresets.add(
							new EQData(keyValuePair[0],keyValuePair[1])
							);
				}
			}
		} else {
			try {
				customPresets.add(new EQData("Default",mService.getProperties()));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
        
        	// Set our service object up as an interface, so we can use the methods
            mService = EqInterface.Stub.asInterface(service);
            
            SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
            String lastEqSettings = settings.getString("lastEqSettings", "");
            if( !lastEqSettings.equals("") ) {
            	try {
					mService.setProperties(lastEqSettings);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
            populateCustomPresets();
            
            // Now that we can use the methods, we can set up our progressBars and other
            // UI elements for the Equalizer
            setupUI();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };
    
    private void setupUI(){
    	// Main linear layout, vertical
//        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Set as main content view
        setContentView(R.layout.main);       
        mLinearLayout =  (LinearLayout) findViewById(R.id.MainLayout);
        
        LinearLayout presets = new LinearLayout(this);
        presets.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams presetsParams= new LinearLayout.LayoutParams(
        		LayoutParams.FILL_PARENT,
        		LayoutParams.WRAP_CONTENT);
        presets.setLayoutParams(presetsParams);
        
        presetSpinner = new Spinner(this);
        ArrayAdapter<EQData> myPresetsAdapter=new ArrayAdapter<EQData>(this,android.R.layout.simple_spinner_item,customPresets);
        myPresetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSpinner.setAdapter(myPresetsAdapter);
        
        class MyOnItemSelectedListener implements OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {
            	try {
					mService.setProperties(customPresets.get(pos).value);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	updateUI();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        }        
        
        presetSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        Button newPresetButton = new Button(this);
        newPresetButton.setText("New");
        newPresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
					customPresets.add(
							new EQData(Integer.toString(presetSpinner.getAdapter().getCount()),
							mService.getProperties())
							);
					presetSpinner.setSelection(presetSpinner.getAdapter().getCount()-1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
        });
        
        Button savePresetButton = new Button(this);
        savePresetButton.setText("Save");
        savePresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
					customPresets.get(presetSpinner.getSelectedItemPosition()).value=mService.getProperties();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
        });
        
        Button deletePresetButton = new Button(this);
        deletePresetButton.setText("Delete");
        deletePresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(presetSpinner.getSelectedItemPosition()!=0) {
					customPresets.remove(presetSpinner.getSelectedItemPosition());
				}
				presetSpinner.setSelection(presetSpinner.getSelectedItemPosition()-1);
			}
		});
        presets.addView(presetSpinner);
        presets.addView(savePresetButton);
        presets.addView(deletePresetButton);
        presets.addView(newPresetButton);
//        mLinearLayout.addView(presets);
        

        // short value representing number of bands supported by Android Audio Engine
        short bands;
        
        // Have to surround every call to the service interface with a "try" block, just in case
        // our connection has dropped with the service
		try {
			// get number of supported bands
			bands = (short)mService.getNumberOfBands();	

//            LinearLayout row = new LinearLayout(this);
//            row.setOrientation(LinearLayout.HORIZONTAL);
//            LinearLayout.LayoutParams myParams =new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.FILL_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            myParams.gravity=Gravity.TOP;
////            myParams.height=400;
//            myParams.weight=1f;
//            row.setLayoutParams(myParams);
			LinearLayout row = (LinearLayout) findViewById(R.id.eq_space);
			
			// for each of the supported bands, we will set up a slider from -10dB to 10dB boost/attenuation,
			// as well as text labels to assist the user
	        for (short i = 0; i < bands; i++) {
	            final short band = i;	            
	
	            // Setup textview to label the frequency band (with center frequency)
	            TextView freqTextView = new TextView(this);
	            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
//	            tvParams.weight=1;
	            tvParams.gravity=Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
	            freqTextView.setLayoutParams(tvParams);
//	            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
	            if( (mService.getCenterFreq((int)band) / 1000) < 1000 ) {
	            	freqTextView.setText(Integer.toString((mService.getCenterFreq((int)band) / 1000)));
	            } else {
	            	freqTextView.setText((mService.getCenterFreq((int)band) / 1000000) + "k");
	            }
	
	            // now we have a 3-part linearlayout, consisting of lower dB limit label,
	            // then the slider bar (SeekBar), then the higher dB limit text label
	            LinearLayout col= new LinearLayout(this);
	            col.setOrientation(LinearLayout.VERTICAL);
	            col.setLayoutParams(new LinearLayout.LayoutParams(
	                    0,ViewGroup.LayoutParams.FILL_PARENT,1f));
	            
	            col.setWeightSum(1f);

	            
	            // Set up SeekBar for this band	            
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	                    LayoutParams.WRAP_CONTENT,
	                    0);
	            layoutParams.weight = 1f;
	            layoutParams.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
	            VerticalSeekBar bar = new VerticalSeekBar(this);
	            bar.setLayoutParams(layoutParams);	 
	            bar.setProgressDrawable(getResources().getDrawable(R.drawable.scrubber_progress_vertical_holo_dark));
	            bar.setThumb(getResources().getDrawable(R.drawable.seek_holo));
	            // Assign the seekbar the appropriate max value
	            bar.setMax(mService.getBandLevelHigh() - mService.getBandLevelLow());
	            // Apply the current value to the seekbar
	            bar.setProgress(mService.getBandLevel((int)band)-mService.getBandLevelLow());
	            
	
	            // Set up so that when seekbar changes for a certain band, we update the
	            // actual value of the band in our Equalizer object in the EqService class
	            bar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
	                public void onProgressChanged(VerticalSeekBar seekBar, int progress,
	                        boolean fromUser) {
	                    try {
							mService.setBandLevel((int)band, (int) (progress + mService.getBandLevelLow()));
						} catch (RemoteException e) {
							e.printStackTrace();
						}
	                }
	
	                public void onStartTrackingTouch(VerticalSeekBar seekBar) {}
	                public void onStopTrackingTouch(VerticalSeekBar seekBar) {}
	            });
	            
	            mBands.add(bar);
	
	            // Add in the two textviews and seekbar
//	            col.addView(maxDbTextView);
	            col.addView(bar);
	            col.addView(freqTextView);
//	            col.addView(minDbTextView);
	            row.addView(col);
	        }	
	        LinearLayout Bounds= new LinearLayout(this);
	        Bounds.setOrientation(LinearLayout.VERTICAL);
	        LinearLayout.LayoutParams params2 =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	        Bounds.setWeightSum(3f);
	        Bounds.setLayoutParams(params2);
            // Set up lower dB limit text label
	        LinearLayout.LayoutParams paramsMin =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, 0);
            TextView minDbTextView = new TextView(this);
//            paramsMin.gravity=Gravity.BOTTOM;
            paramsMin.weight=1f;
            minDbTextView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            minDbTextView.setLayoutParams(paramsMin);
            minDbTextView.setText((mService.getBandLevelLow() / 100) + " dB");
            
	        LinearLayout.LayoutParams paramsMid =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, 0);
            TextView midDbTextView = new TextView(this);
//            paramsMin.gravity=Gravity.BOTTOM;
            paramsMid.weight=1f;
            midDbTextView.setGravity(Gravity.CENTER);
            midDbTextView.setLayoutParams(paramsMid);
            midDbTextView.setText("0 dB");

            // Set up higher dB limit text label
            TextView maxDbTextView = new TextView(this);
	        LinearLayout.LayoutParams paramsMax =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, 0);
            paramsMax.weight=1f;
            maxDbTextView.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            maxDbTextView.setLayoutParams(paramsMax);
            maxDbTextView.setText((mService.getBandLevelHigh() / 100) + " dB");
            Bounds.addView(maxDbTextView);
            Bounds.addView(midDbTextView);
            Bounds.addView(minDbTextView);
            
            row.addView(Bounds);
//            row.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
            // Add the total band into the main linearlayout
//            mLinearLayout.setWeightSum(2f);
//            mLinearLayout.addView(row);
            mLinearLayout.addView(presets);
	        
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

	public void updateUI() {
		for(int i=0; i<mBands.size(); i++){
			try {
				mBands.get(i).setProgress(mService.getBandLevel(i)-mService.getBandLevelLow());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}