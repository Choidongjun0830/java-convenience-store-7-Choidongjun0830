package store.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import store.domain.Product;
import store.domain.Promotion;
import store.dto.PromotionApplyResult;
import store.service.ProductService;
import store.service.PromotionService;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;
    private final ProductService productService;
    private final PromotionService promotionService;

    public StoreController(InputView inputView, OutputView outputView, ProductService productService, PromotionService promotionService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public void startProcess() {
        List<Product> productList = productService.getAllProducts();
        List<Promotion> promotionList = promotionService.getAllPromotions();
        outputView.printWelcomeAndStockList(productList);

        List<Product> buyProducts = productService.getBuyProductAmount();
        List<Product> buyProductsCopy = buyProducts.stream()
                .map(Product::clone)
                .collect(Collectors.toList());
        Map<String, Promotion> activePromotions = findActivePromotionsForBuyProducts(buyProducts);

        Map<Product, PromotionApplyResult> productPromotionApplyResults = applyPromotions(buyProducts, activePromotions);

        outputView.printReceipt(productList, buyProductsCopy, productPromotionApplyResults);
    }

    private Map<Product, PromotionApplyResult> applyPromotions(List<Product> buyProducts, Map<String, Promotion> activePromotions) {
        Map<Product, PromotionApplyResult> applyResult = new HashMap<>();
        for (Product buyProduct : buyProducts) {
            if(activePromotions.containsKey(buyProduct.getName())) {
                Promotion promotion = activePromotions.get(buyProduct.getName());
                int promotionBuyAmount = promotion.getBuyAmount();
                int promotionGetAmount = promotion.getGetAmount();
                int promotionTotalAmount = promotionBuyAmount + promotionGetAmount;

                Product promotionProduct = productService.getPromotionProductByName(buyProduct.getName());
                int totalGetAmount = 0;
                int totalPromotedPrice = 0;

                while(buyProduct.getQuantity() >= promotionBuyAmount && promotionProduct.getQuantity() >= promotionTotalAmount){
                    totalGetAmount += promotionGetAmount;
                    totalPromotedPrice += promotionBuyAmount * promotionProduct.getPrice();
                    buyProduct.decreaseQuantity(promotionTotalAmount);
                    promotionProduct.decreaseQuantity(promotionTotalAmount);
                }
                applyResult.put(buyProduct, new PromotionApplyResult(totalGetAmount, totalPromotedPrice));
            }
        }
        return applyResult;
    }

    private Map<String ,Promotion> findActivePromotionsForBuyProducts(List<Product> buyProducts) {
        Map<String ,Promotion> activePromotions = new HashMap<>();

        for (Product buyProduct : buyProducts) {
            String name = buyProduct.getName();
            Product promotionProduct = productService.getPromotionProductByName(name);

            if(promotionProduct != null) {
                String promotion = promotionProduct.getPromotion();
                Promotion activePromotion = getActivePromotionByName(promotion);

                if(activePromotion != null) {
                    activePromotions.put(name, activePromotion);
                }
            }
        }
        return activePromotions;
    }


    private Promotion getActivePromotionByName(String promotionName) {
        LocalDate now = LocalDate.now();
        Promotion promotionByName = promotionService.getPromotionByName(promotionName);
        if (promotionByName != null) {
            LocalDate startDate = promotionByName.getStart_date();
            LocalDate endDate = promotionByName.getEnd_date();
            return promotionByName;
        }
        return null;
    }
}
