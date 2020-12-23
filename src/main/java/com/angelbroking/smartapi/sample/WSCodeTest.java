package com.angelbroking.smartapi.sample;

import org.json.JSONArray;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.ticker.OnConnect;
import com.angelbroking.smartapi.ticker.OnDisconnect;
import com.angelbroking.smartapi.ticker.OnError;
import com.angelbroking.smartapi.ticker.OnTicks;
import com.angelbroking.smartapi.ticker.SmartAPITicker;

public class WSCodeTest {

	public static void main(String[] args) {
		try {
			SmartConnect smartConnect = new SmartConnect();

			// PROVIDE YOUR API KEY HERE
			smartConnect.setApiKey("smartapi_key");

			// Set session expiry callback.
			smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
				@Override
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});

			User user = smartConnect.generateSession("S212741", "pass@123");
			System.out.println(user.toString());
			smartConnect.setAccessToken(user.getAccessToken());
			smartConnect.setUserId(user.getUserId());

			String clientId = "S212741";
			String feedToken = user.getFeedToken();
			String strwatchlistscrips = "nse_cm|2885&nse_cm|1594&nse_cm|11536";

			SmartAPITicker tickerProvider = new SmartAPITicker(clientId, feedToken);

			tickerProvider.setOnConnectedListener(new OnConnect() {
				@Override
				public void onConnected() {
					System.out.println("onConnected");
					tickerProvider.subscribe(strwatchlistscrips);

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
			// Make sure this is called before calling connect.
			tickerProvider.setTryReconnection(true);
			// maximum retries and should be greater than 0
			tickerProvider.setMaximumRetries(10);
			// set maximum retry interval in seconds
			tickerProvider.setMaximumRetryInterval(30);

			/**
			 * connects to Smart API ticker server for getting live
			 * quotes
			 */
			tickerProvider.connect();

			/**
			 * You can check, if websocket connection is open or not using the following
			 * method.
			 */
			boolean isConnected = tickerProvider.isConnectionOpen();
			System.out.println(isConnected);

			// After using SmartAPI ticker, close websocket connection.
			//tickerProvider.disconnect();

		} catch (

		Exception e) {
			// TODO: handle exception
		} catch (SmartAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
