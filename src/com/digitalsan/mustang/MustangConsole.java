package com.digitalsan.mustang;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.digitalsan.mustang.R;
import com.digitalsan.mustang.can.helpers.ButtonHelper;
import com.digitalsan.mustang.can.helpers.LeverHelper;
import com.digitalsan.mustang.openxc.RadioButtonEvent;
import com.digitalsan.mustang.views.Lever;
import com.openxc.VehicleManager;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleButtonEvent;

public class MustangConsole extends Activity {

	private VehicleManager mVehicleManager;
	private Context mContext;

	//Levers
	private Lever mVolumeLever;
	private Lever mTuneLever;

	//Helpers for Levers
	LeverHelper volumeLever = new LeverHelper(VehicleButtonEvent.ButtonId.VOLUMEUP,
			VehicleButtonEvent.ButtonId.VOLUMEDOWN);
	LeverHelper tuneLeverew = new LeverHelper(VehicleButtonEvent.ButtonId.TUNEUP,
			VehicleButtonEvent.ButtonId.TUNEDOWN);
public void onPress(View v){
	Log.d("Inside activity", "Onpress method called.");
}

public void onRelease(View v){
	Log.d("Inside activity", "Onpress method called.");
}

	
	public void radioButtonClick(View v){
		//Look up the button and send event to can bus
		try{
			RadioButtonEvent.ButtonId buttonPressed =
					RadioButtonEvent.ButtonId.valueOf((String) v.getTag());
			
		}catch (IllegalArgumentException ex){
			Log.w("Console Button Click",
					"Tag not found in RadioButtonEvent ButtonId enum: "  );
		}
	}
	
	// Handle preset clicks
	public void presetOnClick(View v){
		if( !v.getClass().equals(Button.class))
			return;
		
		//Sift through enum of Button IDs,
		//match by button text [09] on items with PRESET+[09]
		for(VehicleButtonEvent.ButtonId buttonId : VehicleButtonEvent.ButtonId.values())
			if(buttonId.name().contains("PRESET" + ((Button)v).getText())){ 
				ButtonHelper.sendPressRelease(mVehicleManager, buttonId);
				return;
			}
	}
	
	
	// Handle Other buttons
	public void sourceButtonOnClick(View v){
		if( !v.getClass().equals(Button.class))
			return;
		
		String buttonTag = (String)v.getTag();
		
		//Look up button, possiblity not in enum
		VehicleButtonEvent.ButtonId vbe;
		try{
			vbe = VehicleButtonEvent.ButtonId.valueOf(buttonTag);
		}catch (IllegalArgumentException iae){
			Log.e("MustangConsole - sourceButtonOnClick",
					"Tag name not found in enum, ignoring button");
			return;
		}
		
		ButtonHelper.sendPressRelease(mVehicleManager, vbe);
	}






	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_radiopanel);

		//Levers
		mVolumeLever = (Lever) findViewById(R.id.volume_lever);
		mTuneLever = (Lever) findViewById(R.id.tune_lever);

		//Handle Volume Slides
		Lever.OnSlideLeverMaskedListener volumeSlideListener = new Lever.OnSlideLeverMaskedListener() {			
			@Override
			public void maskedChangeSinceStart(int signedMaskedChange) {}

			@Override
			public void maskedChangeEvent(boolean negative) {
				//Send to openxc
				try {
					if( negative ) 
						volumeLever.sendLeverDown(mVehicleManager);
					else
						volumeLever.sendLeverUp(mVehicleManager);
				} catch (UnrecognizedMeasurementTypeException e) {
					Log.e("Exception", "UnrecognizedMeasurementTypeException");
				}
			}

		};
		mVolumeLever.addOnSlideLeverMaskedListener(volumeSlideListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mustang_console, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		// When the activity goes into the background or exits, we want to make
		// sure to unbind from the service to avoid leaking memory
		if(mVehicleManager != null) {
			Log.i("openxc", "Unbinding from Vehicle Manager");
			unbindService(mConnection);
			mVehicleManager = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// When the activity starts up or returns from the background,
		// re-connect to the VehicleManager so we can receive updates.
		if(mVehicleManager == null) {
			Intent intent = new Intent(this, VehicleManager.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}



	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the VehicleManager service is established, i.e. bound.
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i("openxc", "Bound to VehicleManager");
			// When the VehicleManager starts up, we store a reference to it
			// here in "mVehicleManager" so we can call functions on it
			// elsewhere in our code.
			mVehicleManager = ((VehicleManager.VehicleBinder) service)
					.getService();
		}
		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.w("openxc", "VehicleManager Service  disconnected unexpectedly");
			mVehicleManager = null;
		}
	};
}
