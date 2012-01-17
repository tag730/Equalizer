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
	
    /** Called when the activity is first created. */
	LinearLayout mLinearLayout;
	EqInterface mService = null;
	private boolean mIsBound;
	
	@Override
	public void onPause() {
		super.onPause();
		if(mIsBound){
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startService(new Intent(EqService.class.getName()));
        bindService(new Intent(EqService.class.getName()),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;        
    }
    
    private void setupUI(){
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        setContentView(mLinearLayout);       

        short bands;
		try {
			bands = (short)mService.getNumberOfBands();
        	
            TextView freqTextView;// = new TextView(this);
//            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.FILL_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//            freqTextView.setText("Preamp");
//            mLinearLayout.addView(freqTextView);

            LinearLayout row;// = new LinearLayout(this);
//            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView;// = new TextView(this);
//            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            minDbTextView.setText((mService.getBandLevelLow() / 100) + " dB");

            TextView maxDbTextView;// = new TextView(this);
//            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            maxDbTextView.setText((mService.getBandLevelHigh() / 100) + " dB");

            LinearLayout.LayoutParams layoutParams;// = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.FILL_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.weight = 1;
            SeekBar bar;// = new SeekBar(this);
//            bar.setLayoutParams(layoutParams);
//            bar.setMax(mService.getBandLevelHigh() - mService.getBandLevelLow());
//            bar.setProgress(mService.getPreAmpLevel()-mService.getBandLevelLow());
//
//            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                public void onProgressChanged(SeekBar seekBar, int progress,
//                        boolean fromUser) {
//                    try {
//						mService.setPreAmpLevel((int) (progress + mService.getBandLevelLow()));
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//                }
//
//                public void onStartTrackingTouch(SeekBar seekBar) {}
//                public void onStopTrackingTouch(SeekBar seekBar) {}
//            });

//            row.addView(minDbTextView);
//            row.addView(bar);
//            row.addView(maxDbTextView);
//
//            mLinearLayout.addView(row);
			

	        for (short i = 0; i < bands; i++) {
	            final short band = i;
	
	            freqTextView = new TextView(this);
	            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.FILL_PARENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
	            freqTextView.setText((mService.getCenterFreq((int)band) / 1000) + " Hz");
	            mLinearLayout.addView(freqTextView);
	
	            row = new LinearLayout(this);
	            row.setOrientation(LinearLayout.HORIZONTAL);
	
	            minDbTextView = new TextView(this);
	            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            minDbTextView.setText((mService.getBandLevelLow() / 100) + " dB");
	
	            maxDbTextView = new TextView(this);
	            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.WRAP_CONTENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT));
	            maxDbTextView.setText((mService.getBandLevelHigh() / 100) + " dB");
	
	            layoutParams = new LinearLayout.LayoutParams(
	                    ViewGroup.LayoutParams.FILL_PARENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
	            layoutParams.weight = 1;
	            bar = new SeekBar(this);
	            bar.setLayoutParams(layoutParams);
	            bar.setMax(mService.getBandLevelHigh() - mService.getBandLevelLow());
	            bar.setProgress(mService.getBandLevel((int)band)-mService.getBandLevelLow());
	
	            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	                public void onProgressChanged(SeekBar seekBar, int progress,
	                        boolean fromUser) {
	                    try {
							mService.setBandLevel((int)band, (int) (progress + mService.getBandLevelLow()));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	
	                public void onStartTrackingTouch(SeekBar seekBar) {}
	                public void onStopTrackingTouch(SeekBar seekBar) {}
	            });
	
	            row.addView(minDbTextView);
	            row.addView(bar);
	            row.addView(maxDbTextView);
	
	            mLinearLayout.addView(row);
	        }	
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
            mService = EqInterface.Stub.asInterface(service);
            setupUI();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };
}