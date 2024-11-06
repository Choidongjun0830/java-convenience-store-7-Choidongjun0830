package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.domain.Promotion;
import store.service.PromotionService;

public class InputView {

    public String getBuyProductAmount() {
        return Console.readLine();
    }

}
