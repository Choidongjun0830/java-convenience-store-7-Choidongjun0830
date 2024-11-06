package store.controller;

import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import store.domain.Product;
import store.domain.Promotion;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void startProcess() {
        List<Product> productList = inputView.getProductList();
        List<Promotion> promotionList = inputView.getPromotionList();
        outputView.printWelcomeAndStockList(productList);

        List<String> buyProductAmount = inputView.getBuyProductAmount();

            if(promotionProduct != null) {
                String promotion = promotionProduct.getPromotion();
                Promotion activePromotion = getActivePromotionByName(promotion);

                if(activePromotion != null) {
                    activePromotions.put(name, activePromotion);
                }
            }
        }
        return activePromotions;
    }


    private Promotion getActivePromotionByName(String promotionName) {
        LocalDate now = LocalDate.now();
        Promotion promotionByName = promotionService.getPromotionByName(promotionName);
        if (promotionByName != null) {
            LocalDate startDate = promotionByName.getStart_date();
            LocalDate endDate = promotionByName.getEnd_date();
            return promotionByName;
        }
        return null;
    }
}
