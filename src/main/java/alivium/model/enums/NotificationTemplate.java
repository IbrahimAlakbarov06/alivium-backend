package alivium.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum NotificationTemplate {
    ORDER_PLACED(
            "Order Confirmed ✓",
            "Your order #{orderId} has been placed successfully. Total: ${amount}. We'll notify you once it's confirmed.",
            NotificationType.ORDER_PLACED
    ),

    ORDER_CONFIRMED(
            "Order Processing",
            "Your order #{orderId} has been confirmed and is being prepared for shipment.",
            NotificationType.ORDER_CONFIRMED
    ),

    ORDER_SHIPPED(
            "Order Shipped 🚚",
            "Your order #{orderId} is on the way! Track it: {trackingNumber}",
            NotificationType.ORDER_SHIPPED
    ),

    ORDER_DELIVERED(
            "Order Delivered 🎉",
            "Your order #{orderId} has been delivered. Thank you for shopping with us!",
            NotificationType.ORDER_DELIVERED
    ),

    ORDER_CANCELLED(
            "Order Cancelled",
            "Order #{orderId} has been cancelled. Reason: {reason}. Refund will be processed within 3-5 days.",
            NotificationType.ORDER_CANCELLED
    ),

    NEW_VOUCHER(
            "New Discount Code! 🎁",
            "You've got {discount}% off! Use code {voucherCode} at checkout. Valid until {expiryDate}.",
            NotificationType.VOUCHER
    ),

    PRODUCT_PRICE_DROP(
            "Price Drop! 💰",
            "{productName} is now ${price} - save {discount}%! Limited time offer.",
            NotificationType.PRICE_DROP
    ),

    NEW_ARRIVAL(
            "New Arrival! 🆕",
            "Check out {productName}! Starting at ${price}. Shop the new collection now!",
            NotificationType.NEW_ARRIVAL
    ),
    WISHLIST_ITEM_ON_SALE(
            "Wishlist Item on Sale! ❤️",
            "{productName} from your wishlist is {discount}% off - just ${price}!",
            NotificationType.WISHLIST_ITEM
    ),

    WISHLIST_ITEM_BACK_IN_STOCK(
            "Wishlist Alert! 🔥",
            "{productName} is back in stock! Price: ${price}. Limited quantity available!",
            NotificationType.WISHLIST_ITEM
    ),

    FLASH_SALE(
            "Flash Sale! ⚡",
            "{discount}% off on {category}! Only {hours} hours left. Shop now!",
            NotificationType.PROMOTION
    ),

    SEASONAL_SALE(
            "{season} Sale! 🎉",
            "{season} sale is live! {discount}% off on {category}. Valid until {endDate}.",
            NotificationType.PROMOTION
    ),

    FREE_SHIPPING(
            "Free Shipping! 🚚",
            "Free shipping on orders over ${minAmount}! Offer ends {endDate}.",
            NotificationType.PROMOTION
    ),
    WELCOME_NEW_USER(
            "Welcome! 👋",
            "Thanks for joining! Here's {discount}% off your first order. Code: {voucherCode}",
            NotificationType.SYSTEM
    ),

    CART_REMINDER(
            "Cart Reminder 🛒",
            "You have {itemCount} items in your cart. Complete your order and save {discount}%!",
            NotificationType.SYSTEM
    );

    private final String title;
    private final String messageTemplate;
    private final NotificationType type;

    public String formatMessage(Map<String, String> params) {
        String message = this.messageTemplate;

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return message;
    }

    public String formatTitle(Map<String, String> params) {
        String formattedTitle = this.title;

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formattedTitle = formattedTitle.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return formattedTitle;
    }
}
