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

    private String isPromotionNull(String promotion) {
        if (promotion.equals("null")) {
            this.productType = ProductType.REGULAR;
            return "";
        }
        this.productType = ProductType.PROMOTION;
        return promotion;
    }

    public void decreaseQuantity(int amount) {
        quantity -= amount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", promotion='" + promotion + '\'' +
                ", productType=" + productType +
                '}';
    }

    @Override
    public Product clone() {
        return new Product(this.name, this.quantity);
    }

    //"null"이 아니라 null일 경우 예외처리
}
