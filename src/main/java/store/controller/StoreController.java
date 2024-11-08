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
            List<Product> stockProducts = displayStockList();
            List<Product> purchaseProducts = getPurchaseProducts(stockProducts);
            //영수증 출력을 위한 복제본 생성
            List<Product> purchaseProductsForReceipt = productService.cloneProductList(purchaseProducts);
            //프로모션 적용 가능 여부 확인 후 적용 + 구매
            List<PromotionApplyResult> productPromotionApplyResults = getPurchaseResults(purchaseProducts,
                    purchaseProductsForReceipt);
            //영수증에서 사용하는 정보 계산 및 멤버십 적용
            ReceiptInfo receiptInfo = calculateReceiptInfoAndApplyMembership(purchaseProductsForReceipt, stockProducts,
                    productPromotionApplyResults);
            outputView.printReceipt(stockProducts, purchaseProductsForReceipt, productPromotionApplyResults, receiptInfo);
        } while (!inputView.checkAdditionalPurchase().equals("N"));
    }

    private List<Product> getPurchaseProducts(List<Product> stockProducts) {
        while(true) {
            try {
                String buyProductAmountInput = inputView.getBuyProductAmount();
                inputValidator.purchaseProductInputPatternValidate(buyProductAmountInput);

                List<Product> purchaseProducts = productService.parsePurchaseProductFromInput(buyProductAmountInput);
                List<TotalProductStock> totalProductStocks = productService.getTotalProductStocks(purchaseProducts);
                inputValidator.purchaseProductValidate(stockProducts, purchaseProducts, totalProductStocks);
                return purchaseProducts;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
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
            storeService.purchaseRegularProduct(purchaseProduct, productService.getRegularProductByName(purchaseProduct.getName()));
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

    private ReceiptInfo calculateReceiptInfoAndApplyMembership(List<Product> purchaseProductsForReceipt, List<Product> stockProducts, List<PromotionApplyResult> productPromotionApplyResults) {
        int totalProductPrice = productService.getTotalProductPrice(purchaseProductsForReceipt, stockProducts); //상품 총액
        int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults); //프로모션으로 할인된 가격

        Membership membership = checkMembership();
        int membershipDiscountAmount = applyMembership(totalProductPrice - totalPromotedPrice, membership);

        return new ReceiptInfo(totalProductPrice, totalPromotedPrice, membershipDiscountAmount);
    }

    private Membership checkMembership() {
        while(true) { //depth 줄이기
            try{
                String getMembership = inputView.checkMembership();
                inputValidator.yesOrNoTypeValidate(getMembership);
                Membership checkMembership = Membership.NON_MEMBERSHIP;
                if (getMembership.equals("Y")) {
                    checkMembership =  Membership.MEMBERSHIP;
                }
                return checkMembership;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int applyMembership(int nonPromotedPrice, Membership checkMembership) {
        int saleAmount = (int) (nonPromotedPrice * checkMembership.getDiscountRate());

        if(saleAmount > 8000) {
            saleAmount = 8000;
        }
        return -saleAmount;
    }



}
