package com.yourapp.desertcraftadmin.product;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourapp.desertcraftadmin.productlist.Price;
import com.yourapp.desertcraftadmin.productlist.Variation;

import java.lang.reflect.Type;
import java.util.List;

public class ProductSingle {
    private String pid;
    private String cid;
    private String p_name;
    private String description;
    private String variation;
    private String price;
    private String p_image;
    private String base_price;
    private String offer_price;
    private String created_on;
    private String updated_on;

    // Parsed fields
    private List<Variation> parsedVariation;
    private List<Price> parsedPrice;

    // Getters and setters
    public List<Variation> getParsedVariation() {
        if (parsedVariation == null && variation != null) {
            Gson gson = new Gson();
            Type variationListType = new TypeToken<List<Variation>>() {
            }.getType();
            parsedVariation = gson.fromJson(variation, variationListType);
        }
        return parsedVariation;
    }

    public void setParsedVariation(List<Variation> parsedVariation) {
        this.parsedVariation = parsedVariation;
    }

    public List<Price> getParsedPrice() {
        if (parsedPrice == null && price != null) {
            Gson gson = new Gson();
            Type priceListType = new TypeToken<List<Price>>() {
            }.getType();
            parsedPrice = gson.fromJson(price, priceListType);
        }
        return parsedPrice;
    }

    public void setParsedPrice(List<Price> parsedPrice) {
        this.parsedPrice = parsedPrice;
    }

    // Other getters and setters...


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

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getP_image() {
        return p_image;
    }

    public void setP_image(String p_image) {
        this.p_image = p_image;
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

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}

