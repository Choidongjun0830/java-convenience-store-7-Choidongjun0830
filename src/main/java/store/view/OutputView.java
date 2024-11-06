package store.view;

import java.util.List;
import store.domain.Product;
import store.util.MoneyFormatter;

public class OutputView {

    private static final String WELCOME = "안녕하세요. W편의점입니다.";
    private static final String STOCK_LIST_HEADER = "현재 보유하고 있는 상품입니다.";
    private static final String PRODUCT_INPUT_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";

    private static final String PROMOTION_NOT_APPLICABLE_MESSAGE = "프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 안내에 대한 메시지 출력";
    private static final String PROMOTION_SHORTAGE_MESSAGE = "프로모션 재고가 부족하여 일부 수량을 프로모션 없이 구매가 결정되어야 하면, 일부 수량에 대해 정가로 결제되었으며 이에 대한 안내 메시지 출력";
    private static final String MEMBERSHIP_DISCOUNT_QUERY_MESSAGE = "멤버십 할인을 받으시겠습니까? (Y/N)";

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

    public void printPromotionNotApplicable() {
        System.out.println(PROMOTION_NOT_APPLICABLE_MESSAGE);
    }

    public void printPromotionShortage() {
        System.out.println(PROMOTION_SHORTAGE_MESSAGE);
    }

    public void printMembershipDiscountQuery() {
        System.out.println(MEMBERSHIP_DISCOUNT_QUERY_MESSAGE);
    }


    private String isQuantityZero(int quantity) {
        if (quantity == 0) {
            return "재고 없음";
        }
        return String.valueOf(quantity) + "개";
    }

}
