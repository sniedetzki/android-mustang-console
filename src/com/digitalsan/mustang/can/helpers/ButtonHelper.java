package com.digitalsan.mustang.can.helpers;

import com.openxc.VehicleManager;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleButtonEvent;

public class ButtonHelper {

	VehicleButtonEvent.ButtonId button;
	VehicleButtonEvent buttonEventPressed;
	VehicleButtonEvent buttonEventReleased;

	//Take two button ids as arguments
	public ButtonHelper(VehicleButtonEvent.ButtonId button) {
		// Two action associated with a button
		this.button = button;
		
		//Prepare button events
		buttonEventPressed = 
				new VehicleButtonEvent(button, VehicleButtonEvent.ButtonAction.PRESSED);
		
		buttonEventReleased = 
				new VehicleButtonEvent(button, VehicleButtonEvent.ButtonAction.RELEASED);
	}
	
	//Return openxc class representing a press
	public VehicleButtonEvent getPressedEvent(){
		return buttonEventPressed;
	}
	
	//Return openxc class representing a release
	public VehicleButtonEvent getReleasedEvent(){
		return buttonEventReleased;
	}
	
	
	public boolean sendPressRelease(VehicleManager vehicleManager) 
			throws UnrecognizedMeasurementTypeException{
		
		if (vehicleManager == null)
			return false;
			
		//Send to car, return anded value, if bot succeeded
		return vehicleManager.send(buttonEventPressed) &&
				vehicleManager.send(buttonEventReleased);
	}
	
	//Helper Method to eliminate need to create a bunch of objects
	public static boolean sendPressRelease(VehicleManager vehicleManager, VehicleButtonEvent.ButtonId vehicleButton){
		try{
			return new ButtonHelper(vehicleButton)
				.sendPressRelease(vehicleManager);
		}catch (UnrecognizedMeasurementTypeException e){
			return false;
		}
	}
	
	//Helper Method to eliminate need to create a bunch of objects
	public static boolean sendPress(VehicleManager vehicleManager, VehicleButtonEvent.ButtonId vehicleButton){
		try{
			return new ButtonHelper(vehicleButton)
				.sendPressRelease(vehicleManager);
		}catch (UnrecognizedMeasurementTypeException e){
			return false;
		}
	}
	
	
}
