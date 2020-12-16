package com.angelbroking.smartapi;

import java.io.IOException;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.TokenSet;
import com.angelbroking.smartapi.models.Trade;
import com.angelbroking.smartapi.models.User;
import com.google.gson.Gson;

public class SmartConnect {
	public static SessionExpiryHook sessionExpiryHook = null;
	public static boolean ENABLE_LOGGING = false;
	private Proxy proxy = null;
	private String apiKey;
	private String accessToken;
	private String refreshToken;
	private Routes routes = new Routes();
	private String userId;
	private Gson gson;
	private SmartAPIRequestHandler smartAPIRequestHandler;

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Registers callback for session error.
	 * 
	 * @param hook can be set to get callback when session is expired.
	 */
	public void setSessionExpiryHook(SessionExpiryHook hook) {
		sessionExpiryHook = hook;
	}

	/**
	 * Returns apiKey of the App.
	 * 
	 * @return String apiKey is returned.
	 * @throws NullPointerException if _apiKey is not found.
	 */
	public String getApiKey() throws NullPointerException {
		if (apiKey != null)
			return apiKey;
		else
			throw new NullPointerException();
	}

	/**
	 * Returns accessToken.
	 * 
	 * @return String access_token is returned.
	 * @throws NullPointerException if accessToken is null.
	 */
	public String getAccessToken() throws NullPointerException {
		if (accessToken != null)
			return accessToken;
		else
			throw new NullPointerException();
	}

