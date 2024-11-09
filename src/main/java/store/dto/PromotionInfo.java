package store.dto;

public class PromotionInfo {

    int promotionBuyAmount;
    int promotionGetAmount;
    int promotionTotalAmount;

    public PromotionInfo(int promotionBuyAmount, int promotionGetAmount) {
        this.promotionBuyAmount = promotionBuyAmount;
        this.promotionGetAmount = promotionGetAmount;
        this.promotionTotalAmount = promotionGetAmount + promotionBuyAmount;
    }

    public int getPromotionBuyAmount() {
        return promotionBuyAmount;
    }

    public int getPromotionGetAmount() {
        return promotionGetAmount;
    }

    public int getPromotionTotalAmount() {
        return promotionTotalAmount;
    }

}
