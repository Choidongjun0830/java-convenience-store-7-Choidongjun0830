package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.domain.Promotion;
import store.service.PromotionService;

public class InputView {

    private static final String MEMBERSHIP_DISCOUNT_QUERY_MESSAGE = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String ADDITIONAL_PURCHASE_QUERY_MESSAGE = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";

    public String getBuyProductAmount() {
        return Console.readLine();
    }

    public String checkMembership() {
        System.out.println(MEMBERSHIP_DISCOUNT_QUERY_MESSAGE);
        return Console.readLine();
    }

    public String checkAddtionalPurchase() {
        System.out.println(ADDITIONAL_PURCHASE_QUERY_MESSAGE);
        return Console.readLine();
    }

}
