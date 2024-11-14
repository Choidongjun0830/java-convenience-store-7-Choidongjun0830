package store.dto;

public class ReceiptInfo {

    private final int totalProductPrice;
    private final int totalPromotedPrice;
    private final int membershipSaleAmount;

    public ReceiptInfo(int totalProductPrice, int totalPromotedPrice, int membershipSaleAmount) {
        this.totalProductPrice = totalProductPrice;
        this.totalPromotedPrice = totalPromotedPrice;
        this.membershipSaleAmount = membershipSaleAmount;
    }

    public int getTotalProductPrice() {
        return totalProductPrice;
    }

    public int getTotalPromotedPrice() {
        return totalPromotedPrice;
    }

    public int getMembershipSaleAmount() {
        return membershipSaleAmount;
    }
}
