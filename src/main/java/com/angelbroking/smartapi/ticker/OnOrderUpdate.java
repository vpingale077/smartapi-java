package com.angelbroking.smartapi.ticker;

import com.angelbroking.smartapi.models.Order;

public interface OnOrderUpdate {
	void onOrderUpdate(Order order);
}
