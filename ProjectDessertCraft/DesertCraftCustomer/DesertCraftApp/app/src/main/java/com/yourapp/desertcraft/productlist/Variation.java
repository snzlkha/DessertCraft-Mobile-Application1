package com.yourapp.desertcraft.productlist;

public class Variation {
    private String variation;
    private String option;

    public Variation(String variation, String option) {
        this.variation = variation;
        this.option = option;
    }

    public String getVariation() { return variation; }
    public void setVariation(String variation) { this.variation = variation; }

    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }
}

