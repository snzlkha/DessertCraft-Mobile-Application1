package com.yourapp.desertcraft.model;

public class ProfileCountData {
    String total_orders;

    public ProfileCountData(String total_orders) {
        this.total_orders = total_orders;
    }

    public String getTotal_orders() {
        return total_orders;
    }

    @Override
    public String toString() {
        return "ProfileCountData{" +
                "total_orders='" + total_orders + '\'' +
                '}';
    }
}
