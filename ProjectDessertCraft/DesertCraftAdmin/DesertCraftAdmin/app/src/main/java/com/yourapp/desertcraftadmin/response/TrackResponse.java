package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.OrderData;

public class TrackResponse {
    private OrderData data;
    private String message;
    private boolean success;

    public TrackResponse(OrderData data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public OrderData getData() {
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
        return "TrackResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

