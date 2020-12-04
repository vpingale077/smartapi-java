package com.angelbroking.smartapi.ticker;

import java.util.ArrayList;

import com.angelbroking.smartapi.models.Tick;

public interface OnTicks {
	void onTicks(ArrayList<Tick> ticks);
}
