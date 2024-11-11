package store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.Store;
import store.dto.PromotionApplyResult;
import store.dto.PromotionInfo;
import store.dto.TotalProductStock;

public class StoreService {

    private final ProductService productService;
    private final PromotionService promotionService;

    public StoreService(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public List<TotalProductStock> getTotalProductStocks(List<Product> buyProducts) {
        return getTotalProductStock(buyProducts)
                .collect(Collectors.toList());
    }

    public List<Store> connectProductsPromotions(List<Product> buyProducts) {
        List<Store> applyResults = new ArrayList<>();
        Map<String, Promotion> activePromotions = promotionService.findActivePromotions(buyProducts);
        purchaseProductStart(buyProducts, activePromotions, applyResults);
        return applyResults;
    }

    public void purchaseProducts(Store productPromotion, List<Product> buyProductsForReceipt, List<PromotionApplyResult> productPromotionApplyResults) {
        Product purchaseProduct = productPromotion.getProduct();
        Promotion appliedPromotion = productPromotion.getPromotion();
        if(appliedPromotion == null) {
            purchaseRegularProduct(purchaseProduct, productService.getRegularProductByName(purchaseProduct.getName()));
            return;
        }
        PromotionApplyResult promotionApplyResult = purchasePromotionProduct(purchaseProduct, appliedPromotion, buyProductsForReceipt);
        productPromotionApplyResults.add(promotionApplyResult);
    }

    public PromotionApplyResult purchasePromotionProduct(Product buyProduct, Promotion promotion, List<Product> purchaseProductsForReceipt) {
        PromotionInfo promotionInfo = setupPromotionInfo(promotion);
        Product promotionProduct = productService.getPromotionProductByName(buyProduct.getName());
        PromotionApplyResult promotionApplyResult = new PromotionApplyResult(buyProduct, 0, 0, 0);

        purchaseProcess(buyProduct, promotion, purchaseProductsForReceipt, promotionInfo, promotionProduct,
                promotionApplyResult);
        return promotionApplyResult;
    }

    public void purchaseRegularProduct(Product purchaseProduct, Product stock) {
        if(purchaseProduct.getQuantity() > 0) {
            if(!stock.getPromotion().isBlank()) purchaseProductFromPromotionStock(purchaseProduct, stock);
            purchaseProductFromRegularStock(purchaseProduct);
        }
    }

    private void purchaseProcess(Product buyProduct, Promotion promotion, List<Product> purchaseProductsForReceipt,
                           PromotionInfo promotionInfo, Product promotionProduct,
                           PromotionApplyResult promotionApplyResult) {
        boolean isExtraPromotionProductApproved = applyPromotionProcess(buyProduct, purchaseProductsForReceipt,
                promotionInfo, promotionProduct, promotionApplyResult);
        if(isExtraPromotionProductApproved && buyProduct.getQuantity() > 0 && !promotion.getName().isBlank()) {
            promotionService.checkPurchaseWithoutPromotion(buyProduct, purchaseProductsForReceipt);
        }
        purchaseRegularProduct(buyProduct, promotionProduct);
    }

    private static void purchaseProductStart(List<Product> buyProducts, Map<String, Promotion> activePromotions,
                                             List<Store> applyResults) {
        for (Product buyProduct : buyProducts) {
            Promotion promotion = activePromotions.get(buyProduct.getName());
            if(promotion != null) {
                applyResults.add(new Store(buyProduct, promotion));
                continue;
            }
            applyResults.add(new Store(buyProduct, null));
        }
    }

    private PromotionInfo setupPromotionInfo(Promotion promotion) {
        return new PromotionInfo(promotion.getBuyAmount(), promotion.getGetAmount());
    }

    private boolean applyPromotionProcess(Product buyProduct, List<Product> purchaseProductsForReceipt, PromotionInfo promotionInfo, Product promotionProduct, PromotionApplyResult promotionApplyResult) {
        boolean isExtraPromotionProductApproved = true;
        while (buyProduct.getQuantity() >= promotionInfo.getPromotionBuyAmount() && promotionProduct.getQuantity() >= promotionInfo.getPromotionTotalAmount()) {
            if (!promotionService.applyExtraForPromo(buyProduct, purchaseProductsForReceipt, promotionInfo)) {
                isExtraPromotionProductApproved = false;
                break;
            }
            calculatePromotionTotals(buyProduct, promotionProduct, promotionInfo, promotionApplyResult);
        }
        return isExtraPromotionProductApproved;
    }

    private void calculatePromotionTotals(Product buyProduct, Product promotionProduct, PromotionInfo promotionInfo, PromotionApplyResult promotionApplyResult) {
        promotionApplyResult.addTotalGetAmount(promotionInfo.getPromotionGetAmount());
        promotionApplyResult.addTotalPromotedPrice(promotionInfo.getPromotionTotalAmount() * promotionProduct.getPrice());
        promotionApplyResult.addTotalPromotedSalePrice(promotionInfo.getPromotionGetAmount() * promotionProduct.getPrice());
        buyProduct.decreaseQuantity(promotionInfo.getPromotionTotalAmount());
        promotionProduct.decreaseQuantity(promotionInfo.getPromotionTotalAmount());
    }

    private void purchaseProductFromRegularStock(Product purchaseProduct) {
        int remainPurchaseProductQuantity = purchaseProduct.getQuantity();
        if(remainPurchaseProductQuantity > 0) {
            Product regularProduct = productService.getRegularProductByName(purchaseProduct.getName());
            regularProduct.decreaseQuantity(remainPurchaseProductQuantity);
            purchaseProduct.decreaseQuantity(remainPurchaseProductQuantity);
        }
    }

    private static void purchaseProductFromPromotionStock(Product purchaseProduct, Product stock) {
        int promotionProductQuantity = stock.getQuantity();
        if(promotionProductQuantity > 0) { //프로모션 재고부터 소진 시키기
            int purchaseQuantity = purchaseProduct.getQuantity();
            if(purchaseQuantity > promotionProductQuantity) purchaseQuantity = promotionProductQuantity;
            stock.decreaseQuantity(purchaseQuantity);
            purchaseProduct.decreaseQuantity(purchaseQuantity);
        }
    }

    private Stream<TotalProductStock> getTotalProductStock(List<Product> buyProducts) {
        return buyProducts.stream().map(buyProduct -> {
            String name = buyProduct.getName();
            Product promotionProduct = productService.getPromotionProductByName(name);
            int promotionQuantity = getPromotionStockQuantity(promotionProduct);

            int regularQuantity = getRegularStockQuantity(name);

            return new TotalProductStock(name, promotionQuantity + regularQuantity);
        });
    }

    private int getRegularStockQuantity(String name) {
        int regularQuantity = Optional.ofNullable(productService.getRegularProductByName(name))
                .map(Product::getQuantity).orElse(0);
        return regularQuantity;
    }

    private int getPromotionStockQuantity(Product promotionProduct) {
        int promotionQuantity = Optional.ofNullable(promotionProduct)
                .filter(prod -> promotionService.isPromotionActive(promotionService.getPromotionByName(prod.getPromotion())))
                .map(Product::getQuantity).orElse(0);
        return promotionQuantity;
    }
}
