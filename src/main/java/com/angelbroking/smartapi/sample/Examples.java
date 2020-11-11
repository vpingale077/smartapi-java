package com.angelbroking.smartapi.sample;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.Trade;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.utils.Constants;

public class Examples {

	public void getProfile(SmartConnect smartConnect) throws IOException, SmartAPIException {
		User profile = smartConnect.getProfile();
		System.out.println("User Profile: " + profile.getUserName());
	}

	/** Place order. */
	public void placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		/**
		 * Place order method requires a orderParams argument which contains,
		 * tradingsymbol, exchange, transaction_type, order_type, quantity, product,
		 * price, trigger_price, disclosed_quantity, validity squareoff_value,
		 * stoploss_value, trailing_stoploss and variety (value can be regular, bo, co,
		 * amo) place order will return order model which will have only orderId in the
		 * order model
		 *
		 * Following is an example param for LIMIT order, if a call fails then
		 * SmartAPIException will have error message in it Success of this call implies
		 * only order has been placed successfully, not order execution.
		 */

		OrderParams orderParams = new OrderParams();
		orderParams.quantity = 1;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "ASHOKLEY";
		orderParams.producttype = Constants.PRODUCT_DELIVERY;
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.price = 122.2;
		// orderParams.tag = "myTag"; //tag is optional and it cannot be more than 8
		// characters and only alphanumeric is allowed

		Order order = smartConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
		System.out.println("orderId: " + order.orderId);
	}

	/** Modify order. */
	public void modifyOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Order modify request will return order model which will contain only
		// order_id.
		OrderParams orderParams = new OrderParams();
		orderParams.quantity = 1;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "ASHOKLEY";
		orderParams.producttype = Constants.PRODUCT_DELIVERY;
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.price = 122.2;

		Order order = smartConnect.modifyOrder("201009000000015", orderParams, Constants.VARIETY_REGULAR);
		System.out.println("orderId: " + order.orderId);
	}

	/** Cancel an order */
	public void cancelOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Order modify request will return order model which will contain only
		// order_id.
		// Cancel order will return order model which will only have orderId.
		Order order = smartConnect.cancelOrder("201009000000015", Constants.VARIETY_REGULAR);
		System.out.println("orderId: " + order.orderId);
	}

	/** Get order details */
	public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		List<Order> orders = smartConnect.getOrderHistory(smartConnect.getUserId());
		for (int i = 0; i < orders.size(); i++) {
			System.out.println(orders.get(i).orderId + " " + orders.get(i).status);
		}
		System.out.println("list size is: " + orders.size());
	}

	/**
	 * Get last price for multiple instruments at once. USers can either pass
	 * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
	 * 50, BSE:SENSEX} or {256265, 265}
	 */
	public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {
		String exchange = "BSE";
		String tradingSymbol = "SBIN-EQ";
		JSONObject ltpData = smartConnect.getLTP(exchange, tradingSymbol);
		System.out.println(ltpData.toString());
	}

	/** Get tradebook */
	public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns tradebook.
		List<Trade> trades = smartConnect.getTrades();
		for (int i = 0; i < trades.size(); i++) {
			System.out.println(trades.get(i).tradingSymbol + " " + trades.size());
		}
		System.out.println("Trade Size: " + trades.size());
	}

	/** Get RMS */
	public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns RMS.
		JSONObject response = smartConnect.getRMS();
		System.out.println(response);
	}

	/** Get Holdings */
	public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Holding.
		JSONObject response = smartConnect.getHolding();
		System.out.println(response);
	}

	/** Get Position */
	public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Position.
		JSONObject response = smartConnect.getPosition();
		System.out.println(response);
	}

	/** convert Position */
	public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("symboltoken", "2885");
		requestObejct.put("producttype", "DELIVERY");
		requestObejct.put("newproducttype", "INTRADAY");
		requestObejct.put("tradingsymbol", "RELIANCE-EQ");
		requestObejct.put("symbolname", "RELIANCE");
		requestObejct.put("instrumenttype", "");
		requestObejct.put("priceden", "1");
		requestObejct.put("pricenum", "1");
		requestObejct.put("genden", "1");
		requestObejct.put("gennum", "1");
		requestObejct.put("precision", "2");
		requestObejct.put("multiplier", "-1");
		requestObejct.put("boardlotsize", "1");
		requestObejct.put("buyqty", "1");
		requestObejct.put("sellqty", "0");
		requestObejct.put("buyamount", "223580");
		requestObejct.put("sellamount", "0");
		requestObejct.put("transactiontype", "BUY");
		requestObejct.put("quantity", 1);
		requestObejct.put("type", "DAY");
		JSONObject response = smartConnect.getPosition();
		System.out.println(response);
	}

	/** Logout user. */
	public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
		/** Logout user and kill session. */
		JSONObject jsonObject = smartConnect.logout();
		System.out.println(jsonObject);
	}

}
