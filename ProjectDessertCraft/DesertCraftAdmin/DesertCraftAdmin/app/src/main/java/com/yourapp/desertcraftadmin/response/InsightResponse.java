package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.CatData;
import com.yourapp.desertcraftadmin.model.InsightData;

import java.util.List;

public class InsightResponse {
    private InsightData counts;
    private String message;
    private boolean success;

    public InsightResponse(InsightData counts, String message, boolean success) {
        this.counts = counts;
        this.message = message;
        this.success = success;
    }

    public InsightData getCounts() {
        return counts;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "InsightResponse{" +
                "counts=" + counts +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

