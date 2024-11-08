package store.view;

import java.util.List;
import store.domain.Product;
import store.dto.PromotionApplyResult;
import store.dto.ReceiptInfo;
import store.util.MoneyFormatter;

public class OutputView {

    private static final String WELCOME = "안녕하세요. W편의점입니다.";
    private static final String STOCK_LIST_HEADER = "현재 보유하고 있는 상품입니다.";
    private static final String PRODUCT_INPUT_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";

    private static final String RECEIPT_HEADER = "===========W 편의점=============";
    private static final String PURCHASE_PRODUCT_LIST_HEADER = "상품명\t\t수량\t금액";
    private static final String GET_PROMOTION_PRODUCT_LIST_HEADER = "===========증\t정=============";
    private static final String PRICE_LIST_HEADER = "==============================";

    private static final String PROMOTION_NOT_APPLICABLE_MESSAGE = "프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 안내에 대한 메시지 출력";
    private static final String PROMOTION_SHORTAGE_MESSAGE = "프로모션 재고가 부족하여 일부 수량을 프로모션 없이 구매가 결정되어야 하면, 일부 수량에 대해 정가로 결제되었으며 이에 대한 안내 메시지 출력";
    private static final String STOCK_SHORTAGE = "재고 없음";

    private final MoneyFormatter moneyFormatter;

    public OutputView(MoneyFormatter moneyFormatter) {
        this.moneyFormatter = moneyFormatter;
    }

    public void printWelcomeAndStockList(List<Product> products) {
        System.out.println(WELCOME);
        System.out.println(STOCK_LIST_HEADER);

        for (Product product : products) {
            String name = product.getName();
            String price = moneyFormatter.formatMoney(product.getPrice());
            String quantity = isQuantityZero(product.getQuantity());
            String promotion = product.getPromotion();

            System.out.println("- " + name + " " + price + " " + quantity + " " + promotion);
        }
        System.out.println("\n" + PRODUCT_INPUT_MESSAGE);
    }

    public void printReceipt(List<Product> stockProducts,
                             List<Product> buyProducts,
                             List<PromotionApplyResult> productPromotionApplyResults,
                             ReceiptInfo receiptInfo) {
        System.out.println(RECEIPT_HEADER);
        int totalAllProductQuantity = printPurchaseProducts(stockProducts, buyProducts);

        printPromotionProducts(productPromotionApplyResults);
        int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults);
        printTotalPurchaseResult(receiptInfo.getTotalProductPrice(), receiptInfo.getMembershipSaleAmount(), totalAllProductQuantity, totalPromotedPrice);
    }


    private int printPurchaseProducts(List<Product> productList, List<Product> buyProducts) {
        System.out.println(PURCHASE_PRODUCT_LIST_HEADER);
        int totalAllProductQuantity = 0;
        for (Product buyProduct : buyProducts) {
            int totalPrice = getProductPrice(buyProduct.getName(), productList) * buyProduct.getQuantity();
            int quantity = buyProduct.getQuantity();
            totalAllProductQuantity += quantity;
            System.out.println(buyProduct.getName() + "\t\t" + quantity + "\t" + MoneyFormatter.formatMoney(totalPrice));
        }
        return totalAllProductQuantity;
    }

    private static void printPromotionProducts(List<PromotionApplyResult> productPromotionApplyResults) {
        System.out.println(GET_PROMOTION_PRODUCT_LIST_HEADER);
        for (PromotionApplyResult productPromotionApplyResult : productPromotionApplyResults) {
            Product promotionProduct = productPromotionApplyResult.getProduct();
            System.out.println(promotionProduct.getName() + "\t" + productPromotionApplyResult.getTotalGetAmount());
        }
    }

    private static void printTotalPurchaseResult(int totalProductPrice, int membershipSaleAmount, int totalAllProductQuantity,
                                                 int totalPromotedPrice) {
        System.out.println(PRICE_LIST_HEADER);
        System.out.println("총구매액" + "\t" + totalAllProductQuantity + "\t" + MoneyFormatter.formatMoney(
                totalProductPrice));
        System.out.println("행사할인" + "\t" + MoneyFormatter.formatMoney(totalPromotedPrice));
        System.out.println("멤버십할인" + "\t" + MoneyFormatter.formatMoney(membershipSaleAmount));
        System.out.println("내실돈" + "\t" + MoneyFormatter.formatMoney(
                totalProductPrice + totalPromotedPrice + membershipSaleAmount));
    }

    private String isQuantityZero(int quantity) {
        if (quantity == 0) {
            return "STOCK_SHORTAGE";
        }
        return String.valueOf(quantity) + "개";
    }

    private int getProductPrice(String name, List<Product> productList) {
        for (Product product : productList) {
            if(product.getName().equals(name)) {
                return product.getPrice();
            }
        }
        return 0;
    }

    private int getTotalPromotedPrice(List<PromotionApplyResult> productPromotionApplyResults) {
        int totalPromotedPrice = 0;
        for (PromotionApplyResult promotionApplyResult : productPromotionApplyResults) {
            totalPromotedPrice += promotionApplyResult.getTotalPromotedSalePrice();
        }
        return -totalPromotedPrice;
    }

}
