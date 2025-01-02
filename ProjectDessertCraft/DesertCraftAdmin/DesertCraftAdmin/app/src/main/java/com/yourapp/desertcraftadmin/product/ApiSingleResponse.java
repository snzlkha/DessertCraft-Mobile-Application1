package com.yourapp.desertcraftadmin.product;

public class ApiSingleResponse {
    private boolean success;
    private String message;
    private ProductSingle data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProductSingle getData() {
        return data;
    }

    public void setData(ProductSingle data) {
        this.data = data;
    }
}
