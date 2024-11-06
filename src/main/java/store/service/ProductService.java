package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    public Map<String, Integer> getBuyProductAmount() {
        String buyProductAmountsInput = inputView.getBuyProductAmount();
        String[] buyProductAmounts = buyProductAmountsInput.split(",");

        for (int i = 0; i < buyProductAmounts.length; i++) {
            buyProductAmounts[i] = buyProductAmounts[i].replaceAll("[\\[\\]]", "").trim();
        }

        Map<String, Integer> buyProductsAmount = new HashMap<>();
        for (String buyProductAmount : buyProductAmounts) {
            String[] splitProductAmount = buyProductAmount.split("-");
            buyProductsAmount.put(splitProductAmount[0].trim(), Integer.parseInt(splitProductAmount[1].trim()));
        }
        return buyProductsAmount;
        //유효한 입력인지 -가 맞은지
        //상품이 재고에 있는지
        //수량이 유효한지
    }

    public Product getPromotionProductByName(String name) {
        if(productRepository.getPromotionProducts().isEmpty()) {
            productRepository.getPromotionProducts();
        }
        return productRepository.getPromotionProductByName(name);
    }

}
