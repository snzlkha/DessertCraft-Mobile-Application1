package com.yourapp.desertcraft.response;

import com.yourapp.desertcraft.model.ProfileCountData;
import com.yourapp.desertcraft.model.UserData;

public class ProfileResponse {
    private UserData data;
    private ProfileCountData count;
    private String message;
    private boolean success;

    public ProfileResponse(UserData data, ProfileCountData count, String message, boolean success) {
        this.data = data;
        this.count = count;
        this.message = message;
        this.success = success;
    }

    public UserData getData() {
        return data;
    }

    public ProfileCountData getCount() {
        return count;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "ProfileResponse{" +
                "data=" + data +
                ", count=" + count +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

