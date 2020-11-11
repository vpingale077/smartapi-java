package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * A wrapper for order.
 */
public class Order {

	@SerializedName("exchange_order_id")
	public String exchangeOrderId;

	@SerializedName("disclosedquantity")
	public String disclosedQuantity;

	@SerializedName("duration")
	public String validity;

	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	@SerializedName("variety")
	public String orderVariety;

	@SerializedName("user_id")
	public String userId;

	@SerializedName("ordertype")
	public String orderType;

	@SerializedName("triggerprice")
	public String triggerPrice;

	@SerializedName("text")
	public String statusMessage;

	@SerializedName("price")
	public String price;

	@SerializedName("status")
	public String status;

	@SerializedName("producttype")
	public String product;

	@SerializedName("placed_by")
	public String accountId;

	@SerializedName("exchange")
	public String exchange;

	@SerializedName("orderid")
	public String orderId;

	@SerializedName("symbol")
	public String symbol;

	@SerializedName("pending_quantity")
	public String pendingQuantity;

	@SerializedName("updatetime")
	public String orderTimestamp;

	@SerializedName("exchtime")
	public String exchangeTimestamp;

	@SerializedName("exchorderupdatetime")
	public String exchangeUpdateTimestamp;

	@SerializedName("average_price")
	public String averagePrice;

	@SerializedName("transactiontype")
	public String transactionType;

	@SerializedName("filled_quantity")
	public String filledQuantity;

	@SerializedName("quantity")
	public String quantity;

	@SerializedName("parent_order_id")
	public String parentOrderId;

	@SerializedName("tag")
	public String tag;

	@SerializedName("guid")
	public String guid;

	@Override
	public String toString() {
		return "Order [exchangeOrderId=" + exchangeOrderId + ", disclosedQuantity=" + disclosedQuantity + ", validity="
				+ validity + ", tradingSymbol=" + tradingSymbol + ", orderVariety=" + orderVariety + ", userId="
				+ userId + ", orderType=" + orderType + ", triggerPrice=" + triggerPrice + ", statusMessage="
				+ statusMessage + ", price=" + price + ", status=" + status + ", product=" + product + ", accountId="
				+ accountId + ", exchange=" + exchange + ", orderId=" + orderId + ", symbol=" + symbol
				+ ", pendingQuantity=" + pendingQuantity + ", orderTimestamp=" + orderTimestamp + ", exchangeTimestamp="
				+ exchangeTimestamp + ", exchangeUpdateTimestamp=" + exchangeUpdateTimestamp + ", averagePrice="
				+ averagePrice + ", transactionType=" + transactionType + ", filledQuantity=" + filledQuantity
				+ ", quantity=" + quantity + ", parentOrderId=" + parentOrderId + ", tag=" + tag + ", guid=" + guid
				+ "]";
	}

}
