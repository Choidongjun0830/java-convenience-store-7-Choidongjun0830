package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;
import store.repository.PromotionRepository;
import store.view.InputView;

public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductService productService;
    private final InputView inputView;

    public PromotionService(PromotionRepository promotionRepository, ProductService productService, InputView inputView) {
        this.promotionRepository = promotionRepository;
        this.productService = productService;
        this.inputView = inputView;
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

    public void getAllPromotions() {
        if(promotionRepository.getAllPromotions().isEmpty()) {
            parseProducts("src/main/resources/promotions.md");
        }
    }

    public Promotion getPromotionByName(String name) {
        return promotionRepository.getPromotionByName(name);
    }

    public Map<String, Promotion> findActivePromotions(List<Product> buyProducts) {
        Map<String, Promotion> activePromotions = new HashMap<>();
        for (Product buyProduct : buyProducts) {
            String name = buyProduct.getName();
            Product promotionProduct = productService.getPromotionProductByName(name);

            if (promotionProduct != null) {
                String promotion = promotionProduct.getPromotion();
                Promotion activePromotion = getActivePromotion(promotion);
                if (activePromotion != null) { //지워도 될듯
                    activePromotions.put(name, activePromotion);
                }
            }
        }
        return activePromotions;
    }

    private Promotion getActivePromotion(String promotionName) {
        Promotion promotion = getPromotionByName(promotionName);
        if (promotion != null && isPromotionActive(promotion)) {
            return promotion;
        }
        return null;
    }

    private boolean isPromotionActive(Promotion promotion) {
        LocalDate now = LocalDate.from(DateTimes.now());
        return !now.isBefore(promotion.getStartDate()) && !now.isAfter(promotion.getEndDate());
    }

    public boolean applyExtraForPromo(Product buyProduct, List<Product> buyProductClone, int promotionBuyAmount,
                                       int promotionTotalAmount, int promotionGetAmount) {
        if(buyProduct.getQuantity() == promotionBuyAmount && buyProduct.getQuantity() < promotionTotalAmount) {;
            String response = inputView.checkAdditionalQuantity(buyProduct.getName(), promotionGetAmount);
            if(response.equalsIgnoreCase("Y")) {
                buyProduct.increaseQuantity(promotionGetAmount);
                productService.increaseTotalPurchaseAmount(buyProduct.getName(), buyProductClone, promotionGetAmount);
            }
            if(buyProduct.getQuantity() < promotionGetAmount) {
                return true;
            }
        }
        return false;
    }

    public int checkPurchaseWithoutPromotion(Product buyProduct) {
        if(buyProduct.getQuantity() > 0) {
            String checkPurchaseWithoutPromotion = inputView.checkPurchaseWithoutPromotion(buyProduct.getName(), buyProduct.getQuantity());
            if(checkPurchaseWithoutPromotion.equals("N")) {
                int result = buyProduct.getQuantity();
                buyProduct.decreaseQuantity(buyProduct.getQuantity());
                return result;
            }
        }
        return 0;
    }
}
