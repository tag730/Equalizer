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
	
	private static final String TAG = EqService.class.getSimpleName();
	
	  private Equalizer myEq;
	  private BassBoost mBassBoost;
	  private Virtualizer mVirtualizer;
	  
	  
	  private int maxLevel, minLevel;
      public static boolean isRunning;

	  @Override
	  public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
//	    HandlerThread thread = new HandlerThread("ServiceStartArguments",
//	            Process.THREAD_PRIORITY_BACKGROUND);
//	    thread.start();
//	    
//	    // Get the HandlerThread's Looper and use it for our Handler 
//	    mServiceLooper = thread.getLooper();
//	    mServiceHandler = new ServiceHandler(mServiceLooper);
		  Log.i(TAG,"Service creating...");
		  super.onCreate();
		  
	      myEq = new Equalizer(10,0);
	      myEq.setEnabled(true);
	      myEq.usePreset((short)0);
	      mBassBoost=new BassBoost(10,0);
//	      mBassBoost.setEnabled(true);
	      mVirtualizer=new Virtualizer(10,0);
//	      mVirtualizer.setEnabled(true);
	      
		  maxLevel=(int)myEq.getBandLevelRange()[1];
	      minLevel=(int)myEq.getBandLevelRange()[0];
	      Log.i(TAG,"Service created");		  
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  Log.i(TAG,"Service started");

	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job

	      isRunning=true;
	      
	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
		  Log.i(TAG,"Service bound");
	      return mBinder;
	  }
	  
	  @Override
	  public void onDestroy() {
	    Log.i(TAG,"Service destroyed");
	    myEq.release();
	    myEq=null;
	    isRunning=false;
	  }
	  
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
