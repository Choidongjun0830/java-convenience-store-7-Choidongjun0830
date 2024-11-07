package store.dto;

public class PromotionApplyResult {

    private int totalGetAmount;
    private int totalPromotedPrice;
    private int totalPromotedSalePrice;

    public PromotionApplyResult(int totalGetAmount, int totalPromotedPrice, int totalPromotedSalePrice) {
        this.totalGetAmount = totalGetAmount;
        this.totalPromotedPrice = totalPromotedPrice;
        this.totalPromotedSalePrice = totalPromotedSalePrice;
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

}
