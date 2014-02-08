package com.digitalsan.mustang.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

@SuppressWarnings("deprecation")
public class Lever extends SeekBar {

	Context mContext;

	//Because things aren't implemented yet :)
	int currentapiVersion = android.os.Build.VERSION.SDK_INT;

	//Array of colors for bars gradient
	int[] barColors = {Color.BLACK,Color.GRAY};

	// Hold masked listeners
	private ArrayList<OnSlideLeverMaskedListener> onSlideLeverMaskedListeners =
			new ArrayList<Lever.OnSlideLeverMaskedListener>();

	private int MAX_VALUE = 100;
	private int CENTER_VALUE = MAX_VALUE / 2;

	//Amount to mask progress by
	private int FACTOR_VALUE = 5;


	private int maxSlidWhileDown = 0;
	private int lastSignedProgress = 0;
	
	private AsyncTask<String, Integer, Boolean>animationTask;


	public Lever(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initializeLever();
	}

	public Lever(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initializeLever();
	}

	public Lever(Context context) {
		super(context);
		mContext = context;
		initializeLever();
	}

	// Set inital values and center out lever
	@SuppressLint("NewApi")
	private void initializeLever(){

		//Disable Progress Tail
		setProgressDrawable(new ColorDrawable(android.R.color.transparent));

		// Create Gradient for bar
		GradientDrawable barGradient = 
				new GradientDrawable(Orientation.BOTTOM_TOP, barColors);

		//Round bar's corners
		float radi[] = {20,20,20,20,20,20,20,20};
		barGradient.setCornerRadii(radi);

		// Set background appropriately
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
			setBackground(barGradient);
		} else{
			setBackgroundDrawable(barGradient);
		}

		// Max value of seekbar
		setMax(MAX_VALUE);

		//Move scrubber to center
		centerOut();
	}

	//Shift Zero to Center of bar
	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress + CENTER_VALUE);
	}
	
	//Shift zero to center
	@Override
	public synchronized int getProgress() {
		int sProgress =  super.getProgress() - CENTER_VALUE;
		return Math.abs(sProgress);
	}
	
	//Get center deviation with sign
	public synchronized int getSignedProgress(){
		return super.getProgress() - CENTER_VALUE;
	}


	//Animated moving to center
	protected void centerOut(){
		final int freq = 5; 
		final int amplitude = 20; 
		final double decay = 5.0; 

		// Animated bounce back
		animationTask = new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				long millis = System.currentTimeMillis();

				//Bounce for 500MS
				while((millis + 520) > System.currentTimeMillis()){
					
					//Get Time pass in Seconds
					double time = (System.currentTimeMillis() - millis)/ (double)1000;
					
					//Negative progress, negate time, to make
					//Animation appear correclty
					double adjTime = time;
					if (lastSignedProgress < 0)
						adjTime *= (double)-1;
					
					Double trigValue = Math.cos(freq* adjTime * 2*Math.PI);
					if (lastSignedProgress < 0)
						trigValue *= (double)-1;
					
					//Get the position on bar for animation at point in time
					Double sin = amplitude * trigValue / Math.exp(decay * time);

					//Cast to int to use with progress bar
					int nowProgress = sin.intValue();
					
					/** Debugging only
					Log.d("touch", "Time: " + time + 
							" - BOUNCE PROGRESS: " + nowProgress +
							" - SIN:" + sin + " " + sin.intValue() + 
							" - Time: " + adjTime + 
							" - TrigVal: " + trigValue);
							**/

					//Move scrollbar on UI
					publishProgress(nowProgress);
					
					// Try to Limit the update to the UI, 
					// Android may be too fast ;-)
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						return false;
					}
				}
				return true;
			}//END AsyncTask

			//Handle an update from doInBackground
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				setProgress(values[0]);
			}

			//Move scrubber back to center when animation complete
			protected void onPostExecute(Boolean result) {
				setProgress(0);
			};
		}.execute("");
	}

	//Handle user moving scrubber, animation mostly
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// Return to center position
		if( event.getAction() == MotionEvent.ACTION_UP ){
			centerOut();

			// Reset the touch event
			maxSlidWhileDown = 0;
		}
		
		//If user re-clicks while animating, throw animation away
		if( event.getAction() == MotionEvent.ACTION_DOWN ){
			Log.d("touch", "ACTION_DOWN");
			if (animationTask != null || 
					animationTask.getStatus() == AsyncTask.Status.RUNNING){
				animationTask.cancel(true);
			}
			
			//Prevent user from clicking far down the bar
			//and having a TON of events fire at once,
			//the idea of this view is as you slide it,
			//it increses, you must do the work to get the increase
			if (getMaskedProgress() > 1){
				maxSlidWhileDown = getMaskedProgress();
			}
			
		}
		

		if( event.getAction() == MotionEvent.ACTION_MOVE ){
			lastSignedProgress = getSignedProgress();
			//Log.d("touch", "ACTION_MOVE");
		
			//Calculate masked change
			int maskedProgress = 
					calculateMaskedValue(getSignedProgress());

			int UnsignedMaskedProgress = 
					Math.abs(maskedProgress);
			
			//Only trigger change if we increment a value
			if( UnsignedMaskedProgress > maxSlidWhileDown){
				maxSlidWhileDown = UnsignedMaskedProgress;
				triggerMaskedEvent(maskedProgress);
			}
		}

		//return super.onTouchEvent(event);
		return true;
	}

	// Determine if bar is being slid into
	// Negative values of center
	private boolean getNegativeStatus(){
		int progress = super.getProgress();
		return (progress < CENTER_VALUE);
	}

	//Get masked progress
	private int getMaskedProgress(){
		return calculateMaskedValue(getProgress());
	}
	
	// Calculate the masked value
	private int calculateMaskedValue(int progress){
		int maskedChange = progress / FACTOR_VALUE;
		return maskedChange;
	}

	// Notify Listeners
	private void triggerMaskedEvent(int signedMaskedChange){
		boolean negative = getNegativeStatus();

		for(OnSlideLeverMaskedListener listener : onSlideLeverMaskedListeners){
			listener.maskedChangeEvent(negative);
			listener.maskedChangeSinceStart(signedMaskedChange);
		}
	}


	// On event to indicate value change which is not equal to the number
	// of positions the actual underlying seekbar has changed, but masked
	// by a mathematical function to allow the progress bar to flow smootly
	// but not trigger a change and each progress change
	public static abstract class OnSlideLeverMaskedListener{
		public abstract void maskedChangeSinceStart(int signedMaskedChange);
		public abstract void maskedChangeEvent(boolean negative);
	}

	// Add a listener
	public void addOnSlideLeverMaskedListener
	(OnSlideLeverMaskedListener listener){
		onSlideLeverMaskedListeners.add(listener);
	}

}
