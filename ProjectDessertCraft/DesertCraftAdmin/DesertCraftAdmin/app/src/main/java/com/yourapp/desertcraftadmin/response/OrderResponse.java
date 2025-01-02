package com.yourapp.desertcraftadmin.response;


import com.yourapp.desertcraftadmin.model.OrderData;

import java.util.List;

public class OrderResponse {
    private List<OrderData> data;
    private String message;
    private boolean success;

    public OrderResponse(List<OrderData> data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public List<OrderData> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

