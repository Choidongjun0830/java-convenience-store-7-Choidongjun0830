package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        while (true) {
            List<Product> productList = productService.getAllProducts();
            List<Promotion> promotionList = promotionService.getAllPromotions();
            outputView.printWelcomeAndStockList(productList);

            List<Product> buyProducts = productService.getBuyProductAmount();
            List<Product> buyProductsCopy = buyProducts.stream()
                    .map(Product::clone)
                    .collect(Collectors.toList());
            Map<String, Promotion> activePromotions = findActivePromotionsForBuyProducts(buyProducts);

            //프로모션 적용 가능 여부 확인 후 적용
            Map<Product, PromotionApplyResult> productPromotionApplyResults = applyPromotions(buyProducts, activePromotions);
            int totalProductPrice = getTotalProductPrice(buyProductsCopy, productList);
            System.out.println("totalProductPrice = " + totalProductPrice);

            int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults);
            System.out.println("totalPromotedPrice = " + totalPromotedPrice);

            //멤버십 할인 여부 확인 - 프로모션 미적용되는 상품 금액의 30% 할인 - 최대 한도는 8000원
            String checkMembership = inputView.checkMembership();
            int membershipSaleAmount = applyMembership(totalProductPrice - totalPromotedPrice, checkMembership);
            System.out.println("membershipSaleAmount = " + membershipSaleAmount);

            outputView.printReceipt(productList, buyProductsCopy, productPromotionApplyResults, totalProductPrice, membershipSaleAmount);

            String checkAddtionalPurchase = inputView.checkAddtionalPurchase();
            if (checkAddtionalPurchase.equals("N")) break;
        }

    }

    private static int getTotalPromotedPrice(Map<Product, PromotionApplyResult> productPromotionApplyResults) {
        int totalPromotedPrice = 0;
        for (PromotionApplyResult promotionApplyResult : productPromotionApplyResults.values()) {
            int promotedPrice = promotionApplyResult.getTotalPromotedPrice();
            totalPromotedPrice += promotedPrice;
        }
        return totalPromotedPrice;
    }

    private int applyMembership(int nonPromotedPrice, String checkMembership) {
        if(checkMembership.equals("Y")) {
            int saleAmount = nonPromotedPrice * 30 / 100;
            if(saleAmount >= 8000) {
                return -8000;
            }
            return -saleAmount;
        }
        return 0;
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
                int totalPromotedSalePrice = 0;

                while(buyProduct.getQuantity() >= promotionBuyAmount && promotionProduct.getQuantity() >= promotionTotalAmount){
                    totalGetAmount += promotionGetAmount;
                    totalPromotedPrice += promotionTotalAmount * promotionProduct.getPrice();
                    totalPromotedSalePrice += promotionGetAmount * promotionProduct.getPrice();
                    buyProduct.decreaseQuantity(promotionTotalAmount);
                    promotionProduct.decreaseQuantity(promotionTotalAmount);
                }
                System.out.println(totalPromotedSalePrice);
                applyResult.put(buyProduct, new PromotionApplyResult(totalGetAmount, totalPromotedPrice, totalPromotedSalePrice));
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
        LocalDateTime now = DateTimes.now();
        Promotion promotionByName = promotionService.getPromotionByName(promotionName);
        if (promotionByName != null) {
            LocalDate startDate = promotionByName.getStartDate();
            LocalDate endDate = promotionByName.getEndDate();
            return promotionByName;
        }
        return null;
    }

    private int getTotalProductPrice(List<Product> buyProducts, List<Product> productList) {
        int totalProductPrice = 0;
        for (Product buyProduct : buyProducts) {
            int price = getProductPrice(buyProduct.getName(), productList) * buyProduct.getQuantity();
            totalProductPrice += price;
        }
        return totalProductPrice;
    }

    private int getProductPrice(String name, List<Product> productList) {
        for(Product product : productList) {
            if(product.getName().equals(name)) { //프로모션 상품과 금액 다를 경우도 고려.
                return product.getPrice();
            }
        }
        return 0;
    }
}
