package com.yourapp.desertcraft.response;

import com.yourapp.desertcraft.model.BannerData;

public class BannerResponse {
    private BannerData data;
    private boolean success;

    public BannerResponse(BannerData data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public BannerData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "BannerResponse{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}

