package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;
import store.dto.PromotionInfo;
import store.repository.PromotionRepository;
import store.validator.InputValidator;
import store.view.InputView;

public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductService productService;
    private final InputView inputView;
    private final InputValidator inputValidator;

    public PromotionService(PromotionRepository promotionRepository, ProductService productService, InputView inputView, InputValidator inputValidator) {
        this.promotionRepository = promotionRepository;
        this.productService = productService;
        this.inputView = inputView;
        this.inputValidator = inputValidator;
    }

    private void parseProducts(String filePath) {
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

    public boolean applyExtraForPromo(Product purchaseProduct, List<Product> buyProductClone, PromotionInfo promotionInfo) {
        if(purchaseProduct.getQuantity() == promotionInfo.getPromotionBuyAmount() && purchaseProduct.getQuantity() < promotionInfo.getPromotionTotalAmount()) {;
            String response = getResponseForExtraProduct(purchaseProduct, promotionInfo.getPromotionGetAmount());
            if(response.equalsIgnoreCase("Y")) {
                purchaseProduct.increaseQuantity(promotionInfo.getPromotionGetAmount());
                productService.increaseTotalPurchaseAmount(purchaseProduct.getName(), buyProductClone, promotionInfo.getPromotionGetAmount());
                return true;
            }
            return false;
        }
        return true;
    }

    private String getResponseForExtraProduct(Product buyProduct, int promotionGetAmount) {
        while(true) {
            try {
                String response = inputView.checkAdditionalQuantity(buyProduct.getName(), promotionGetAmount);
                inputValidator.validateYesOrNoType(response);
                return response;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void checkPurchaseWithoutPromotion(Product buyProduct) {
        if(buyProduct.getQuantity() > 0) {
            String checkPurchaseWithoutPromotion = inputView.checkPurchaseWithoutPromotion(buyProduct.getName(), buyProduct.getQuantity());
            if(checkPurchaseWithoutPromotion.equalsIgnoreCase("N")) {
                buyProduct.decreaseQuantity(buyProduct.getQuantity());
            }
        }
    }
}
