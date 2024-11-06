package store.dto;

public class PromotionApplyResult {

    private int totalGetAmount;
    private int totalPromotedPrice;

    public PromotionApplyResult(int totalGetAmount, int totalPromotedPrice) {
        this.totalGetAmount = totalGetAmount;
        this.totalPromotedPrice = totalPromotedPrice;
    }

    public int getTotalGetAmount() {
        return totalGetAmount;
    }

    public int getTotalPromotedPrice() {
        return totalPromotedPrice;
    }

    @Override
    public String toString() {
        return "PromotionApplyResult{" +
                "totalGetAmount=" + totalGetAmount +
                ", totalPromotedPrice=" + totalPromotedPrice +
                '}';
    }
}
