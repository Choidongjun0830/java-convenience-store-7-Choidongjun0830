package store.dto;

import store.domain.Product;

public class PromotionApplyResult {

    private Product product;
    private int totalGetAmount;
    private int totalPromotedPrice;
    private int totalPromotedSalePrice;

    public PromotionApplyResult(Product product, int totalGetAmount, int totalPromotedPrice, int totalPromotedSalePrice) {
        this.product = product;
        this.totalGetAmount = totalGetAmount;
        this.totalPromotedPrice = totalPromotedPrice;
        this.totalPromotedSalePrice = totalPromotedSalePrice;
    }

    public Product getProduct() { return product; }

    public int getTotalGetAmount() {
        return totalGetAmount;
    }

    public int getTotalPromotedPrice() {
        return totalPromotedPrice;
    }

    public int getTotalPromotedSalePrice() {
        return totalPromotedSalePrice;
    }
}