	/**
	 * Returns userId.
	 * 
	 * @return String userId is returned.
	 * @throws NullPointerException if userId is null.
	 */
	public String getUserId() throws NullPointerException {
		if (userId != null) {
			return userId;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Set userId.
	 * 
	 * @param id is user_id.
	 */
	public void setUserId(String id) {
		userId = id;
	}

	/**
	 * Returns publicToken.
	 * 
	 * @throws NullPointerException if publicToken is null.
	 * @return String public token is returned.
	 */
	public String getPublicToken() throws NullPointerException {
		if (refreshToken != null) {
			return refreshToken;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Set the accessToken received after a successful authentication.
	 * 
	 * @param accessToken is the access token received after sending request token
	 *                    and api secret.
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Set publicToken.
	 * 
	 * @param publicToken is the public token received after sending request token
	 *                    and api secret.
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Retrieves login url
	 * 
	 * @return String loginUrl is returned.
	 */
	public String getLoginURL() throws NullPointerException {
		String baseUrl = routes.getLoginUrl();
		return baseUrl;
	}

	/**
	 * Do the token exchange with the `request_token` obtained after the login flow,
	 * and retrieve the `access_token` required for all subsequent requests.
	 * 
	 * @param requestToken received from login process.
	 * @param apiSecret    which is unique for each aap.
	 * @return User is the user model which contains user and session details.
	 * @throws SmartAPIException
	 * @throws SmartAPIException is thrown for all SmartAPI trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public User generateSession(String clientCode, String password)
			throws SmartAPIException, JSONException, IOException {
		smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);

		// Create JSON params object needed to be sent to api.
		JSONObject params = new JSONObject();
		params.put("clientcode", clientCode);
		params.put("password", password);

		JSONObject loginResultObject = smartAPIRequestHandler.postRequest(this.apiKey, routes.getLoginUrl(), params);
		String jwtToken = loginResultObject.getJSONObject("data").getString("jwtToken");
		String refreshToken = loginResultObject.getJSONObject("data").getString("refreshToken");
		String url = routes.get("api.user.profile");
		User user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
		user.setAccessToken(jwtToken);
		user.setRefreshToken(refreshToken);

		return user;

	}

	/**
	 * Get a new access token using refresh token.
	 * 
	 * @param refreshToken is the refresh token obtained after generateSession.
	 * @param apiSecret    is unique for each app.
	 * @return TokenSet contains user id, refresh token, api secret.
	 * @throws IOException       is thrown when there is connection error.
	 * @throws SmartAPIException is thrown for all SmartAPI trade related errors.
	 */
	public TokenSet renewAccessToken(String accessToken, String refreshToken)
			throws IOException, SmartAPIException, JSONException {
		String hashableText = this.apiKey + refreshToken + accessToken;
		String sha256hex = sha256Hex(hashableText);

		JSONObject params = new JSONObject();
		params.put("refreshToken", refreshToken);
		params.put("checksum", sha256hex);
		String url = routes.get("api.refresh");
		JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

		accessToken = response.getJSONObject("data").getString("jwtToken");
		refreshToken = response.getJSONObject("data").getString("refreshToken");

		TokenSet tokenSet = new TokenSet();
		tokenSet.setUserId(userId);
		tokenSet.setAccessToken(accessToken);
		tokenSet.setRefreshToken(refreshToken);

		return tokenSet;
	}

	/**
	 * Hex encodes sha256 output for android support.
	 * 
	 * @return Hex encoded String.
	 * @param str is the String that has to be encrypted.
	 */
	public String sha256Hex(String str) {
		byte[] a = DigestUtils.sha256(str);
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	/**
	 * Get the profile details of the use.
	 * 
	 * @return Profile is a POJO which contains profile related data.
	 * @throws IOException       is thrown when there is connection error.
	 * @throws SmartAPIException is thrown for all SmartAPI trade related errors.
	 */
	public User getProfile() throws IOException, SmartAPIException, JSONException {
		String url = routes.get("api.user.profile");
		User user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken));
		return user;
	}

	/**
	 * Places an order.
	 * 
	 * @param orderParams is Order params.
	 * @param variety     variety="regular". Order variety can be bo, co, amo,
	 *                    regular.
	 * @return Order contains only orderId.
	 * @throws SmartAPIException is thrown for all SmartAPI trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public Order placeOrder(OrderParams orderParams, String variety)
			throws SmartAPIException, JSONException, IOException {
		String url = routes.get("api.order.place");

		JSONObject params = new JSONObject();

		if (orderParams.exchange != null)
			params.put("exchange", orderParams.exchange);
		if (orderParams.tradingsymbol != null)
			params.put("tradingsymbol", orderParams.tradingsymbol);
		if (orderParams.transactiontype != null)
			params.put("transactiontype", orderParams.transactiontype);
		if (orderParams.quantity != null)
			params.put("quantity", orderParams.quantity);
		if (orderParams.price != null)
			params.put("price", orderParams.price);
		if (orderParams.producttype != null)
			params.put("producttype", orderParams.producttype);
		if (orderParams.ordertype != null)
			params.put("ordertype", orderParams.ordertype);
		if (orderParams.duration != null)
			params.put("duration", orderParams.duration);
		if (orderParams.price != null)
			params.put("price", orderParams.price);
		if (orderParams.symboltoken != null)
			params.put("symboltoken", orderParams.symboltoken);
		if (orderParams.squareoff != null)
			params.put("squareoff", orderParams.squareoff);
		if (orderParams.stoploss != null)
			params.put("stoploss", orderParams.stoploss);

		params.put("variety", variety);

		JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
		Order order = new Order();
		order.orderId = jsonObject.getJSONObject("data").getString("orderid");
		return order;
	}

	/**
	 * Modifies an open order.
	 *
	 * @param orderParams is Order params.
	 * @param variety     variety="regular". Order variety can be bo, co, amo,
	 *                    regular.
	 * @param orderId     order id of the order being modified.
	 * @return Order object contains only orderId.
	 * @throws SmartAPIException is thrown for all SmartAPI trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public Order modifyOrder(String orderId, OrderParams orderParams, String variety)
			throws SmartAPIException, JSONException, IOException {
		String url = routes.get("api.order.modify");

		JSONObject params = new JSONObject();

		if (orderParams.exchange != null)
			params.put("exchange", orderParams.exchange);
		if (orderParams.tradingsymbol != null)
			params.put("tradingsymbol", orderParams.tradingsymbol);
		if (orderParams.symboltoken != null)
			params.put("symboltoken", orderParams.symboltoken);
		if (orderParams.quantity != null)
			params.put("quantity", orderParams.quantity);
		if (orderParams.price != null)
			params.put("price", orderParams.price);
		if (orderParams.producttype != null)
			params.put("producttype", orderParams.producttype);
		if (orderParams.ordertype != null)
			params.put("ordertype", orderParams.ordertype);
		if (orderParams.duration != null)
			params.put("duration", orderParams.duration);

		params.put("variety", variety);
		params.put("orderid", orderId);

		JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
		Order order = new Order();
		order.orderId = jsonObject.getJSONObject("data").getString("orderid");
		return order;
	}

	/**
	 * Cancels an order.
	 * 
	 * @param orderId order id of the order to be cancelled.
	 * @param variety [variety="regular"]. Order variety can be bo, co, amo,
	 *                regular.
	 * @return Order object contains only orderId.
	 * @throws SmartAPIException is thrown for all Angel trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public Order cancelOrder(String orderId, String variety) throws SmartAPIException, JSONException, IOException {
		String url = routes.get("api.order.cancel");
		JSONObject params = new JSONObject();
		params.put("variety", variety);
		params.put("orderid", orderId);

		JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
		Order order = new Order();
		order.orderId = jsonObject.getJSONObject("data").getString("orderid");
		return order;
	}

	/**
	 * Returns list of different stages an order has gone through.
	 * 
	 * @return List of multiple stages an order has gone through in the system.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @param orderId is the order id which is obtained from orderbook.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws IOException       is thrown when there is connection error.
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public List<Order> getOrderHistory(String clientId) throws SmartAPIException, IOException, JSONException {
		try {
			String url = routes.get("api.order.book");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			gson = new Gson();
			List<Order> orderList = Arrays.asList(gson.fromJson(String.valueOf(response.get("data")), Order[].class));
			for (Iterator iterator = orderList.iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
			}
			return orderList;
		} catch (Exception e) {
			System.out.println("Exception#: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves last price. User can either pass exchange with tradingsymbol or
	 * instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265,
	 * 265}.
	 * 
	 * @return Map of String and LTPQuote.
	 * @param instruments is the array of tradingsymbol and exchange or instruments
	 *                    token.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws IOException       is thrown when there is connection related error.
	 */
	public JSONObject getLTP(String exchange, String tradingSymbol, String symboltoken)
			throws SmartAPIException, IOException, JSONException {
		JSONObject params = new JSONObject();
		params.put("exchange", exchange);
		params.put("tradingsymbol", tradingSymbol);
		params.put("symboltoken", symboltoken);

		String url = routes.get("api.ltp.data");
		JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

		return response.getJSONObject("data");
	}

	/**
	 * Retrieves list of trades executed.
	 * 
	 * @return List of trades.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public List<Trade> getTrades() throws SmartAPIException, JSONException, IOException {
		String url = routes.get("api.order.trade.book");
		JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		return Arrays.asList(gson.fromJson(String.valueOf(response.get("data")), Trade[].class));
	}

	/**
	 * Retrieves RMS.
	 * 
	 * @return Object of RMS.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject getRMS() throws JSONException, IOException, SmartAPIException {
		String url = routes.get("api.order.rms.data");
		JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		return response.getJSONObject("data");
	}

	/**
	 * Retrieves Holding.
	 * 
	 * @return Object of Holding.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject getHolding() throws JSONException, IOException, SmartAPIException {
		String url = routes.get("api.order.rms.holding");
		JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		return response.getJSONObject("data");
	}

	/**
	 * Retrieves position.
	 * 
	 * @return Object of position.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject getPosition() throws JSONException, IOException, SmartAPIException {
		String url = routes.get("api.order.rms.position");
		JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		return response.getJSONObject("data");
	}

	/**
	 * Retrieves conversion.
	 * 
	 * @return Object of conversion.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject convertPosition(JSONObject params) throws SmartAPIException, IOException, JSONException {
		String url = routes.get("api.order.rms.position.convert");
		JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
		return response.getJSONObject("data");
	}

	/**
	 * Logs out user by invalidating the access token.
	 * 
	 * @return JSONObject which contains status
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws IOException       is thrown when there is connection related error.
	 */
	public JSONObject logout() throws SmartAPIException, IOException, JSONException {

		String url = routes.get("api.user.logout");
		JSONObject params = new JSONObject();
		params.put("clientcode", this.userId);
		JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
		return response;
	}

}
