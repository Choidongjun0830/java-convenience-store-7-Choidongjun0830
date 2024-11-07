package store.view;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import store.domain.Product;
import store.dto.PromotionApplyResult;
import store.util.MoneyFormatter;

public class OutputView {

    private static final String WELCOME = "안녕하세요. W편의점입니다.";
    private static final String STOCK_LIST_HEADER = "현재 보유하고 있는 상품입니다.";
    private static final String PRODUCT_INPUT_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";

    private static final String RECEIPT_HEADER = "===========W 편의점=============";
    private static final String BUY_PRODUCT_LIST_HEADER = "상품명\t\t수량\t금액";
    private static final String GET_PRODUCT_LIST_HEADER = "===========증\t정=============";
    private static final String PRICE_LIST_HEADER = "==============================";

    private static final String PROMOTION_NOT_APPLICABLE_MESSAGE = "프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 안내에 대한 메시지 출력";
    private static final String PROMOTION_SHORTAGE_MESSAGE = "프로모션 재고가 부족하여 일부 수량을 프로모션 없이 구매가 결정되어야 하면, 일부 수량에 대해 정가로 결제되었으며 이에 대한 안내 메시지 출력";

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

    public void printReceipt(List<Product> productList, List<Product> buyProducts, Map<Product, PromotionApplyResult> productPromotionApplyResults) {
        System.out.println(RECEIPT_HEADER);
        System.out.println(BUY_PRODUCT_LIST_HEADER);
        int totalAllProductPrice = 0;
        int totalAllProductQuantity = 0;
        for (Product buyProduct : buyProducts) {
            int totalPrice = getProductPrice(buyProduct.getName(), productList) * buyProduct.getQuantity();
            totalAllProductPrice += totalPrice;
            int quantity = buyProduct.getQuantity();
            totalAllProductQuantity += quantity;
            System.out.println(buyProduct.getName() + "\t\t" + quantity + "\t" + MoneyFormatter.formatMoney(totalPrice));
        }
        System.out.println(GET_PRODUCT_LIST_HEADER);
        for (Entry<Product, PromotionApplyResult> productPromotionApplyResult : productPromotionApplyResults.entrySet()) {
            Product promotionProduct = productPromotionApplyResult.getKey();
            PromotionApplyResult promotionApplyResult = productPromotionApplyResult.getValue();
            System.out.println(promotionProduct.getName() + "\t" + promotionApplyResult.getTotalGetAmount());
        }
        System.out.println(PRICE_LIST_HEADER);
        System.out.println("총구매액" + "\t" + totalAllProductQuantity + "\t" + MoneyFormatter.formatMoney(totalAllProductPrice));
        System.out.println("행사할인");
        System.out.println("멤버십할인");
        System.out.println("내실돈");
    }

    public void printPromotionNotApplicable() {
        System.out.println(PROMOTION_NOT_APPLICABLE_MESSAGE);
    }

    public void printPromotionShortage() {
        System.out.println(PROMOTION_SHORTAGE_MESSAGE);
    }

    private String isQuantityZero(int quantity) {
        if (quantity == 0) {
            return "재고 없음";
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

}
