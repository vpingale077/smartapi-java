package com.angelbroking.smartapi.ticker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterOutputStream;

import javax.net.ssl.SSLContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.utils.NaiveSSLContext;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

public class SmartAPITicker {

	private Routes routes = new Routes();
	private final String wsuri = routes.getWsuri();;
	private OnTicks onTickerArrivalListener;
	private OnConnect onConnectedListener;
	private OnDisconnect onDisconnectedListener;
	private OnError onErrorListener;
	private WebSocket ws;

	private long lastPongAt = 0;
	private int maxRetries = 10;
	private int count = 0;
	private Timer timer = null;
	private boolean tryReconnection = false;
	private final int pingInterval = 2500;
	private final int pongCheckInterval = 2500;
	private int nextReconnectInterval = 0;
	private int maxRetryInterval = 30000;
	private Timer canReconnectTimer = null;
	/** Used to reconnect after the specified delay. */
	private boolean canReconnect = true;
	private String clientId;
	private String feedToken;

	/**
	 * Initialize SmartAPITicker.
	 */
	public SmartAPITicker(String clientId, String feedToken) {

		this.clientId = clientId;
		this.feedToken = feedToken;

		try {

			SSLContext context = NaiveSSLContext.getInstance("TLS");
			ws = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(wsuri);

		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			return;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		ws.addListener(getWebsocketAdapter());
	}

	/**
	 * Returns task which performs check every second for reconnection.
	 * 
	 * @return TimerTask returns timer task which will be invoked after user defined
	 *         interval and tries reconnect.
	 */
	private TimerTask getTask() {
		TimerTask checkForRestartTask = new TimerTask() {
			@Override
			public void run() {
				if (lastPongAt == 0)
					return;

				Date currentDate = new Date();
				long timeInterval = (currentDate.getTime() - lastPongAt);
				if (timeInterval >= 2 * pingInterval) {
					doReconnect();
				}
			}
		};
		return checkForRestartTask;
	}

	/**
	 * Performs reconnection after a particular interval if count is less than
	 * maximum retries.
	 */
	public void doReconnect() {
		if (!tryReconnection)
			return;

		if (nextReconnectInterval == 0) {
			nextReconnectInterval = (int) (2000 * Math.pow(2, count));
		} else {
			nextReconnectInterval = (int) (nextReconnectInterval * Math.pow(2, count));
		}

		if (nextReconnectInterval > maxRetryInterval) {
			nextReconnectInterval = maxRetryInterval;
		}
		if (count <= maxRetries) {
			if (canReconnect) {
				count++;
				reconnect();
				canReconnect = false;
				canReconnectTimer = new Timer();
				canReconnectTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						canReconnect = true;
					}
				}, nextReconnectInterval);
			}
		} else if (count > maxRetries) {
			// if number of tries exceeds maximum number of retries then stop timer.
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
	}

	/**
	 * Set tryReconnection, to instruct SmartAPITicker that it has to reconnect, if
	 * com.angelbroking.smartapi.ticker is disconnected.
	 * 
	 * @param retry will denote whether reconnection should be tried or not.
	 */
	public void setTryReconnection(boolean retry) {
		tryReconnection = retry;
	}

	/**
	 * Set error listener.
	 * 
	 * @param listener of type OnError which listens to all the type of errors that
	 *                 may arise in SmartAPITicker class.
	 */
	public void setOnErrorListener(OnError listener) {
		onErrorListener = listener;
	}

	/**
	 * Set max number of retries for reconnection, for infinite retries set value as
	 * -1.
	 * 
	 * @param maxRetries denotes maximum number of retries that the
	 *                   com.angelbroking.smartapi.ticker can perform.
	 * @throws SmartAPIException when maximum retries is less than 0.
	 */
	public void setMaximumRetries(int maxRetries) throws SmartAPIException {
		if (maxRetries > 0) {
			this.maxRetries = maxRetries;
		} else {
			throw new SmartAPIException("Maximum retries can't be less than 0");
		}
	}

	/* Set a maximum interval for every retry. */
	public void setMaximumRetryInterval(int interval) throws SmartAPIException {
		if (interval >= 5) {
			// convert to milliseconds
			maxRetryInterval = interval * 1000;
		} else {
			throw new SmartAPIException("Maximum retry interval can't be less than 0");
		}
	}

	/**
	 * Set listener for listening to ticks.
	 * 
	 * @param onTickerArrivalListener is listener which listens for each tick.
	 */
	public void setOnTickerArrivalListener(OnTicks onTickerArrivalListener) {
		this.onTickerArrivalListener = onTickerArrivalListener;
	}

	/**
	 * Set listener for on connection established.
	 * 
	 * @param listener is used to listen to onConnected event.
	 */
	public void setOnConnectedListener(OnConnect listener) {
		onConnectedListener = listener;
	}

	/**
	 * Set listener for on connection is disconnected.
	 * 
	 * @param listener is used to listen to onDisconnected event.
	 */
	public void setOnDisconnectedListener(OnDisconnect listener) {
		onDisconnectedListener = listener;
	}

	/**
	 * Establishes a web socket connection.
	 */
	public void connect() {
		try {
			ws.setPingInterval(pingInterval);
			ws.connect();
		} catch (WebSocketException e) {
			e.printStackTrace();
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			if (tryReconnection) {
				if (timer == null) {
					// this is to handle reconnection first time
					if (lastPongAt == 0) {
						lastPongAt = 1;
					}
					timer = new Timer();
					timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);
				}
			}
		}
	}

	/** Returns a WebSocketAdapter to listen to ticker related events. */
	public WebSocketAdapter getWebsocketAdapter() {
		return new WebSocketAdapter() {

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
				count = 0;
				nextReconnectInterval = 0;

				if (onConnectedListener != null) {
					onConnectedListener.onConnected();
				}

				if (tryReconnection) {
					if (timer != null) {
						timer.cancel();
					}
					timer = new Timer();
					timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);

				}
			}

			@Override
			public void onTextMessage(WebSocket websocket, String message) throws IOException, DataFormatException {
				byte[] decoded = Base64.getDecoder().decode(message);
				byte[] result = decompress(decoded);
				String str = new String(result, StandardCharsets.UTF_8);

				JSONArray tickerData = new JSONArray(str);

				if (onTickerArrivalListener != null) {
					onTickerArrivalListener.onTicks(tickerData);
				}
			}

			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] binary) {
				try {
					super.onBinaryMessage(websocket, binary);
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

			@Override
			public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
				try {
					super.onPongFrame(websocket, frame);
					Date date = new Date();
					lastPongAt = date.getTime();
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

			/**
			 * On disconnection, return statement ensures that the thread ends.
			 *
			 * @param websocket
			 * @param serverCloseFrame
			 * @param clientCloseFrame
			 * @param closedByServer
			 * @throws Exception
			 */
			@Override
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
					WebSocketFrame clientCloseFrame, boolean closedByServer) {
				if (onDisconnectedListener != null) {
					onDisconnectedListener.onDisconnected();
				}
				return;
			}

			@Override
			public void onError(WebSocket websocket, WebSocketException cause) {
				try {
					super.onError(websocket, cause);
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

		};
	}

	/** Disconnects websocket connection. */
	public void disconnect() {
		if (timer != null) {
			timer.cancel();
		}
		if (ws != null && ws.isOpen()) {
			ws.disconnect();
		}
	}

	/** Disconnects websocket connection only for internal use */
	private void nonUserDisconnect() {
		if (ws != null) {
			ws.disconnect();
		}
	}

	/**
	 * Returns true if websocket connection is open.
	 * 
	 * @return boolean
	 */
	public boolean isConnectionOpen() {
		if (ws != null) {
			if (ws.isOpen()) {
				return true;
			}
		}
		return false;
	}

	/** Disconnects and reconnects */
	private void reconnect() {
		nonUserDisconnect();
		try {
			ws = new WebSocketFactory().createSocket(wsuri);
		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			return;
		}
		ws.addListener(getWebsocketAdapter());
		connect();
		final OnConnect onUsersConnectedListener = this.onConnectedListener;
		setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				lastPongAt = 0;
				count = 0;
				nextReconnectInterval = 0;
				onConnectedListener = onUsersConnectedListener;
			}
		});
	}

	/**
	 * Subscribes script.
	 */
	public void subscribe(String script) {
		if (ws != null) {
			if (ws.isOpen()) {

				// Send a text frame.
				JSONObject wsCNJSONRequest = new JSONObject();
				wsCNJSONRequest.put("task", "cn");
				wsCNJSONRequest.put("channel", "");
				wsCNJSONRequest.put("token", this.feedToken);
				wsCNJSONRequest.put("user", this.clientId);
				wsCNJSONRequest.put("acctid", this.clientId);

				ws.sendText(wsCNJSONRequest.toString());

				JSONObject wsMWJSONRequest = new JSONObject();
				wsMWJSONRequest.put("task", "mw");
				wsMWJSONRequest.put("channel", script);
				wsMWJSONRequest.put("token", this.feedToken);
				wsMWJSONRequest.put("user", this.clientId);
				wsMWJSONRequest.put("acctid", this.clientId);

				ws.sendText(wsMWJSONRequest.toString());

			} else {
				if (onErrorListener != null) {
					onErrorListener.onError(new SmartAPIException("ticker is not connected", "504"));
				}
			}
		} else {
			if (onErrorListener != null) {
				onErrorListener.onError(new SmartAPIException("ticker is null not connected", "504"));
			}
		}
	}

	public static byte[] decompress(byte[] compressedTxt) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (OutputStream ios = new InflaterOutputStream(os)) {
			ios.write(compressedTxt);
		}

		return os.toByteArray();
	}

}
