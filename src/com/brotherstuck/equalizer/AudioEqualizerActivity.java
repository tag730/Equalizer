package com.brotherstuck.equalizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class AudioEqualizerActivity extends Activity {
	
	public static final String PREFS_NAME = "AudioEQPrefsFile";
	public static final String PRESETS_NAME = "CustomPresetsFile";
	ArrayList<EQData> customPresets;
	ArrayList<VerticalSeekBar> mBands;
    LinearLayout mVisualizerView;
    Visualizer mVisualizer;
    private final float EXPONENT=1.5f;
    private ArrayList<AudioBar> visBars;
    private int mDivisions;
	
	// Using LinearLayout instead of R.layout.main (main.xml)
	LinearLayout mLinearLayout;
	LinearLayout mSecondLinearLayout;
	Spinner presetSpinner;
	
	// mService is the object for using interface methods
	EqInterface mService = null;
	
	// Boolean for whether we are bound to the EqService or not
	private boolean mIsBound;
	
	@Override
	public void onPause() {
		
		// Access properties file named PREFS_NAME
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		
		String properties=null;
		try {
			if (mService!=null){
				properties = mService.getProperties();				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// If properties have been returned from EQ service
		if(!(properties==null)){
			// Save properties (last-used EQ settings) to properties file
			editor.putString("lastEqSettings",properties);
			editor.commit();			
		}
		
		if (mVisualizer!=null) {
			mVisualizer.setEnabled(false);
		}
		
		// Save off custom presets before closing
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
	
	@Override
	public void onStop(){
		super.onStop();
	}
	
	private void saveCustomPresets() {
		// Current implementation of custom presets, concatenated strings holding all of
		// the names of the presets, and their EQ data
		
		// Begin with blank string
		String customPresetString="";
		
		// For each custom preset that has been made
		for(int i=0; i<customPresets.size();i++){
			// add "NAME#SETTINGS$"
			customPresetString+=customPresets.get(i).key+"#"+customPresets.get(i).value+"$";
		}
		
		// Open properties file for saving
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		
		// Save key-value pair where key is PRESETS_NAME and value is the
		// concatenated string of presets
		editor.putString(PRESETS_NAME, customPresetString);
		editor.commit();
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
        
        // Initialize list that holds the custom presets
        customPresets=new ArrayList<EQData>();
        
        // Initialize list that holds the equalizer band seekbar objects
        mBands = new ArrayList<VerticalSeekBar>();
        visBars = new ArrayList<AudioBar>();
                
        // Set to "bound"
        mIsBound = true;     
    }
    
    @Override
    public void onResume() {
    	super.onResume();
        if(mVisualizer!=null){
        	mVisualizer.setEnabled(true);
        }
    }
    
    private void populateCustomPresets() {
		// Current implementation reads in concatenated string of custom presets,
    	// parses the data, and sets up the list of custom presets to be used
    	// by the presetSpinner object
    	
    	String[] eqData; // to hold all preset name-value pairs
    	String[] keyValuePair; // to hold one key-value pair (first element is key, second is value)
    	String eqDataDelim="[$]+"; // Delimiter between key-value pairs
    	String keyValuePairDelim="[#]+"; // Delimiter between each key and value
    	
    	// Access properties file
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
    	// Retrieve custom presets string from properties file
    	String customPresetString = settings.getString(PRESETS_NAME, "");

		if(!customPresetString.equals("")) {
			// create array where each element is a string representing
			// a key-value preset pair
			eqData=customPresetString.split(eqDataDelim);
			
			// for each pair
			for(int i=0; i<eqData.length; i++){
				// split into array where first element is key, second is value
				keyValuePair=eqData[i].split(keyValuePairDelim);
				
				// if there is no error is the string
				if(keyValuePair.length==2){
					// add preset to the customPreset list to be used by the
					// GUI presetSpinner
					customPresets.add(
							new EQData(keyValuePair[0],keyValuePair[1])
							);
				}
			}
		} else {
			// This is the case of the first time the program is installed
			try {
				// create one preset, the Default, which can't be deleted
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
            
            // Access properties file
            SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
            // Retrieve last-used EQ settings (such as: before a reboot)
            String lastEqSettings = settings.getString("lastEqSettings", "");
            if( !lastEqSettings.equals("") ) {
            	// If this isn't the first time a connection is made with the service...
            	try {
            		// Try to access the service and give it the last-used EQ settings
					mService.setProperties(lastEqSettings);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
            
            // Set up presets from last save
            populateCustomPresets();
            
            // Now that we can use the methods in the Service interface
            // we can set up our progressBars and other UI elements for 
            // the Equalizer / Bass Boost / Virtualizer
            setupUI();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };
    
    private void setupUI(){
        // Set as main content view.
    	// Separate xml files are used for portrait and landscape
    	// (see res/layout and res/layout-land)
        setContentView(R.layout.main);       
        
        // Retrieve "main" linear layout from xml,
        // which is used for EQ, BassBoost, and virtualizer
        mLinearLayout =  (LinearLayout) findViewById(R.id.MainLayout);
        mSecondLinearLayout=(LinearLayout) findViewById(R.id.SecondLayout);
        
        // Setup layout for presets, including the spinner
        // and "new", "save", and "delete" buttons
        LinearLayout presets = new LinearLayout(this);
        presets.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams presetsParams= new LinearLayout.LayoutParams(
        		LayoutParams.FILL_PARENT, // take up full horizontal space
        		LayoutParams.WRAP_CONTENT); // take up only as much vertical space as it needs
        presets.setLayoutParams(presetsParams);
        
        // Rest in the bottom-center of its container
        presets.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        
        // Set up spinner for presets (selection list)
        presetSpinner = new Spinner(this);
        
        // Set up adapter to link the spinner to the list of presets
        ArrayAdapter<EQData> myPresetsAdapter=new ArrayAdapter<EQData>(this,android.R.layout.simple_spinner_item,customPresets);
        
        // Setup up graphics for the dropdown items
        myPresetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presetSpinner.setAdapter(myPresetsAdapter);
        
        // create the item-selected listener
        class MyOnItemSelectedListener implements OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {
            	try {
            		// recall the properties from the selected preset
					mService.setProperties(customPresets.get(pos).value);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	// update the equalizer, bassboost, virtualizer sliders
            	updateUI();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        }        
        
        // Use the listener created above with the spinner 
        presetSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        // Set up a button to create a new preset and make it active
        Button newPresetButton = new Button(this);
        newPresetButton.setText("New");
        newPresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
					// Add preset object to the presets list, using current settings
					customPresets.add(
							new EQData(Integer.toString(presetSpinner.getAdapter().getCount()),
							mService.getProperties())
							);
					// Set this new preset as the active preset
					presetSpinner.setSelection(presetSpinner.getAdapter().getCount()-1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
        });
        
        // Set up a button to save the currently-selected preset
        // using the current eq / bassboost / virtualizer settings
        Button savePresetButton = new Button(this);
        savePresetButton.setText("Save");
        savePresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// Set "value" of preset (EQ data) to the current EQ data
				try {
					customPresets.get(presetSpinner.getSelectedItemPosition()).value=mService.getProperties();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
        });
        
        // Set up a button to delete the currently-selected preset
        // and activate the preset that precedes it in the presets list
        Button deletePresetButton = new Button(this);
        deletePresetButton.setText("Delete");
        deletePresetButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// Cannot delete "default" preset
				if(presetSpinner.getSelectedItemPosition()!=0) {
					// delete selected preset
					customPresets.remove(presetSpinner.getSelectedItemPosition());
				}
				// activate preceding preset in the presets list
				presetSpinner.setSelection(presetSpinner.getSelectedItemPosition()-1);
			}
		});
        // Add the spinner and buttons to the horizontal linear layout, to be added to the
        // main layout later on
        presets.addView(presetSpinner);
        presets.addView(savePresetButton);
        presets.addView(deletePresetButton);
        presets.addView(newPresetButton);
        

        // short value representing number of bands supported by Android Audio Engine
        short bands;
        
        // Have to surround every call to the service interface with a "try" block, just in case
        // our connection has dropped with the service
		try {
			// get number of supported bands
			bands = (short)mService.getNumberOfBands();	

			// Retrieve linear layout for EQ sliders
			LinearLayout eqLayout = (LinearLayout) findViewById(R.id.eq_space);
			
			// for each of the supported bands, we will set up a slider from -10dB to 10dB boost/attenuation,
			// as well as text labels to assist the user
	        for (short i = 0; i < bands; i++) {
	            final short band = i;	            
	
	            // Setup textview to label the frequency band (with center frequency)
	            TextView freqTextView = new TextView(this);
	            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
	            
	            // Make the frequency label lie in the bottom-center of its container
	            tvParams.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
	            freqTextView.setLayoutParams(tvParams);
	            
	            // Abbreviate Hz to kHz if the value 1000 Hz or more
	            if( (mService.getCenterFreq((int)band) / 1000) < 1000 ) {
	            	freqTextView.setText(Integer.toString((mService.getCenterFreq((int)band) / 1000)));
	            } else {
	            	freqTextView.setText((mService.getCenterFreq((int)band) / 1000000) + "k");
	            }
	
	            // Set up vertical linearlayout to hold the Eq slider and freq label
	            LinearLayout eqSlider= new LinearLayout(this);
	            eqSlider.setOrientation(LinearLayout.VERTICAL);
	            eqSlider.setLayoutParams(new LinearLayout.LayoutParams(
	                    0, // Trick for splitting width evenly
	                       // set width = 0 and weight = 1 for all elements
	                    ViewGroup.LayoutParams.FILL_PARENT, // take up all 
	                               // available vertical space in container
	                    1f)); // Set weight = 1
	            
	            eqSlider.setWeightSum(1f); // Total sum of weights inside the container is 1

	            
	            // Set up EQ slider for this band	            
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	                    LayoutParams.WRAP_CONTENT, // only take up as much horizontal space 
	                                               // as is needed
	                    0, 1f); // Take up all available vertical space
	            // Position in bottom-center of container
	            layoutParams.setMargins(0, -10, 0, -10);
	            layoutParams.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
	            
	            // set up vertical slider for EQ
	            VerticalSeekBar bar = new VerticalSeekBar(this);
	            bar.setLayoutParams(layoutParams);
	            
	            // Use custom progess graphic from Ice Cream Sandwich
	            bar.setProgressDrawable(getResources().getDrawable(R.drawable.scrubber_progress_vertical_holo_dark));
	            // Use custom Thumb drawable from Ice Cream Sandwich
	            bar.setThumb(getResources().getDrawable(R.drawable.seek_holo));
	            // Assign the seekbar the appropriate max value
	            bar.setMax(mService.getBandLevelHigh() - mService.getBandLevelLow());
	            // Apply the current value to the seekbar
	            bar.setProgress(mService.getBandLevel((int)band)-mService.getBandLevelLow());
	            
	            bar.mPaddingTop=32;
	            bar.mPaddingBottom=32;
	
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

	            eqSlider.addView(bar);
	            eqSlider.addView(freqTextView);

	            eqLayout.addView(eqSlider);
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
            paramsMin.bottomMargin=25;
            paramsMin.weight=1f;
            minDbTextView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            minDbTextView.setLayoutParams(paramsMin);
            minDbTextView.setText((mService.getBandLevelLow() / 100) + " dB");
            
	        LinearLayout.LayoutParams paramsMid =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, 0);
            TextView midDbTextView = new TextView(this);
            paramsMid.weight=1f;
            paramsMid.topMargin=-5;
            midDbTextView.setGravity(Gravity.CENTER);
            midDbTextView.setLayoutParams(paramsMid);
            midDbTextView.setText("0 dB");

            // Set up higher dB limit text label
            TextView maxDbTextView = new TextView(this);
	        LinearLayout.LayoutParams paramsMax =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT, 0);
            paramsMax.weight=1f;
            paramsMax.topMargin=5;
            maxDbTextView.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            maxDbTextView.setLayoutParams(paramsMax);
            maxDbTextView.setText((mService.getBandLevelHigh() / 100) + " dB");
            
            TextView dummyView = new TextView(this);
	        LinearLayout.LayoutParams dummyParams =new LinearLayout.LayoutParams(
	        		LayoutParams.WRAP_CONTENT,
	        		LayoutParams.WRAP_CONTENT);
	        dummyView.setLayoutParams(dummyParams);
	        dummyView.setText("");
	        
            Bounds.addView(maxDbTextView);
            Bounds.addView(midDbTextView);
            Bounds.addView(minDbTextView);
            Bounds.addView(dummyView);
            

            
            
            eqLayout.addView(Bounds);

            mLinearLayout.addView(presets);
            
            SeekBar mBassBoost = (SeekBar) findViewById(R.id.bb_seekbar);
            mBassBoost.setProgressDrawable(getResources().getDrawable(R.drawable.scrubber_progress_holo_dark));
            mBassBoost.setThumb(getResources().getDrawable(R.drawable.seek_holo));
            mBassBoost.setPadding(32, 0, 32, 10);
            
            SeekBar mVirtualizer = (SeekBar) findViewById(R.id.v_seekbar);
            mVirtualizer.setProgressDrawable(getResources().getDrawable(R.drawable.scrubber_progress_holo_dark));
            mVirtualizer.setThumb(getResources().getDrawable(R.drawable.seek_holo));
            mVirtualizer.setPadding(32, 0, 32, 10);
            
            
            // Create a VisualizerView (defined below), which will render the simplified audio
            // wave form to a Canvas.
//            mVisualizerView = new VisualizerView(this);
//            mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.FILL_PARENT,
//                    (int)(50 * getResources().getDisplayMetrics().density)));
//            mSecondLinearLayout.addView(mVisualizerView);

            // Create the Visualizer object and attach it to our media player.
            mVisualizer = new Visualizer(0);
//            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setEnabled(false);
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);            	
            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                        int samplingRate) {}

                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                	updateVisualizer(bytes);
//                	Log.i(this.getClass().getSimpleName(), Integer.toString(samplingRate));
                }
            }, Visualizer.getMaxCaptureRate(), false, true);

            mVisualizer.setEnabled(true);
            
            mVisualizerView = (LinearLayout) findViewById(R.id.vis_space);
            mDivisions = (int)(Math.log(mVisualizer.getCaptureSize()/2)/Math.log(EXPONENT))-1;
            visBars.clear();
            for( int i=0; i<mDivisions; i++ ){
            	VerticalProgressBar mBar = new VerticalProgressBar(this);
            	mBar.setProgressDrawable(getResources().getDrawable(R.drawable.visbar_progress));
            	mBar.setProgress(0);
            	LinearLayout.LayoutParams mBarParams = new LinearLayout.LayoutParams(
            			0, LayoutParams.FILL_PARENT,1f);
            	mBar.setLayoutParams(mBarParams);
            	visBars.add(new AudioBar(mBar));
            	mVisualizerView.addView(mBar);            	
            }           
            
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

	protected void updateVisualizer(byte[] bytes) {        
		// we will update each slider with the current Fourier Transform data
        for (int i = 0; i < visBars.size(); i++) { 
        	int sum, count, avg;
        	sum=0;
        	count=0;
        	for (int j=(int) Math.pow(EXPONENT,i); j<=(int) Math.pow(EXPONENT,i+1);j++){
            	byte rfk = bytes[2*j];
            	byte ifk = bytes[2*j+1];
            	int magnitude = (rfk*rfk + ifk*ifk);
            	sum+=magnitude;
            	count++;
        	}
        	avg = sum/count;
        	int dbValue = (int)(20*Math.log10(avg));
        	
//////////////// COMMENTED OUT BLOCK USED FOR _NO AVERAGING_ /////////////////
//        	int j = i+1;
//        	byte rfk = bytes[2*((int) Math.pow(EXPONENT,j)-1)];
//        	byte ifk = bytes[2*((int) Math.pow(EXPONENT,j)-1)+1];
//        	int magnitude = (rfk*rfk + ifk*ifk);
//        	int dbValue = (int)(10*Math.log10(magnitude));
//        	Log.i("AudioEqualizerActivity",Integer.toString(dbValue));
//////////////////////////////////////////////////////////////////////////////
        	visBars.get(i).updateProgress(dbValue);        		
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