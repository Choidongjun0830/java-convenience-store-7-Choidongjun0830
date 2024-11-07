package store.dto;

public class PromotionApplyResult {

    private int totalGetAmount;
    private int totalPromotedPrice;
    private int totalPromotedSalePrice;
    private NotPurchaseProduct notPurchaseProduct;

    public PromotionApplyResult(int totalGetAmount, int totalPromotedPrice, int totalPromotedSalePrice, NotPurchaseProduct notPurchaseProduct) {
        this.totalGetAmount = totalGetAmount;
        this.totalPromotedPrice = totalPromotedPrice;
        this.totalPromotedSalePrice = totalPromotedSalePrice;
        this.notPurchaseProduct = notPurchaseProduct;
    }

    public int getTotalGetAmount() {
        return totalGetAmount;
    }

    public int getTotalPromotedPrice() {
        return totalPromotedPrice;
    }

    public int getTotalPromotedSalePrice() {
        return totalPromotedSalePrice;
    }

    public NotPurchaseProduct getNotPurchaseProduct() {
        return notPurchaseProduct;
    }
}
