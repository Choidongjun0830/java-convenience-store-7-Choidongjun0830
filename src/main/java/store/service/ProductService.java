package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import store.domain.Product;
import store.repository.ProductRepository;
import store.view.InputView;

public class ProductService {

    private final ProductRepository productRepository;
    private final InputView inputView;

    public ProductService(ProductRepository productRepository, InputView inputView) {
        this.productRepository = productRepository;
        this.inputView = inputView;
    }

    private void parseProducts(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int price = Integer.parseInt(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                String promotion = fields.length > 3 ? fields[3] : null; // 수정하기
                productRepository.addProduct(new Product(name, price, quantity, promotion));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
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
        //유효한 입력인지 -가 맞은지
        //상품이 재고에 있는지
        //수량이 유효한지
    }

    public Product getPromotionProductByName(String name) {
        productRepository.getPromotionProducts();
        return productRepository.getPromotionProductByName(name);
    }

    public Product getRegularProductByName(String name) {
        productRepository.getRegularProducts();
        return productRepository.getRegularProductByName(name);
    }

    public List<Product> cloneProductList(List<Product> buyProducts) {
        return buyProducts.stream()
                .map(Product::clone)
                .collect(Collectors.toList());
    }

    public int getTotalProductPrice(List<Product> buyProducts, List<Product> productList) {
        int totalProductPrice = 0;
        for (Product buyProduct : buyProducts) {
            int price = getProductPrice(buyProduct.getName(), productList) * buyProduct.getQuantity();
            totalProductPrice += price;
        }
        return totalProductPrice;
    }

    public int getProductPrice(String name, List<Product> productList) {
        for(Product product : productList) {
            if(product.getName().equals(name)) { //프로모션 상품과 금액 다를 경우도 고려.
                return product.getPrice();
            }
        }
        return 0;
    }
}
