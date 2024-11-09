package store.validator;

import java.util.ArrayList;
import java.util.List;
import store.constant.ExceptionMessage;
import store.domain.Product;
import store.dto.TotalProductStock;

public class InputValidator {
    // [ ]  - , 검증 -> 정규식
    public void purchaseProductInputPatternValidate(String buyProductAmountInput) {
        // [\\[\\,\\[]
        boolean patternMatches = buyProductAmountInput.matches("\\[([\\w가-힣]+)-(\\d+)\\](,\\[([\\w가-힣]+)-(\\d+)\\])*");
        if (!patternMatches) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        }
    }

    public void purchaseProductValidate(List<Product> stockProducts, List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
        validatePurchaseProductNotExistProductList(stockProducts, purchaseProducts);
        validatePurchaseAmountOverStock(purchaseProducts, totalProductStocks);
    }

    public void validateYesOrNoType(String response) { //수정
        List<String> possibleResponse = new ArrayList<>(List.of("Y", "N", "y", "n"));
        if (!possibleResponse.contains(response)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_EXCEPTION);
        }
    }

    private void validatePurchaseAmountOverStock(List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
        purchaseProducts.forEach(purchaseProduct -> {
            String purchaseProductName = purchaseProduct.getName();
            int purchaseQuantity = purchaseProduct.getQuantity();

            totalProductStocks.stream()
                    .filter(totalProductStock -> totalProductStock.getName().equals(purchaseProductName))
                    .findFirst()
                    .ifPresent(totalProductStock -> {
                        if (purchaseQuantity > totalProductStock.getStock()) {
                            throw new IllegalArgumentException(ExceptionMessage.STOCK_OVER_EXCEPTION);
                        }
                    });
        });
    }

    private void validatePurchaseProductNotExistProductList(List<Product> stockProducts, List<Product> purchaseProducts) {
        purchaseProducts.forEach(purchaseProduct -> {
            String purchaseProductName = purchaseProduct.getName();

            boolean existed = stockProducts.stream()
                    .anyMatch(stockProduct -> stockProduct.getName().equals(purchaseProductName));
            if(!existed) {
                throw new IllegalArgumentException(new IllegalArgumentException(ExceptionMessage.NOT_EXIST_PRODUCT_EXCEPTION));
            }
        });
    }
}
