package store.repository;

import java.util.ArrayList;
import java.util.List;
import store.domain.Product;

public class ProductRepository {

    private final List<Product> products = new ArrayList<>();
    List<Product> promotionProducts = new ArrayList<>();
    List<Product> regularProducts = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getPromotionProducts() {
        for (Product product : products) {
            if (product.isPromotion()) {
                promotionProducts.add(product);
            }
        }
        return promotionProducts;
    }

    public List<Product> getRegularProducts() {
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

    public Product getPromotionProductByName(String name) {
        return promotionProducts.stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Product getRegularProductByName(String name) {
        return regularProducts.stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void decreaseProductQuantity(Product product) {

    }
}
