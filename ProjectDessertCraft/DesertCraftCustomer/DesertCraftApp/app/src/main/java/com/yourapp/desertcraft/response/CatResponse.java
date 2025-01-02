package com.yourapp.desertcraft.response;

import com.yourapp.desertcraft.model.CatData;

import java.util.List;

public class CatResponse {
    private List<CatData> data;
    private String message;
    private boolean success;

    public CatResponse(List<CatData> data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public List<CatData> getData() {
        return data;
    }

    public void setData(List<CatData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "CallResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

