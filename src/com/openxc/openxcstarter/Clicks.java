package com.openxc.openxcstarter;

import com.openxc.units.Quantity;

public class Clicks extends Quantity<Number>{
	
	public Clicks(Number value) {
		super(value);
	}

	private final String TYPE_STRING = "clicks";
	
    @Override
    public String getTypeString() {
        return TYPE_STRING;
    }
	
}