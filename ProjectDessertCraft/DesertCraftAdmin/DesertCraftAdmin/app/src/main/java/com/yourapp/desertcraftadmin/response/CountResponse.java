package com.yourapp.desertcraftadmin.response;

import com.yourapp.desertcraftadmin.model.CountData;

public class CountResponse {
    boolean success;
    CountData counts;

    public CountResponse(boolean success, CountData counts) {
        this.success = success;
        this.counts = counts;
    }

    public boolean isSuccess() {
        return success;
    }

    public CountData getCounts() {
        return counts;
    }

    @Override
    public String toString() {
        return "CountResponse{" +
                "success=" + success +
                ", counts=" + counts +
                '}';
    }
}
