package com.yourapp.desertcraft.response;

import com.yourapp.desertcraft.model.UserData;

public class LoginResponse {
    private UserData data;
    private String message;
    private boolean success;

    public LoginResponse(UserData data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public UserData getData() {
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
        return "LoginResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

