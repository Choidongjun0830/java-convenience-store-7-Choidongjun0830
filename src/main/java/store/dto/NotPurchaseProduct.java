package store.dto;

public class NotPurchaseProduct {

    String name;
    int notPurchaseAmount;

    public NotPurchaseProduct(String name, int notPurchaseAmount) {
        this.name = name;
        this.notPurchaseAmount = notPurchaseAmount;
    }

    public String getName() {
        return name;
    }

    public int getNotPurchaseAmount() {
        return notPurchaseAmount;
    }
}
