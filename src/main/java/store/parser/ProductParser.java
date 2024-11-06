package store.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.domain.Product;

public class ProductParser {

    public static List<Product> parseProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int price = Integer.parseInt(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                String promotion = fields.length > 3 ? fields[3] : null; // 수정하기
                products.add(new Product(name, price, quantity, promotion));

            }
            return products;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
