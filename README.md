# Smart API 1.0 Java client
The official Java client for communicating with [Smart API Connect API](https://smartapi.angelbroking.com).

Smart API is a set of REST-like APIs that expose many capabilities required to build a complete investment and trading platform. Execute orders in real time, manage user portfolio, stream live market data (WebSockets), and more, with the simple HTTP API collection.

[Angel Broking Technology Pvt Ltd](https://www.angelbroking.com/) (c) 2018. Licensed under the MIT License.

## Documentation
- [Smart API - HTTP API documentation] (https://smartapi.angelbroking.com/docs/connect/v1/)
- [Java library documentation](https://smartapi.angelbroking.com/docs/connect/v1/)

## Usage
- [Download SmartAPI jar file](https://github.com/angelbroking-github/smartapi-java/blob/main/dist) and include it in your build path.

- Include com.angelbroking.smartapi into build path from maven. Use version 1.0.0

## API usage
```java
// Initialize Samart API using clientcode and password.
SmartConnect smartConnect = new SmartConnect();
User user = smartConnect.generateSession("your_clientcode", "your_password");

// Set token.
smartConnect.setAccessToken(user.getAccessToken());

// Set userId.
smartConnect.setUserId(user.getUserId());


/* First you should get request_token, public_token using smartapi login and then use jwttoken smartapi call.
Get login url. Use this url in webview to login user, after authenticating user you will get requestToken. Use the same to get accessToken. */
String url = smartConnect.getLoginUrl();

// Get accessToken as follows,
User user = smartConnect.generateSession("your_clientcode", "your_password");

// Set request token and public token which are obtained from login process.
smartConnect.setAccessToken(user.getAccessToken());
smartConnect.setUserId(user.getUserId());

// Set session expiry callback.
smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
    @Override
    public void sessionExpired() {
        System.out.println("session expired");                    
    }
});

```
For more details, take a look at Examples.java in sample directory.

