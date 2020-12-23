package com.angelbroking.smartapi.ticker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

public class Ticker {

	private static final String SERVER = "wss://omnefeeds.angelbroking.com/NestHtml5Mobile/socket/stream";
	private static final int TIMEOUT = 5000;

	public void connect(String clientId, String feedToken, String script) throws Exception {

		System.out.println("in connect1");

		// Connect to the echo server.
		WebSocket ws = connect();

		System.out.println("in connect2");

		JSONObject wsJSONRequest = new JSONObject();
		wsJSONRequest.put("task", "cn");
		wsJSONRequest.put("channel", "");
		wsJSONRequest.put("token", feedToken);
		wsJSONRequest.put("user", clientId);
		wsJSONRequest.put("acctid", clientId);

		ws.sendText(wsJSONRequest.toString());

		System.out.println("in connect3");

		JSONObject wsJSONRequest_2 = new JSONObject();
		wsJSONRequest_2.put("task", "cn");
		wsJSONRequest_2.put("channel", script);
		wsJSONRequest_2.put("token", feedToken);
		wsJSONRequest_2.put("user", clientId);
		wsJSONRequest_2.put("acctid", clientId);

		ws.sendText(wsJSONRequest_2.toString());

		System.out.println("in connect4");

		// Close the web socket.
		ws.disconnect();

		System.out.println("disconnect");

	}

	/**
	 * Connect to the server.
	 */
	private static WebSocket connect() throws Exception {
		return new WebSocketFactory().setVerifyHostname(false).setConnectionTimeout(TIMEOUT).createSocket(SERVER)
				.addListener(new WebSocketAdapter() {
					// A text message arrived from the server.
					public void onTextMessage(WebSocket websocket, String message) {
						System.out.println(message);
					}
				}).addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).connect();
	}

	/**
	 * Wrap the standard input with BufferedReader.
	 */
	private static BufferedReader getInput() throws IOException {
		return new BufferedReader(new InputStreamReader(System.in));
	}

}
