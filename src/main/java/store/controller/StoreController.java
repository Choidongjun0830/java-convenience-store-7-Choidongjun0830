package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.domain.Product;
import store.domain.Promotion;
import store.dto.NotPurchaseProduct;
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

    public StoreController(InputView inputView, OutputView outputView, ProductService productService,
                           PromotionService promotionService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public void startProcess() {
        while (true) {
            List<Product> productList = displayStockList();
            List<Promotion> promotionList = promotionService.getAllPromotions();

            List<Product> buyProducts = productService.getBuyProductAmount();
            List<Product> buyProductsCopy = cloneProductList(buyProducts);
            Map<String, Promotion> activePromotions = findActivePromotions(buyProducts);

            //프로모션 적용 가능 여부 확인 후 적용
            Map<Product, PromotionApplyResult> promotionResults = applyNotPurchaseAmount(
                    buyProducts, activePromotions, buyProductsCopy);

            int totalProductPrice = getTotalProductPrice(buyProductsCopy, productList);
            int totalPromotedPrice = getTotalPromotedPrice(promotionResults);

            String checkMembership = inputView.checkMembership();
            int membershipSaleAmount = applyMembership(totalProductPrice - totalPromotedPrice, checkMembership);

            outputView.printReceipt(productList, buyProductsCopy, promotionResults, totalProductPrice,
                    membershipSaleAmount);

            if (inputView.checkAddtionalPurchase().equals("N")) {
                break;
            }
        }
    }

    private List<Product> displayStockList() {
        List<Product> productList = productService.getAllProducts();
        outputView.printWelcomeAndStockList(productList);
        return productList;
    }

    private static List<Product> cloneProductList(List<Product> buyProducts) {
        return buyProducts.stream()
                .map(Product::clone)
                .collect(Collectors.toList());
    }

    private Map<String, Promotion> findActivePromotions(List<Product> buyProducts) {
        Map<String, Promotion> activePromotions = new HashMap<>();
        for (Product buyProduct : buyProducts) {
            String name = buyProduct.getName();
            Product promotionProduct = productService.getPromotionProductByName(name);

            if (promotionProduct != null) {
                String promotion = promotionProduct.getPromotion();
                Promotion activePromotion = getActivePromotion(promotion);
                if (activePromotion != null) {
                    activePromotions.put(name, activePromotion);
                }
            }
        }
        return activePromotions;
    }

    private Promotion getActivePromotion(String promotionName) {
        Promotion promotion = promotionService.getPromotionByName(promotionName);
        if (promotion != null && isPromotionActive(promotion)) {
            return promotion;
        }
        return null;
    }

    private boolean isPromotionActive(Promotion promotion) {
        LocalDate now = LocalDate.from(DateTimes.now());
        return !now.isBefore(promotion.getStartDate()) && !now.isAfter(promotion.getEndDate());
    }

    private static int getTotalPromotedPrice(Map<Product, PromotionApplyResult> productPromotionApplyResults) {
        int totalPromotedPrice = 0;
        for (PromotionApplyResult promotionApplyResult : productPromotionApplyResults.values()) {
            int promotedPrice = promotionApplyResult.getTotalPromotedPrice();
            totalPromotedPrice += promotedPrice;
        }
        return totalPromotedPrice;
    }

    private Map<Product, PromotionApplyResult> applyPromotions(List<Product> buyProducts, Map<String, Promotion> activePromotions) {
        Map<Product, PromotionApplyResult> applyResults = new HashMap<>();

        for (Product buyProduct : buyProducts) {
            Promotion promotion = activePromotions.get(buyProduct.getName());
            if(promotion != null) {
                PromotionApplyResult promotionApplyResult = applyPromotion(buyProduct, promotion);
                applyResults.put(buyProduct, promotionApplyResult);
            }
        }
        return applyResults;
    }

    private PromotionApplyResult applyPromotion(Product buyProduct, Promotion promotion) {
        int promotionBuyAmount = promotion.getBuyAmount();
        int promotionGetAmount = promotion.getGetAmount();
        int promotionTotalAmount = promotionBuyAmount + promotionGetAmount;

        Product promotionProduct = productService.getPromotionProductByName(buyProduct.getName());
        int totalGetAmount = 0;
        int totalPromotedPrice = 0;
        int totalPromotedSalePrice = 0;

        while (buyProduct.getQuantity() >= promotionBuyAmount && promotionProduct.getQuantity() >= promotionTotalAmount) {
            totalGetAmount += promotionGetAmount;
            totalPromotedPrice += promotionTotalAmount * promotionProduct.getPrice();
            totalPromotedSalePrice += promotionGetAmount * promotionProduct.getPrice();
            buyProduct.decreaseQuantity(promotionTotalAmount);
            promotionProduct.decreaseQuantity(promotionTotalAmount);
        }
        int notPurchaseAmount = checkPurchaseWithoutPromotion(buyProduct);
        NotPurchaseProduct notPurchaseProduct = new NotPurchaseProduct(buyProduct.getName(), notPurchaseAmount);
        return new PromotionApplyResult(totalGetAmount, totalPromotedPrice, totalPromotedSalePrice, notPurchaseProduct);
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

    private Map<Product, PromotionApplyResult> applyNotPurchaseAmount(List<Product> buyProducts, Map<String, Promotion> activePromotions, List<Product> buyProductsCopy) {
        Map<Product, PromotionApplyResult> promotionResults = applyPromotions(buyProducts, activePromotions);
        for (PromotionApplyResult promotionApplyResult : promotionResults.values()) {
            NotPurchaseProduct notPurchaseProduct = promotionApplyResult.getNotPurchaseProduct();
            for (Product buyProduct : buyProductsCopy) {
                if(notPurchaseProduct.getName().equals(buyProduct.getName())) {
                    buyProduct.decreaseQuantity(notPurchaseProduct.getNotPurchaseAmount());
                }
            }
        }
        return promotionResults;
    }

    private int checkPurchaseWithoutPromotion(Product buyProduct) {
        if(buyProduct.getQuantity() > 0) {
            String checkPurchaseWithoutPromotion = inputView.checkPurchaseWithoutPromotion(buyProduct.getName());
            if(checkPurchaseWithoutPromotion.equals("N")) {
                int result = buyProduct.getQuantity();
                buyProduct.decreaseQuantity(buyProduct.getQuantity());
                return result;
            }
        }
        return 0;
    }
}
