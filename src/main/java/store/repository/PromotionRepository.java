package store.repository;

import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;

public class PromotionRepository {

    private final List<Promotion> promotions = new ArrayList<>();

    public void addPromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    public List<Promotion> getAllPromotions() {
        return promotions;
    }


    public Promotion getPromotionByName(String name) {
        return promotions.stream()
                .filter(promotion -> promotion.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
