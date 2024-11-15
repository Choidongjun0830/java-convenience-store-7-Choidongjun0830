package store.domain;

import store.enumerate.ProductType;

public class Product implements Cloneable{

    private String name;
    private int price;
    private int quantity;
    private String promotion;
    private ProductType productType;

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Product(String name, int price, int quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = isPromotionNull(promotion);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotion() {
        return promotion;
    }

    public boolean isPromotion() {
        return productType.equals(ProductType.PROMOTION);
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

    @Override
    public Product clone() {
        return new Product(this.name, this.quantity);
    }

    private String isPromotionNull(String promotion) {
        if (promotion.equals("null")) {
            this.productType = ProductType.REGULAR;
            return "";
        }
        this.productType = ProductType.PROMOTION;
        return promotion;
    }
}
