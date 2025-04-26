package com.example.crypto;

public class CryptoItem {
    public String name;
    public String price;
    public String marketCap;
    public String imageUrl;

    public CryptoItem(String name, String price, String marketCap, String imageUrl) {
        this.name = name;
        this.price = price;
        this.marketCap = marketCap;
        this.imageUrl = imageUrl;
    }
}

