package store.repository;

import java.util.ArrayList;
import java.util.List;
import store.domain.Product;

public class ProductRepository {

    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getPromotionProducts() {
        List<Product> promotionProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.isPromotion()) {
                promotionProducts.add(product);
            }
        }
        return promotionProducts;
    }

    public List<Product> getRegularProducts() {
        List<Product> regularProducts = new ArrayList<>();
        for (Product product : products) {
            if (!product.isPromotion()) {
                regularProducts.add(product);
            }
        }
        return regularProducts;
    }

    public List<Product> getAllProducts() {
        return products;
    }
}
