package com.openxc.openxcstarter;

import com.openxc.measurements.BaseMeasurement;
import com.openxc.util.Range;

public class TemperaturKnob extends BaseMeasurement<Clicks> {
	
    private final static Range<Clicks> RANGE =
            new Range<Clicks>( new Clicks(0), new Clicks(30) );
    
    public final static String ID = "temperature_knob";
	
	
    public TemperaturKnob(Number value) {
        super(new Clicks(value), RANGE);
    }
    
    
	public TemperaturKnob(Clicks value) {
		super(value);
	}
	
	
    @Override
    public String getGenericName() {
        return ID;
    }
}
