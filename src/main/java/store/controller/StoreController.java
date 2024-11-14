package store.controller;

import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.Store;
import store.dto.PromotionApplyResult;
import store.dto.ReceiptInfo;
import store.dto.TotalProductStock;
import store.enumerate.Membership;
import store.service.MembershipService;
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
    private final StoreService storeService;
    private final InputValidator inputValidator;
    private final MembershipService membershipService;
    private final PromotionService promotionService;

    public StoreController(InputView inputView, OutputView outputView, ProductService productService,
                           StoreService storeService, InputValidator inputValidator,
                           MembershipService membershipService, PromotionService promotionService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.productService = productService;
        this.storeService = storeService;
        this.inputValidator = inputValidator;
        this.membershipService = membershipService;
        this.promotionService = promotionService;
    }

    public void startProcess() {
        do {
            promotionService.getAllPromotions();
            List<Product> stockProducts = displayStockList();
            List<Product> purchaseProducts = getPurchaseProducts(stockProducts);
            List<Product> purchaseProductsForReceipt = productService.cloneProductList(purchaseProducts);
            List<PromotionApplyResult> productPromotionApplyResults = getPurchaseResults(purchaseProducts, purchaseProductsForReceipt);

            ReceiptInfo receiptInfo = calculateReceiptInfoAndApplyMembership(purchaseProductsForReceipt, stockProducts, productPromotionApplyResults);
            outputView.printReceipt(stockProducts, purchaseProductsForReceipt, productPromotionApplyResults, receiptInfo);
        } while (isNo());
    }

    private List<Product> getPurchaseProducts(List<Product> stockProducts) {
        while(true) {
            try {
                return getProducts(stockProducts);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private List<Product> getProducts(List<Product> stockProducts) {
        String buyProductAmountInput = inputView.getBuyProductAmount();
        inputValidator.purchaseProductInputPatternValidate(buyProductAmountInput);

        List<Product> purchaseProducts = productService.parsePurchaseProductFromInput(buyProductAmountInput);
        List<TotalProductStock> totalProductStocks = storeService.getTotalProductStocks(purchaseProducts);
        inputValidator.purchaseProductValidate(stockProducts, purchaseProducts, totalProductStocks);
        return purchaseProducts;
    }

    private List<PromotionApplyResult> getPurchaseResults(List<Product> purchaseProducts,
                                                          List<Product> purchaseProductsForReceipt) {
        List<Store> productPromotionStore = storeService.connectProductsPromotions(purchaseProducts);
        List<PromotionApplyResult> productPromotionApplyResults = new ArrayList<>();
        for (Store productPromotion : productPromotionStore) {
            storeService.purchaseProducts(productPromotion, purchaseProductsForReceipt, productPromotionApplyResults);
        }
        return productPromotionApplyResults;
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

    private boolean isNo() {
        while(true) {
            try {
                String checkAdditionalPurchase = inputView.checkAdditionalPurchase();
                inputValidator.validateYesOrNoType(checkAdditionalPurchase);
                return !checkAdditionalPurchase.equals("N");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private ReceiptInfo calculateReceiptInfoAndApplyMembership(List<Product> purchaseProductsForReceipt, List<Product> stockProducts, List<PromotionApplyResult> productPromotionApplyResults) {
        int totalProductPrice = productService.getTotalProductPrice(purchaseProductsForReceipt, stockProducts); //상품 총액
        int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults); //프로모션으로 할인된 가격

        int membershipDiscountAmount = 0;
        if(totalProductPrice != 0 && totalProductPrice - totalPromotedPrice > 0){
            Membership membership = membershipService.checkMembership();
            membershipDiscountAmount = membershipService.applyMembership(totalProductPrice - totalPromotedPrice, membership);
        }
        return new ReceiptInfo(totalProductPrice, totalPromotedPrice, membershipDiscountAmount);
    }

}
