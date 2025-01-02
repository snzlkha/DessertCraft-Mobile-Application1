package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.CatData;
import com.yourapp.desertcraftadmin.model.RateData;

import java.util.List;

public class RateResponse {
    private List<RateData> data;
    private String message;
    private boolean success;

    public RateResponse(List<RateData> data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public List<RateData> getData() {
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
        return "RateResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

