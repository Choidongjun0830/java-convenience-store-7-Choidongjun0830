package store.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;

public class PromotionParser {

    public static List<Promotion> parseProducts(String filePath) {
        List<Promotion> promotion = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int buyAmount = Integer.parseInt(fields[1]);
                int getAmount = Integer.parseInt(fields[2]);
                LocalDate start_date = LocalDate.parse(fields[3]);
                LocalDate end_date = LocalDate.parse(fields[4]);
                promotion.add(new Promotion(name, buyAmount, getAmount, start_date, end_date));

            }
            return promotion;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
