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
	private static String _rootUrl = "https://openapisuat.angelbroking.com";
	private static String _loginUrl = "https://openapisuat.angelbroking.com/login-service/rest/auth/angelbroking/user/v1/loginByPassword";

	// Initialize all routes,
	@SuppressWarnings("serial")
	public Routes() {
		routes = new HashMap<String, String>() {
			{
				put("api.token", "/login-service/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.profile", "/login-service/rest/secure/angelbroking/user/v1/getProfile");
				put("api.refresh", "/login-service/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.logout", "/login-service/rest/secure/angelbroking/user/v1/logout");
				put("api.order.place", "/order-service/rest/secure/angelbroking/order/v1/placeOrder");
				put("api.order.modify", "/order-service/rest/secure/angelbroking/order/v1/modifyOrder");
				put("api.order.cancel", "/order-service/rest/secure/angelbroking/order/v1/cancelOrder");
				put("api.order.book", "/order-service/rest/secure/angelbroking/order/v1/getOrderBook");
				put("api.order.trade.book", "/order-service/rest/secure/angelbroking/order/v1/getTradeBook");
				put("api.order.rms.data", "/order-service/rest/secure/angelbroking/rms/v1/getRMS");
				put("api.order.rms.holding", "/order-service/rest/secure/angelbroking/rms/v1/getHolding");
				put("api.order.rms.position", "/order-service/rest/secure/angelbroking/rms/v1/getPosition");
				put("api.order.rms.position.convert", "/order-service/rest/secure/angelbroking/rms/v1/convertPosition");
				put("api.ltp.data", "/order-service/rest/secure/angelbroking/order/v1/getLtpData");
			}
		};
	}

	public String get(String key) {
		return _rootUrl + routes.get(key);
	}

	public String getLoginUrl() {
		return _loginUrl;
	}
}
