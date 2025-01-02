package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.CatData;
import com.yourapp.desertcraftadmin.model.CatData;

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

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "DCallResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

