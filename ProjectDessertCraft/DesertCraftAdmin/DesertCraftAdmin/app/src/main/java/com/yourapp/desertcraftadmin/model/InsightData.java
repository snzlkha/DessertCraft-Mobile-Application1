package com.yourapp.desertcraftadmin.model;

public class InsightData {
    String total_users;
    String total_orders;
    String total_sales;

    public InsightData(String total_users, String total_orders, String total_sales) {
        this.total_users = total_users;
        this.total_orders = total_orders;
        this.total_sales = total_sales;
    }

    public String getTotal_users() {
        return total_users;
    }

    public String getTotal_orders() {
        return total_orders;
    }

    public String getTotal_sales() {
        return total_sales;
    }

    @Override
    public String toString() {
        return "InsightData{" +
                "total_users='" + total_users + '\'' +
                ", total_orders='" + total_orders + '\'' +
                ", total_sales='" + total_sales + '\'' +
                '}';
    }
}
