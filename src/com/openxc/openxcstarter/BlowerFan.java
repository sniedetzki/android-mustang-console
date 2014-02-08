package com.openxc.openxcstarter;

import com.openxc.measurements.BaseMeasurement;
import com.openxc.util.Range;

public class BlowerFan extends BaseMeasurement<Clicks>{
	public final static String ID = "blower_motor_fan_speed";
	
	
    private final static Range<Clicks> RANGE =
            new Range<Clicks>( new Clicks(0), new Clicks(6) );
	
    public BlowerFan(Number value) {
        super(new Clicks(value), RANGE);
    }
	
	public BlowerFan(Clicks value) {
		super(value);
	}

	
    @Override
    public String getGenericName() {
        return ID;
    }
	

}
