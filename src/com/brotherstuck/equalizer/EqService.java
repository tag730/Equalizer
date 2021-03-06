package com.brotherstuck.equalizer;

import android.app.Service;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class EqService extends Service {
	
	  // short name for debugging
	  private static final String TAG = EqService.class.getSimpleName();
	
	  private Equalizer myEq;
	  
	  // BassBoost and Virtualizer not currently being implemented
	  private BassBoost mBassBoost;
	  private Virtualizer mVirtualizer;
	  private int maxLevel, minLevel;
	  // isRunning currently not being used
      public static boolean isRunning;

	  @Override
	  public void onCreate() {
	    // In the manifest.xml, this service is listed as a "remote" service, so it
		// runs on its own thread, separate from the Android UI thread
		  
		  // Create log entry for debugging
		  Log.i(TAG,"Service creating...");
		  // Call parent class's create method...
		  super.onCreate();
		  
		  // Setup a Audio Effect "Equalizer" object with priority=10 and audio session id=0 (global)
	      myEq = new Equalizer(10,0);
	      myEq.setEnabled(true);
	      
	      // TODO : Implement BassBoost and Virtualizer
	      // Setup a "BassBoost" object with priority=10, and audio session id=0 (global)
	      mBassBoost=new BassBoost(10,0);
	      // Setup a "Virtualizer" object with priority=10, and audio session id=0 (global)
	      mVirtualizer=new Virtualizer(10,0);
	      
	      // Set up variables for max boost/attenuation for the bands of the equalizer
		  maxLevel=(int)myEq.getBandLevelRange()[1];
	      minLevel=(int)myEq.getBandLevelRange()[0];
	      
	      // Create log entry for debugging
	      Log.i(TAG,"Service created");		  
	  }

	  // Service can be started either on boot, by the BootReceiver class, which listens for "BOOT_COMPLETE"
	  // from OS, or by the AudioEqualizerActivity
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  // Log entry for debugging
		  Log.i(TAG,"Service started");

		  // Currently has no effect
	      isRunning=true;
	      
	      // If service gets killed, restart
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // Log entry for debugging
		  Log.i(TAG,"Service bound");
		  
		  // Return Binder object which delivers interface to Activity
	      return mBinder;
	  }
	  
	  @Override
	  public void onDestroy() {
		  // Log entry for debugging
		  Log.i(TAG,"Service destroyed");
		  
		  // Release EQ object to free memory and control
		  myEq.release();
		  // Set myEq pointer to null
		  myEq=null;
		  
		  // Currently no significant effect
		  isRunning=false;
	  }
	  
	  
	  // Implementations of interface functions, which basically are just getters
	  // and setters for the Equalizer, BassBoost, and Virtualizer objects vital
	  // for control and UI in the Activity
	  private final EqInterface.Stub mBinder = new EqInterface.Stub() {
			public int getBandLevelLow() throws RemoteException {
				Log.i(TAG,"Returned BandLevelLow to Client");
				return minLevel;
			}
			public int getBandLevelHigh() throws RemoteException {
				Log.i(TAG,"Returned BandLevelHigh to Client");
				return maxLevel;
			}
			public int getNumberOfBands() throws RemoteException {
				Log.i(TAG,"Returned NumberOfBands to Client");
				return (int)myEq.getNumberOfBands();
			}
			public int getCenterFreq(int band) throws RemoteException {
				Log.i(TAG,"Returned CenterFreq to Client");
				return myEq.getCenterFreq((short)band);
			}
			public int getBandLevel(int band) throws RemoteException {
				Log.i(TAG,"Returned BandLevel to Client");
				return (int)myEq.getBandLevel((short)band);
			}
			public void setBandLevel(int band, int level) throws RemoteException {
				Log.i(TAG,"Set BandLevel from Client");
				myEq.setBandLevel((short)band,(short)level);
			}
			public boolean isRunning() throws RemoteException {
				Log.i(TAG,"Returned isRunning to Client");
				return isRunning;
			}
			@Override
			public boolean isBassBoostEnabled() throws RemoteException {
				return mBassBoost.getEnabled();
			}
			@Override
			public void setBassBoostEnabled(boolean isEnabled)
					throws RemoteException {
				mBassBoost.setEnabled(isEnabled);				
			}
			@Override
			public int getBassBoostStrength() throws RemoteException {
				return (int)mBassBoost.getRoundedStrength();
			}
			@Override
			public void setBassBoostStrength(int strength)
					throws RemoteException {
				mBassBoost.setStrength((short)strength);
			}
			@Override
			public boolean isVirtualizerEnabled() throws RemoteException {
				return mVirtualizer.getEnabled();
			}
			@Override
			public void setVirtualizerEnabled(boolean isEnabled)
					throws RemoteException {
				mVirtualizer.setEnabled(isEnabled);
			}
			@Override
			public int getVirtualizerStrength() throws RemoteException {
				return (int)mVirtualizer.getRoundedStrength();
			}
			@Override
			public void setVirtualizerStrength(int strength) throws RemoteException {
				mVirtualizer.setStrength((short)strength);
			}
	  };
}
