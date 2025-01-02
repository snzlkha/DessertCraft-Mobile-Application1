package com.yourapp.desertcraftadmin.productlist;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message;
    private List<Product> data;


    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Product> getData() { return data; }
    public void setData(List<Product> data) { this.data = data; }
}
