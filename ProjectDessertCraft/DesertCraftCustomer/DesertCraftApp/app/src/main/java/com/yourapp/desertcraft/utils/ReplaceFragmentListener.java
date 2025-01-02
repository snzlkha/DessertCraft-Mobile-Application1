package com.yourapp.desertcraft.utils;

public interface ReplaceFragmentListener {
    void onOrderNowClick();

    void onTrackNowClick();

    void openCartFragment();

    void onCartItemClick(int itemId, int points);

    void openTrackingFragment(int orderId);

    void goBackToHome();

    void onEditItemClick(int itemId, String did);
}
