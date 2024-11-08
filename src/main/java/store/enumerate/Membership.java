package store.enumerate;

public enum Membership {

    MEMBERSHIP(0.3F),
    NON_MEMBERSHIP(0);

    float discountRate;

    Membership(float discountRate) {
        this.discountRate = discountRate;
    }

    public float getDiscountRate() {
        return discountRate;
    }
}
