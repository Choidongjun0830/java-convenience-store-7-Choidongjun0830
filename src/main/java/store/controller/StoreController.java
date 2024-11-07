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
            List<Product> buyProductsForReceipt = productService.cloneProductList(buyProducts);
            Map<String, Promotion> activePromotions = findActivePromotions(buyProducts);

            //프로모션 적용 가능 여부 확인 후 적용
            Map<Product, PromotionApplyResult> promotionResults = applyNotPurchaseAmount(
                    buyProducts, activePromotions, buyProductsForReceipt);

            int totalProductPrice = productService.getTotalProductPrice(buyProductsForReceipt, productList);
            int totalPromotedPrice = getTotalPromotedPrice(promotionResults);

            String checkMembership = inputView.checkMembership();
            int membershipSaleAmount = applyMembership(totalProductPrice - totalPromotedPrice, checkMembership);

            outputView.printReceipt(productList, buyProductsForReceipt, promotionResults, totalProductPrice, membershipSaleAmount);

            if (inputView.checkAdditionalPurchase().equals("N")) {
                break;
            }
        }
    }

    private List<Product> displayStockList() {
        List<Product> productList = productService.getAllProducts();
        outputView.printWelcomeAndStockList(productList);
        return productList;
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

    private Map<Product, PromotionApplyResult> applyPromotions(List<Product> buyProducts, Map<String, Promotion> activePromotions, List<Product> buyProductClone) {
        Map<Product, PromotionApplyResult> applyResults = new HashMap<>();

        for (Product buyProduct : buyProducts) {
            Promotion promotion = activePromotions.get(buyProduct.getName());
            if(promotion != null) {
                PromotionApplyResult promotionApplyResult = applyPromotion(buyProduct, promotion, buyProductClone);
                applyResults.put(buyProduct, promotionApplyResult);
                continue;
            }
            buyRegularProduct(buyProduct, buyProductClone);

        }
        return applyResults;
    }

    private void buyRegularProduct(Product buyProduct, List<Product> buyProductClone) {
        Product product = productService.getRegularProductByName(buyProduct.getName());
        int quantity = buyProduct.getQuantity();
        if(product.getQuantity() >= quantity) {
            product.decreaseQuantity(quantity);
            buyProduct.decreaseQuantity(quantity);
            return;
        }
        //예외처리
    }

    private PromotionApplyResult applyPromotion(Product buyProduct, Promotion promotion, List<Product> buyProductClone) {
        int promotionBuyAmount = promotion.getBuyAmount();
        int promotionGetAmount = promotion.getGetAmount();
        int promotionTotalAmount = promotionBuyAmount + promotionGetAmount;

        Product promotionProduct = productService.getPromotionProductByName(buyProduct.getName());
        int totalGetAmount = 0;
        int totalPromotedPrice = 0;
        int totalPromotedSalePrice = 0;

        while (buyProduct.getQuantity() >= promotionBuyAmount && promotionProduct.getQuantity() >= promotionTotalAmount) {

            if (applyExtraForPromo(buyProduct, buyProductClone, promotionBuyAmount, promotionTotalAmount, promotionGetAmount)) {
                break;
            }

            totalGetAmount += promotionGetAmount;
            totalPromotedPrice += promotionTotalAmount * promotionProduct.getPrice();
            totalPromotedSalePrice += promotionGetAmount * promotionProduct.getPrice();
            buyProduct.decreaseQuantity(promotionTotalAmount);
            promotionProduct.decreaseQuantity(promotionTotalAmount);
        }

        int notPurchaseAmount = checkPurchaseWithoutPromotion(buyProduct);
        NotPurchaseProduct notPurchaseProduct = new NotPurchaseProduct(buyProduct.getName(), notPurchaseAmount);

        if(buyProduct.getQuantity() > 0) {
            int promotionProductQuantity = promotionProduct.getQuantity();
            if(promotionProductQuantity > 0) {
                promotionProduct.decreaseQuantity(promotionProductQuantity);
                buyProduct.decreaseQuantity(promotionProductQuantity);
            }
            int remainBuyProductQuantity = buyProduct.getQuantity();
            if(remainBuyProductQuantity > 0) {
                Product regularProduct = productService.getRegularProductByName(buyProduct.getName());
                regularProduct.decreaseQuantity(remainBuyProductQuantity);
                buyProduct.decreaseQuantity(remainBuyProductQuantity);
            }
        }
        return new PromotionApplyResult(totalGetAmount, totalPromotedPrice, totalPromotedSalePrice, notPurchaseProduct);
    }

    private boolean applyExtraForPromo(Product buyProduct, List<Product> buyProductClone, int promotionBuyAmount,
                              int promotionTotalAmount, int promotionGetAmount) {
        if(buyProduct.getQuantity() == promotionBuyAmount && buyProduct.getQuantity() < promotionTotalAmount) {;
            String response = inputView.checkAdditionalQuantity(buyProduct.getName(), promotionGetAmount);
            if(response.equalsIgnoreCase("Y")) {
                buyProduct.increaseQuantity(promotionGetAmount);
                increaseTotalPurchaseAmount(buyProduct.getName(), buyProductClone, promotionGetAmount);
            }
            if(buyProduct.getQuantity() < promotionGetAmount) {
                return true;
            }
        }
        return false;
    }





    private int applyMembership(int nonPromotedPrice, String checkMembership) {
        if(checkMembership.equalsIgnoreCase("Y")) {
            int saleAmount = nonPromotedPrice * 30 / 100;
            if(saleAmount >= 8000) {
                return -8000;
            }
            return -saleAmount;
        }
        return -0;
    }

    private Map<Product, PromotionApplyResult> applyNotPurchaseAmount(List<Product> buyProducts, Map<String, Promotion> activePromotions, List<Product> buyProductsCopy) {
        Map<Product, PromotionApplyResult> promotionResults = applyPromotions(buyProducts, activePromotions, buyProductsCopy);
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
            String checkPurchaseWithoutPromotion = inputView.checkPurchaseWithoutPromotion(buyProduct.getName(), buyProduct.getQuantity());
            if(checkPurchaseWithoutPromotion.equals("N")) {
                int result = buyProduct.getQuantity();
                buyProduct.decreaseQuantity(buyProduct.getQuantity());
                return result;
            }
        }
        return 0;
    }

    private void increaseTotalPurchaseAmount(String name, List<Product> buyProductCopy, int increaseAmount) {
        for (Product product : buyProductCopy) {
            if(product.getName().equals(name)) {
                product.increaseQuantity(increaseAmount);
            }
        }
    }
}
