package com.yourapp.desertcraftadmin.response;

public class CallResponse {
    private String message;
    private boolean success;

    public CallResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "CallResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

