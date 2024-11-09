package store.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.Store;
import store.dto.NotPurchaseProduct;
import store.dto.PromotionApplyResult;
import store.dto.PromotionInfo;

public class StoreService {

    private final ProductService productService;
    private final PromotionService promotionService;

    public StoreService(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public List<Store> connectProductsPromotions(List<Product> buyProducts) {
        List<Store> applyResults = new ArrayList<>();
        promotionService.getAllPromotions();
        Map<String, Promotion> activePromotions = promotionService.findActivePromotions(buyProducts);
        for (Product buyProduct : buyProducts) {
            Promotion promotion = activePromotions.get(buyProduct.getName());
            if(promotion != null) {
                applyResults.add(new Store(buyProduct, promotion));
                continue;
            }
            applyResults.add(new Store(buyProduct, null));
        }
        return applyResults;
    }

    public PromotionApplyResult purchasePromotionProduct(Product buyProduct, Promotion promotion, List<Product> buyProductClone) {
        PromotionInfo promotionInfo = setupPromotionInfo(promotion);
        Product promotionProduct = productService.getPromotionProductByName(buyProduct.getName());
        int totalGetAmount = 0;
        int totalPromotedPrice = 0;
        int totalPromotedSalePrice = 0;
        boolean isExtraPromotionProductApproved = true;
        while (buyProduct.getQuantity() >= promotionInfo.getPromotionBuyAmount() && promotionProduct.getQuantity() >= promotionInfo.getPromotionTotalAmount()) {
            if (!promotionService.applyExtraForPromo(buyProduct, buyProductClone, promotionInfo)) {
                isExtraPromotionProductApproved = false;
                break;
            }
            totalGetAmount += promotionInfo.getPromotionGetAmount();
            totalPromotedPrice += promotionInfo.getPromotionTotalAmount() * promotionProduct.getPrice();
            totalPromotedSalePrice += promotionInfo.getPromotionGetAmount() * promotionProduct.getPrice();
            buyProduct.decreaseQuantity(promotionInfo.getPromotionTotalAmount());
            promotionProduct.decreaseQuantity(promotionInfo.getPromotionTotalAmount());
        }
        int notPurchasedAmount = promotionService.checkPurchaseWithoutPromotion(buyProduct); //이거

        purchaseRegularProduct(buyProduct, promotionProduct);
        return new PromotionApplyResult(buyProduct ,totalGetAmount, totalPromotedPrice, totalPromotedSalePrice);
    }

    private PromotionInfo setupPromotionInfo(Promotion promotion) {
        return new PromotionInfo(promotion.getBuyAmount(), promotion.getGetAmount());
    }

    public void purchaseRegularProduct(Product purchaseProduct, Product stock) {
        if(purchaseProduct.getQuantity() > 0) {
            purchaseProductFromPromotionStock(purchaseProduct, regularProductStock);
            purchaseProductFromRegularStock(purchaseProduct);
        }
    }

    private void purchaseProductFromRegularStock(Product purchaseProduct) {
        int remainPurchaseProductQuantity = purchaseProduct.getQuantity();
        if(remainPurchaseProductQuantity > 0) {
            Product regularProduct = productService.getRegularProductByName(purchaseProduct.getName());
            regularProduct.decreaseQuantity(remainPurchaseProductQuantity);
            purchaseProduct.decreaseQuantity(remainPurchaseProductQuantity);
        }
    }

    private static void purchaseProductFromPromotionStock(Product purchaseProduct, Product regularProductStock) {
        int promotionProductQuantity = regularProductStock.getQuantity();
        if(promotionProductQuantity > 0) { //프로모션 재고부터 소진 시키기
            regularProductStock.decreaseQuantity(promotionProductQuantity);
            purchaseProduct.decreaseQuantity(promotionProductQuantity);
        }
    }
}
