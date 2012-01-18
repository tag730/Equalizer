package com.brotherstuck.equalizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioEqualizerActivity extends Activity {
	
	// Using LinearLayout instead of R.layout.main (main.xml)
	LinearLayout mLinearLayout;
	
	// mService is the object for using interface methods
	EqInterface mService = null;
	
	// Boolean for whether we are bound to the EqService or not
	private boolean mIsBound;
	
	@Override
	public void onPause() {
		// Call parent class's "onPause()"
		super.onPause();
		
		// If our Activity is closing, we don't want to kill the Service, but we will unbind from it
		if(mIsBound){
            // Detach our existing connection.
            unbindService(mConnection);
            // Set to "not bound"
            mIsBound = false;
		}
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
        
        // Set to "bound"
        mIsBound = true;        
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
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Set as main content view
        setContentView(mLinearLayout);       

        // short value representing number of bands supported by Android Audio Engine
        short bands;
        
        // Have to surround every call to the service interface with a "try" block, just in case
        // our connection has dropped with the service
		try {
			// get number of supported bands
			bands = (short)mService.getNumberOfBands();	

			// for each of the supported bands, we will set up a slider from -10dB to 10dB boost/attenuation,
			// as well as text labels to assist the user
	        for (short i = 0; i < bands; i++) {
	            final short band = i;
	            LinearLayout row = new LinearLayout(this);
	            row.setOrientation(LinearLayout.VERTICAL);
	            row.setLayoutParams(new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.FILL_PARENT,
	                    LinearLayout.LayoutParams.FILL_PARENT, 1f));	            
	
	            // Setup textview to label the frequency band (with center frequency)
	            TextView freqTextView = new TextView(this);
	            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
	            freqTextView.setText((mService.getCenterFreq((int)band) / 1000) + "\nHz");
	            row.addView(freqTextView);
	
	            // now we have a 3-part linearlayout, consisting of lower dB limit label,
	            // then the slider bar (SeekBar), then the higher dB limit text label
	            LinearLayout col= new LinearLayout(this);
	            col.setOrientation(LinearLayout.VERTICAL);
	            col.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.FILL_PARENT));
	
	            // Set up lower dB limit text label
	            TextView minDbTextView = new TextView(this);
	            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            minDbTextView.setText((mService.getBandLevelLow() / 100) + " dB");
	
	            // Set up higher dB limit text label
	            TextView maxDbTextView = new TextView(this);
	            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            maxDbTextView.setText((mService.getBandLevelHigh() / 100) + " dB");
	            
	            // Set up SeekBar for this band
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.FILL_PARENT);
	            layoutParams.weight = 1;
	            VerticalSeekBar bar = new VerticalSeekBar(this);
	            bar.setLayoutParams(layoutParams);
	            bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_vertical));
	            bar.setThumb(getResources().getDrawable(R.drawable.seek_thumb));
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
	
	            // Add in the two textviews and seekbar
	            col.addView(maxDbTextView);
	            col.addView(bar);
	            col.addView(minDbTextView);
	            row.addView(col);
	            
	            // Add the total band into the main linearlayout
	            mLinearLayout.addView(row);
	        }	
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
}