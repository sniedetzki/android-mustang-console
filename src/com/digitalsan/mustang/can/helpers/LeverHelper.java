package com.digitalsan.mustang.can.helpers;

import com.openxc.VehicleManager;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleButtonEvent;

public class LeverHelper {
	
	ButtonHelper leverUp;
	ButtonHelper leverDown;
	
	public LeverHelper(VehicleButtonEvent.ButtonId leverUp,
			VehicleButtonEvent.ButtonId leverDown){
		
		this.leverUp = new ButtonHelper(leverUp);
		this.leverDown = new ButtonHelper(leverDown);
	}
	
	//Handle a lever up event, does press and release
	public boolean sendLeverUp(VehicleManager vehicleManager) 
			throws UnrecognizedMeasurementTypeException{
		return leverUp.sendPressRelease(vehicleManager);
	}
	
	//Handle a lever down event, does press and release
	public boolean sendLeverDown(VehicleManager vehicleManager) 
			throws UnrecognizedMeasurementTypeException{
		return leverDown.sendPressRelease(vehicleManager);
	}
}
