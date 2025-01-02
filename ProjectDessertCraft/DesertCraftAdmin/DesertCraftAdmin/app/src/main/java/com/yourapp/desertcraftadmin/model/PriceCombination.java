package com.yourapp.desertcraftadmin.model;

import com.yourapp.desertcraftadmin.productlist.Price;

public class PriceCombination {
    private String flavour;
    private String size;
    private String weight;
    private String price;
    private String offerPrice;
    private String stock;

    public PriceCombination(String flavour, String size, String weight) {
        this.flavour = flavour;
        this.size = size;
        this.weight = weight;
    }

    public String getFlavour() { return flavour; }
    public String getSize() { return size; }
    public String getWeight() { return weight; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getOfferPrice() { return offerPrice; }
    public void setOfferPrice(String offerPrice) { this.offerPrice = offerPrice; }
    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }

    public boolean matches(Price price) {
        return this.flavour.equals(price.getFlavour()) &&
                this.size.equals(price.getSize()) &&
                this.weight.equals(price.getWeight());
    }



    @Override
    public String toString() {
        return flavour + ", " + size + ", " + weight;
    }

//    @Override
//    public String toString() {
//        return "PriceCombination{" +
//                "flavour='" + flavour + '\'' +
//                ", size='" + size + '\'' +
//                ", weight='" + weight + '\'' +
//                '}';
//    }
}
