package store.view;

import camp.nextstep.edu.missionutils.Console;

public class InputView {

    private static final String MEMBERSHIP_DISCOUNT_QUERY_MESSAGE = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String ADDITIONAL_PURCHASE_QUERY_MESSAGE = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private static final String WITHOUT_PROMOTION_QUERY_MESSAGE = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private static final String ADDITIONAL_QUANTITY_QUERY_MESSAGE = "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";

    public String getBuyProductAmount() {
        return Console.readLine().trim();
    }

    public String checkMembership() {
        System.out.println(MEMBERSHIP_DISCOUNT_QUERY_MESSAGE);
        return Console.readLine().trim();
    }

    public String checkAdditionalPurchase() {
        System.out.println(ADDITIONAL_PURCHASE_QUERY_MESSAGE);
        return Console.readLine().trim();
    }

    public String checkPurchaseWithoutPromotion(String product, int quantity) {
        System.out.println(String.format(WITHOUT_PROMOTION_QUERY_MESSAGE, product, quantity));
        return Console.readLine().trim();
    }

    public String checkAdditionalQuantity(String product, int quantity) {
        System.out.println(String.format(ADDITIONAL_QUANTITY_QUERY_MESSAGE, product, quantity));
        return Console.readLine().trim();
    }

}
