package com.angelbroking.smartapi.sample;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.Trade;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.ticker.OnConnect;
import com.angelbroking.smartapi.ticker.OnDisconnect;
import com.angelbroking.smartapi.ticker.OnError;
import com.angelbroking.smartapi.ticker.OnTicks;
import com.angelbroking.smartapi.ticker.SmartAPITicker;
import com.angelbroking.smartapi.utils.Constants;

@SuppressWarnings("unused")
public class Examples {

	public void getProfile(SmartConnect smartConnect) throws IOException, SmartAPIException {
		User profile = smartConnect.getProfile();
	}

	/** Place order. */
	public void placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {

		OrderParams orderParams = new OrderParams();
		orderParams.variety = "NORMAL";
		orderParams.quantity = 1;
		orderParams.symboltoken = "3045";
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "SBIN-EQ";
		orderParams.producttype = Constants.PRODUCT_INTRADAY;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
		orderParams.price = 122.2;
		orderParams.squareoff = "0";
		orderParams.stoploss = "0";

		Order order = smartConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
	}

	/** Modify order. */
	public void modifyOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Order modify request will return order model which will contain only

		OrderParams orderParams = new OrderParams();
		orderParams.quantity = 1;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "ASHOKLEY";
		orderParams.symboltoken = "3045";
		orderParams.producttype = Constants.PRODUCT_DELIVERY;
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.price = 122.2;

		String orderId = "201216000755110";
		Order order = smartConnect.modifyOrder(orderId, orderParams, Constants.VARIETY_REGULAR);
	}

	/** Cancel an order */
	public void cancelOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Order modify request will return order model which will contain only
		// order_id.
		// Cancel order will return order model which will only have orderId.
		Order order = smartConnect.cancelOrder("201009000000015", Constants.VARIETY_REGULAR);
	}

	/** Get order details */
	public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		List<Order> orders = smartConnect.getOrderHistory(smartConnect.getUserId());
		for (int i = 0; i < orders.size(); i++) {
			System.out.println(orders.get(i).orderId + " " + orders.get(i).status);
		}
	}

	/**
	 * Get last price for multiple instruments at once. USers can either pass
	 * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
	 * 50, BSE:SENSEX} or {256265, 265}
	 */
	public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {
		String exchange = "NSE";
		String tradingSymbol = "SBIN-EQ";
		String symboltoken = "3045";
		JSONObject ltpData = smartConnect.getLTP(exchange, tradingSymbol, symboltoken);
	}

	/** Get tradebook */
	public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns tradebook.
		List<Trade> trades = smartConnect.getTrades();
		for (int i = 0; i < trades.size(); i++) {
			System.out.println(trades.get(i).tradingSymbol + " " + trades.size());
		}
	}

	/** Get RMS */
	public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns RMS.
		JSONObject response = smartConnect.getRMS();
	}

	/** Get Holdings */
	public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Holding.
		JSONObject response = smartConnect.getHolding();
	}

	/** Get Position */
	public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Position.
		JSONObject response = smartConnect.getPosition();
	}

	/** convert Position */
	public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {

		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("oldproducttype", "DELIVERY");
		requestObejct.put("newproducttype", "MARGIN");
		requestObejct.put("tradingsymbol", "SBIN-EQ");
		requestObejct.put("transactiontype", "BUY");
		requestObejct.put("quantity", 1);
		requestObejct.put("type", "DAY");

		JSONObject response = smartConnect.getPosition();
	}

	public void tickerUsage(String clientId, String feedToken, String strWatchListScript, String task)
			throws SmartAPIException {

		SmartAPITicker tickerProvider = new SmartAPITicker(clientId, feedToken);

		tickerProvider.setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				System.out.println("onConnected");

				tickerProvider.subscribe(strWatchListScript, task);
			}
		});

		tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
			@Override
			public void onDisconnected() {
				System.out.println("onDisconnected");
			}
		});

		/** Set error listener to listen to errors. */
		tickerProvider.setOnErrorListener(new OnError() {
			@Override
			public void onError(Exception exception) {
				System.out.println("onError: " + exception.getMessage());
			}

			@Override
			public void onError(SmartAPIException smartAPIException) {
				System.out.println("onError: " + smartAPIException.getMessage());
			}

			@Override
			public void onError(String error) {
				System.out.println("onError: " + error);
			}
		});

		tickerProvider.setOnTickerArrivalListener(new OnTicks() {
			@Override
			public void onTicks(JSONArray ticks) {
				System.out.println("ticker data: " + ticks.toString());
			}
		});

		/**
		 * connects to Smart API ticker server for getting live quotes
		 */
		tickerProvider.connect();

		/**
		 * You can check, if websocket connection is open or not using the following
		 * method.
		 */
		boolean isConnected = tickerProvider.isConnectionOpen();
		System.out.println(isConnected);

		// After using SmartAPI ticker, close websocket connection.
		// tickerProvider.disconnect();

	}

	/** Logout user. */
	public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
		/** Logout user and kill session. */
		JSONObject jsonObject = smartConnect.logout();
	}

}
