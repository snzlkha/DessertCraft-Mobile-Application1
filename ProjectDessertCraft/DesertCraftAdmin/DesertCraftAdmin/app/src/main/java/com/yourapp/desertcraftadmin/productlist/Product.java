package com.yourapp.desertcraftadmin.productlist;

import java.util.List;

public class Product {
    private String pid;
    private String cid;
    private String p_name;
    private String description;
    private String variation;  // This will hold the JSON string for variations
    private String price;  // This will hold the JSON string for prices
    private List<Variation> parsedVariation;
    private List<Price> parsedPrice;
    private String p_image;
    private String created_on;
    private String updated_on;
    private String total_sold;
    private String base_price;
    private String offer_price;

    // Getters and setters
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPName() {
        return p_name;
    }

    public void setPName(String p_name) {
        this.p_name = p_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public List<Variation> getParsedVariation() {
        return parsedVariation;
    }

    public void setParsedVariation(List<Variation> parsedVariation) {
        this.parsedVariation = parsedVariation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<Price> getParsedPrice() {
        return parsedPrice;
    }

    public void setParsedPrice(List<Price> parsedPrice) {
        this.parsedPrice = parsedPrice;
    }

    public String getPImage() {
        return p_image;
    }

    public void setPImage(String p_image) {
        this.p_image = p_image;
    }

    public String getCreatedOn() {
        return created_on;
    }

    public void setCreatedOn(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdatedOn() {
        return updated_on;
    }

    public void setUpdatedOn(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getTotalSold() {
        return total_sold;
    }

    public void setTotalSold(String total_sold) {
        this.total_sold = total_sold;
    }

    public String getBase_price() {
        return base_price;
    }

    public void setBase_price(String base_price) {
        this.base_price = base_price;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }
}
