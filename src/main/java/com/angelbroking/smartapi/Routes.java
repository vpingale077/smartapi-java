package com.angelbroking.smartapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates end-points for all smart api calls.
 *
 * Here all the routes are translated into a Java Map.
 *
 */

public class Routes {

	public Map<String, String> routes;
	private static String _rootUrl = "https://apiconnect.angelbroking.com";
	private static String _loginUrl = "https://apiconnect.angelbroking.com/rest/auth/angelbroking/user/v1/loginByPassword";
	private static String _wsuri = "wss://omnefeeds.angelbroking.com/NestHtml5Mobile/socket/stream";

	// Initialize all routes,
	@SuppressWarnings("serial")
	public Routes() {
		routes = new HashMap<String, String>() {
			{
				put("api.token", "/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.profile", "/rest/secure/angelbroking/user/v1/getProfile");
				put("api.refresh", "/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.logout", "/rest/secure/angelbroking/user/v1/logout");
				put("api.order.place", "/rest/secure/angelbroking/order/v1/placeOrder");
				put("api.order.modify", "/rest/secure/angelbroking/order/v1/modifyOrder");
				put("api.order.cancel", "/rest/secure/angelbroking/order/v1/cancelOrder");
				put("api.order.book", "/rest/secure/angelbroking/order/v1/getOrderBook");
				put("api.order.trade.book", "/rest/secure/angelbroking/order/v1/getTradeBook");
				put("api.order.rms.data", "/rest/secure/angelbroking/user/v1/getRMS");
				put("api.order.rms.holding", "/rest/secure/angelbroking/portfolio/v1/getHolding");
				put("api.order.rms.position", "/rest/secure/angelbroking/order/v1/getPosition");
				put("api.order.rms.position.convert", "/rest/secure/angelbroking/order/v1/convertPosition");
				put("api.ltp.data", "/rest/secure/angelbroking/order/v1/getLtpData");
			}
		};
	}

	public String get(String key) {
		return _rootUrl + routes.get(key);
	}

	public String getLoginUrl() {
		return _loginUrl;
	}

	public String getWsuri() {
		return _wsuri;
	}
}
