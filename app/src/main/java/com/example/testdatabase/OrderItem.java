package com.example.testdatabase;

public class OrderItem {
    private String product;
    private String sweetness;
    private String size;
    private String amount;

    public OrderItem(String product, String sweetness, String size, String amount) {
        this.product = product;
        this.sweetness = sweetness;
        this.size = size;
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public String getSweetness() {
        return sweetness;
    }

    public String getSize() {
        return size;
    }

    public String getAmount() {
        return amount;
    }
}
