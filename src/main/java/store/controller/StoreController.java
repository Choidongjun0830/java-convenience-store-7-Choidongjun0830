package store.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.Store;
import store.dto.PromotionApplyResult;
import store.dto.ReceiptInfo;
import store.service.ProductService;
import store.service.PromotionService;
import store.service.StoreService;
import store.validator.InputValidator;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final StoreService storeService;
    private final InputValidator inputValidator;

    public StoreController(InputView inputView, OutputView outputView, ProductService productService,
                           PromotionService promotionService, StoreService storeService, InputValidator inputValidator) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.productService = productService;
        this.promotionService = promotionService;
        this.storeService = storeService;
        this.inputValidator = inputValidator;
    }

    public void startProcess() {
        do {
            List<Product> productList = displayStockList();
            promotionService.getAllPromotions();
            List<Product> purchaseProducts = getPurchaseProducts();
            //영수증 출력을 위한 복제본 생성
            List<Product> purchaseProductsForReceipt = productService.cloneProductList(purchaseProducts);
            //프로모션 적용 가능 여부 확인 후 적용 + 구매
            List<PromotionApplyResult> productPromotionApplyResults = getPurchaseResults(purchaseProducts,
                    purchaseProductsForReceipt);
            //영수증에서 사용하는 정보 계산 및 멤버십 적용
            ReceiptInfo receiptInfo = calculateReceiptInfoAndApplyMembership(purchaseProductsForReceipt, productList,
                    productPromotionApplyResults);
            outputView.printReceipt(productList, purchaseProductsForReceipt, productPromotionApplyResults, receiptInfo);
        } while (!inputView.checkAdditionalPurchase().equals("N"));
    }

    private ReceiptInfo calculateReceiptInfoAndApplyMembership(List<Product> purchaseProductsForReceipt, List<Product> productList, List<PromotionApplyResult> productPromotionApplyResults) {
        int totalProductPrice = productService.getTotalProductPrice(purchaseProductsForReceipt, productList); //상품 총액
        int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults); //프로모션으로 할인된 가격
        int membershipSaleAmount = applyMembership(totalProductPrice - totalPromotedPrice);

        return new ReceiptInfo(totalProductPrice, totalPromotedPrice, membershipSaleAmount);
    }

    private List<Product> getPurchaseProducts() {
        String buyProductAmountInput = inputView.getBuyProductAmount();

        List<Product> purchaseProducts = productService.parsePurchaseProductFromInput(buyProductAmountInput);
        productService.validateStock(purchaseProducts);
        return purchaseProducts;
    }

    private List<PromotionApplyResult> getPurchaseResults(List<Product> purchaseProducts,
                                                          List<Product> purchaseProductsForReceipt) {
        List<Store> productPromotionStore = storeService.connectProductsPromotions(purchaseProducts);
        List<PromotionApplyResult> productPromotionApplyResults = new ArrayList<>();
        for (Store productPromotion : productPromotionStore) {
            purchaseProducts(productPromotion, purchaseProductsForReceipt, productPromotionApplyResults);
        }
        return productPromotionApplyResults;
    }

    private void purchaseProducts(Store productPromotion, List<Product> buyProductsForReceipt,
                           List<PromotionApplyResult> productPromotionApplyResults) {
        Product purchaseProduct = productPromotion.getProduct();
        Promotion appliedPromotion = productPromotion.getPromotion();
        if(appliedPromotion == null) {
            storeService.purchaseRegularProduct(purchaseProduct, productService.getRegularProductByName(purchaseProduct.getName()));//여기 들어갈거 그냥 productService에서 같은 이름의 promotionProduct 가져오기)
            return;
        }

        PromotionApplyResult promotionApplyResult = storeService.purchasePromotionProduct(purchaseProduct,
                appliedPromotion, buyProductsForReceipt);
        productPromotionApplyResults.add(promotionApplyResult);
    }

    private List<Product> displayStockList() {
        List<Product> productList = productService.getAllProducts();
        outputView.printWelcomeAndStockList(productList);
        return productList;
    }

    private static int getTotalPromotedPrice(List<PromotionApplyResult> productPromotionApplyResults) {
        int totalPromotedPrice = 0;
        for (PromotionApplyResult promotionApplyResult : productPromotionApplyResults) {
            int promotedPrice = promotionApplyResult.getTotalPromotedPrice();
            totalPromotedPrice += promotedPrice;
        }
        return totalPromotedPrice;
    }

    private int applyMembership(int nonPromotedPrice, String checkMembership) { //멤버십을 Enum으로?
        if(checkMembership.equalsIgnoreCase("Y")) {
            int saleAmount = nonPromotedPrice * 30 / 100;
            if(saleAmount >= 8000) {
                return -8000;
            }
            return -saleAmount;
        }
        return -0;
    }

    private int applyMembership(int nonPromotedPrice) {
        String checkMembership = inputView.checkMembership();
        int membershipSaleAmount = applyMembership(nonPromotedPrice, checkMembership);
        return membershipSaleAmount;
    }

}
