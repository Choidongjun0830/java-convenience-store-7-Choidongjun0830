package store.view;

import java.util.List;
import store.constant.OutputViewMessage;
import store.domain.Product;
import store.dto.PromotionApplyResult;
import store.dto.ReceiptInfo;
import store.util.MoneyFormatter;

public class OutputView {

    public OutputView() {
    }

    public void printWelcomeAndStockList(List<Product> products) {
        System.out.println(OutputViewMessage.WELCOME);
        System.out.println(OutputViewMessage.STOCK_LIST_HEADER);

        for (Product product : products) {
            String name = product.getName();
            String price = MoneyFormatter.formatMoney(product.getPrice());
            String quantity = isQuantityZero(product.getQuantity());
            String promotion = product.getPromotion();

            System.out.println(OutputViewMessage.STOCK_LIST_PREFIX + name + " " + price + " " + quantity + " " + promotion);
        }
        System.out.println("\n" + OutputViewMessage.PRODUCT_INPUT_MESSAGE);
    }

    public void printReceipt(List<Product> stockProducts,
                             List<Product> buyProducts,
                             List<PromotionApplyResult> productPromotionApplyResults,
                             ReceiptInfo receiptInfo) {
        System.out.println(OutputViewMessage.RECEIPT_HEADER);
        int totalAllProductQuantity = printPurchaseProducts(stockProducts, buyProducts);

        if(productPromotionApplyResults.size() > 0) {
            printPromotionProducts(productPromotionApplyResults);
        }
        int totalPromotedPrice = getTotalPromotedPrice(productPromotionApplyResults);
        printTotalPurchaseResult(receiptInfo.getTotalProductPrice(), receiptInfo.getMembershipSaleAmount(), totalAllProductQuantity, totalPromotedPrice);
    }


    private int printPurchaseProducts(List<Product> productList, List<Product> buyProducts) {
        System.out.println(OutputViewMessage.PURCHASE_PRODUCT_LIST_HEADER);
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
        System.out.println(OutputViewMessage.GET_PROMOTION_PRODUCT_LIST_HEADER);
        for (PromotionApplyResult productPromotionApplyResult : productPromotionApplyResults) {
            Product promotionProduct = productPromotionApplyResult.getProduct();
            System.out.println(promotionProduct.getName() + "\t" + productPromotionApplyResult.getTotalGetAmount());
        }
    }

    private static void printTotalPurchaseResult(int totalProductPrice, int membershipSaleAmount, int totalAllProductQuantity, int totalPromotedPrice) {
        System.out.println(OutputViewMessage.PRICE_LIST_HEADER);
        System.out.println(OutputViewMessage.TOTAL_PRICE_PREFIX + "\t" + totalAllProductQuantity + "\t" + MoneyFormatter.formatMoney(totalProductPrice));
        System.out.println(OutputViewMessage.PROMOTION_DISCOUNT_PREFIX + "\t" + MoneyFormatter.formatMoney(totalPromotedPrice));
        System.out.println(OutputViewMessage.MEMBERSHIP_DISCOUNT_PREFIX + "\t" + MoneyFormatter.formatMoney(membershipSaleAmount));
        System.out.println(OutputViewMessage.RESULT_TOTAL_PRICE_PREFIX + "\t" + MoneyFormatter.formatMoney(totalProductPrice + totalPromotedPrice + membershipSaleAmount));
    }

    private String isQuantityZero(int quantity) {
        if (quantity == 0) {
            return OutputViewMessage.STOCK_SHORTAGE;
        }
        return String.valueOf(quantity) + OutputViewMessage.PRODUCT_UNIT;
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
