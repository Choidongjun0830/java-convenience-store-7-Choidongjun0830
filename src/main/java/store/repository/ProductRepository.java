package store.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import store.domain.Product;

public class ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getPromotionProducts() {
        return products.stream()
                .filter(Product::isPromotion)
                .collect(Collectors.toList());
    }

    public List<Product> getRegularProducts() {
        return products.stream()
                .filter(product -> !product.isPromotion())
                .collect(Collectors.toList());
    }

    public Product getPromotionProductByName(String name) {
        return getPromotionProducts().stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Product getRegularProductByName(String name) {
        return getRegularProducts().stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void decreasePromotionProductQuantity(String name, int amount) {
        Product promotionProduct = getPromotionProductByName(name);
        if (promotionProduct != null) {
            promotionProduct.decreaseQuantity(amount);
        }
    }
}
