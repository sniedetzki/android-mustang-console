package com.digitalsan.mustang.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.digitalsan.mustang.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class RadioButton extends Button{

	/**
	 * Class Members
	 * 
	 */
	Context mContext;
	Activity activity;
	Method onPressMethod = null;
	Method onReleaseMethod = null;




	/**
	 * Constructors
	 * 
	 */
	public RadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		//Init events not supported 
		//by visual editor
		if( !isInEditMode() ){
			initXml(attrs);
		}
		
	}

	public RadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		//Init events not supported 
		//by visual editor
		if( !isInEditMode() ){
			initXml(attrs);
		}
		
	}

	public RadioButton(Context context) {
		super(context);
		mContext = context;
	}


	/**
	 * Class Methods
	 * 
	 */

	//Init xml attributes
	public void initXml(AttributeSet attrs){
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.RadioButton);

		//Get activity that ultimately started this view
		activity = (Activity)getRootView().getContext();

		//Cycle through and get attrs
		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i)
		{
			int attr = a.getIndex(i);
			String mName;
			switch (attr)
			{
			case R.styleable.RadioButton_onPress:
				mName = a.getString(attr);
				try{
					onPressMethod = activity.getClass().getMethod(mName, View.class);
					Log.d("RadioButton - Found onPressMethod", "Method name: " + onPressMethod.getName() + " from" + mContext.getClass().getCanonicalName());
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case R.styleable.RadioButton_onRelease:
				mName = a.getString(attr);
				try{
					onReleaseMethod = activity.getClass().getMethod(mName, View.class);
					Log.d("RadioButton - Found onReleaseMethod", "Method name: " + onReleaseMethod.getName() + " from" + mContext.getClass().getCanonicalName());
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}
		}//END FOR
		a.recycle();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		//Get only type of action
		int action = (event.getAction() & MotionEvent.ACTION_MASK);

		//Find out what action happened
		switch ( action ) {
		case MotionEvent.ACTION_DOWN:
			if(onPressMethod != null)
				try {
					Log.d("RadioButton Press", "Pressed the radio button, calling onPressMethod:" + onPressMethod.getName() +" from: " + activity.getClass().getCanonicalName());
					onPressMethod.invoke(activity, this);
				} catch (Exception e) {

				}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(onReleaseMethod != null)
				try {
					Log.d("RadioButton Release", "Released the radio button, calling onReleaseMethod: " + onReleaseMethod.getName() + " from: " + activity.getClass().getCanonicalName());
					onReleaseMethod.invoke(activity, this);
				} catch (Exception e) {

				}
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

}
