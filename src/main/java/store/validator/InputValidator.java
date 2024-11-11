package store.validator;

import java.util.ArrayList;
import java.util.List;
import store.constant.ExceptionMessage;
import store.domain.Product;
import store.dto.TotalProductStock;

public class InputValidator {

    public static final List<String> POSSIBLE_RESPONSE = new ArrayList<>(List.of("Y", "N"));

    public void purchaseProductInputPatternValidate(String buyProductAmountInput) {
        boolean patternMatches = buyProductAmountInput.matches("\\[\\s*([\\w가-힣]+)\\s*-\\s*(\\d+)\\s*\\](\\s*,\\s*\\[\\s*([\\w가-힣]+)\\s*-\\s*(\\d+)\\s*\\])*\\s*");
        if (!patternMatches) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        }
    }

    public void purchaseProductValidate(List<Product> stockProducts, List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
        validatePurchaseProductNotExistProductList(stockProducts, purchaseProducts);
        validatePurchaseAmountOverStock(purchaseProducts, totalProductStocks);
        validatePurchaseAmount(purchaseProducts);
    }

    public void validateYesOrNoType(String response) {
        if (!POSSIBLE_RESPONSE.contains(response)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_EXCEPTION);
        }
    }

    private void validatePurchaseAmountOverStock(List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
        purchaseProducts.forEach(purchaseProduct -> {
            String purchaseProductName = purchaseProduct.getName();
            int purchaseQuantity = purchaseProduct.getQuantity();
            validateOverStockProcess(totalProductStocks, purchaseProductName, purchaseQuantity);
        });
    }

    private static void validateOverStockProcess(List<TotalProductStock> totalProductStocks, String purchaseProductName, int purchaseQuantity) {
        totalProductStocks.stream()
                .filter(totalProductStock -> totalProductStock.getName().equals(purchaseProductName))
                .findFirst()
                .ifPresent(totalProductStock -> {
                    if (purchaseQuantity > totalProductStock.getStock()) {
                        throw new IllegalArgumentException(ExceptionMessage.STOCK_OVER_EXCEPTION);
                    }
                });
    }

    private void validatePurchaseProductNotExistProductList(List<Product> stockProducts, List<Product> purchaseProducts) {
        purchaseProducts.forEach(purchaseProduct -> {
            String purchaseProductName = purchaseProduct.getName();

            boolean existed = stockProducts.stream()
                    .anyMatch(stockProduct -> stockProduct.getName().equals(purchaseProductName));
            if(!existed) {
                throw new IllegalArgumentException(ExceptionMessage.NOT_EXIST_PRODUCT_EXCEPTION);
            }
        });
    }

    private void validatePurchaseAmount(List<Product> purchaseProducts) {
        for (Product purchaseProduct : purchaseProducts) {
            if(purchaseProduct.getQuantity() <= 0) {
                throw new IllegalArgumentException(ExceptionMessage.PURCHASE_AMOUNT_INVALID_EXCEPTION);
            }
        }
    }
}
