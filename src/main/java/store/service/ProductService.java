package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import store.constant.ExceptionMessage;
import store.domain.Product;
import store.dto.TotalProductStock;
import store.repository.ProductRepository;
import store.view.InputView;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        if(productRepository.getAllProducts().isEmpty()) {
            parseProducts("src/main/resources/products.md");
        }
        return productRepository.getAllProducts();
    }

    public List<Product> parsePurchaseProductFromInput(String buyProductAmountsInput) {
        String[] buyProductAmounts = buyProductAmountsInput.split(",");

        for (int i = 0; i < buyProductAmounts.length; i++) {
            buyProductAmounts[i] = buyProductAmounts[i].replaceAll("[\\[\\]]", "").trim();
        }

        List<Product> buyProducts = new ArrayList<>();
        for (String buyProductAmount : buyProductAmounts) {
            String[] splitProductAmount = buyProductAmount.split("-");
            Product buyProduct = new Product(splitProductAmount[0].trim(), Integer.parseInt(splitProductAmount[1].trim()));
            buyProducts.add(buyProduct);
        }
        return buyProducts;
    }

    public Product getPromotionProductByName(String name) {
        productRepository.getPromotionProducts();
        return productRepository.getPromotionProductByName(name);
    }

    public Product getRegularProductByName(String name) {
        productRepository.getRegularProducts();
        return productRepository.getRegularProductByName(name);
    }

    public List<Product> cloneProductList(List<Product> purchaseProducts) {
        return purchaseProducts.stream()
                .map(Product::clone)
                .collect(Collectors.toList());
    }

    public int getTotalProductPrice(List<Product> purchaseProducts, List<Product> stockProducts) {
        int totalProductPrice = 0;
        for (Product buyProduct : purchaseProducts) {
            int price = getProductPrice(buyProduct.getName(), stockProducts) * buyProduct.getQuantity();
            totalProductPrice += price;
        }
        return totalProductPrice;
    }

    public int getProductPrice(String name, List<Product> stockProducts) {
        for(Product product : stockProducts) {
            if(product.getName().equals(name)) {
                return product.getPrice();
            }
        }
        return 0;
    }

    public void increaseTotalPurchaseAmount(String name, List<Product> purchaseProductsForReceipt, int increaseAmount) {
        for (Product product : purchaseProductsForReceipt) {
            if(product.getName().equals(name)) {
                product.increaseQuantity(increaseAmount);
            }
        }
    }

    public List<TotalProductStock> getTotalProductStocks(List<Product> buyProducts) {
        return buyProducts.stream()
                .map(buyProduct -> {
                    String name = buyProduct.getName();
                    int totalQuantity = Optional.ofNullable(getPromotionProductByName(name))
                            .map(Product::getQuantity)
                            .orElse(0)
                    + Optional.ofNullable(getRegularProductByName(name))
                            .map(Product::getQuantity)
                            .orElse(0);
                    return new TotalProductStock(name, totalQuantity);
                })
                .collect(Collectors.toList());
    }

    private void parseProducts(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            parseProduct(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void parseProduct(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            String name = fields[0];
            int price = Integer.parseInt(fields[1]);
            int quantity = Integer.parseInt(fields[2]);
            String promotion = fields.length > 3 ? fields[3] : null; // 수정하기
            productRepository.addProduct(new Product(name, price, quantity, promotion));
        }
    }
}
