package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.Promotion;
import store.repository.PromotionRepository;

public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    private void parseProducts(String filePath) {
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
                promotionRepository.addPromotion(new Promotion(name, buyAmount, getAmount, start_date, end_date));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Promotion> getAllPromotions() {
        if(promotionRepository.getAllPromotions().isEmpty()) {
            parseProducts("src/main/resources/promotions.md");
        }
        return promotionRepository.getAllPromotions();
    }
}
