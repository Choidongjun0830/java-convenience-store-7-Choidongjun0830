package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import java.util.Scanner;
import store.domain.Product;
import store.domain.Promotion;
import store.parser.ProductParser;
import store.parser.PromotionParser;

public class InputView {

    private final ProductParser productParser;
    private final PromotionParser promotionParser;

    public InputView(ProductParser productParser, PromotionParser promotionParser) {
        this.productParser = productParser;
        this.promotionParser = promotionParser;
    }

    public List<Product> getProductList() {
        List<Product> products = ProductParser.parseProducts("src/main/resources/products.md");
        return products;
    };

    public List<Promotion> getPromotionList() {
        List<Promotion> promotions = PromotionParser.parseProducts("src/main/resources/promotions.md");
        return promotions;
    }

    public List<String> getBuyProductAmount() {
        String buyProductAmount = Console.readLine();
        String[] buyProductAmounts = buyProductAmount.split(",");

        for (int i = 0; i < buyProductAmounts.length; i++) {
            buyProductAmounts[i] = buyProductAmounts[i].trim();
        }
        return List.of(buyProductAmounts);
        //상품이 재고에 있는지
        //수량이 유효한지
    }

}
