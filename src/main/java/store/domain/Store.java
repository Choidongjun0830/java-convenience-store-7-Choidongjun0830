package store.domain;

public class Store {

    Product product;
    Promotion promotion;

    public Store(Product product, Promotion promotion) {
        this.product = product;
        this.promotion = promotion;
    }

    public Product getProduct() {
        return product;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
