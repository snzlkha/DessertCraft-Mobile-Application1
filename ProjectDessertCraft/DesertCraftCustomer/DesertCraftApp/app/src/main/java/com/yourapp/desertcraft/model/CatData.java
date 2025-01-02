package com.yourapp.desertcraft.model;

public class CatData {
    String cid;
    String c_name;

    public CatData(String cid, String c_name) {
        this.cid = cid;
        this.c_name = c_name;
    }

    public String getCid() {
        return cid;
    }

    public String getC_name() {
        return c_name;
    }

    @Override
    public String toString() {
        return "CatData{" +
                "cid='" + cid + '\'' +
                ", c_name='" + c_name + '\'' +
                '}';
    }
}

