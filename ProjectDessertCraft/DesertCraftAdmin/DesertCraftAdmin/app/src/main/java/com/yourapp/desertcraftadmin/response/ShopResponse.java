package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.ShopData;

public class ShopResponse {
    private ShopData data;
    private String message;
    private boolean success;

    public ShopResponse(ShopData data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public ShopData getData() {
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
        return "ShopResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

