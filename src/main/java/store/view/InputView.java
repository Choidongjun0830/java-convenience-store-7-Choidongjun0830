package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.domain.Promotion;
import store.service.PromotionService;

public class InputView {

    private static final String MEMBERSHIP_DISCOUNT_QUERY_MESSAGE = "멤버십 할인을 받으시겠습니까? (Y/N)";


    public String getBuyProductAmount() {
        return Console.readLine();
    }

    public String checkMembership() {
        System.out.println(MEMBERSHIP_DISCOUNT_QUERY_MESSAGE);
        return Console.readLine();
    }

}
