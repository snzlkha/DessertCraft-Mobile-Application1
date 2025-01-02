package com.yourapp.desertcraft.response;

import com.yourapp.desertcraft.model.RewardData;

public class RewardResponse {
    private RewardData data;
    private boolean success;

    public RewardResponse(RewardData data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public RewardData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "RewardResponse{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}

