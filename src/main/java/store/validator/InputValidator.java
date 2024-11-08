package store.validator;

import java.util.List;
import store.constant.ExceptionMessage;
import store.domain.Product;
import store.dto.TotalProductStock;

public class InputValidator {
    // [ ]  - , 검증 -> 정규식
    public void purchaseProductInputPatternValidate(String buyProductAmountInput) {
        // [\\[\\,\\[]
        boolean patternMatches = buyProductAmountInput.matches("\\[(\\w+)-(\\d+)\\](,\\[(\\w+)-(\\d+)\\])*");
        if (!patternMatches) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        }
    }

    public void purchaseProductValidate(List<Product> stockProducts, List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
        purchaseProductNameBlankValidate(stockProducts);
        purchaseProductNotExistProductListValidate(stockProducts, purchaseProducts);
        validatePositivePurchaseQuantity(stockProducts);
        purchaseAmountOverStockValidate(purchaseProducts, totalProductStocks);
    }

    public void yesOrNoTypeValidate(String response) {
        for(char c: response.toCharArray()) {
            if(!Character.isLetter(c)) throw new IllegalArgumentException(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        }
    }

    private void purchaseAmountOverStockValidate(List<Product> purchaseProducts, List<TotalProductStock> totalProductStocks) {
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

    private void purchaseProductNotExistProductListValidate(List<Product> stockProducts, List<Product> purchaseProducts) {
        purchaseProducts.forEach(purchaseProduct -> {
            String purchaseProductName = purchaseProduct.getName();

            boolean existed = stockProducts.stream()
                    .anyMatch(stockProduct -> stockProduct.getName().equals(purchaseProductName));
            if(!existed) {
                throw new IllegalArgumentException(new IllegalArgumentException(ExceptionMessage.NOT_EXIST_PRODUCT_EXCEPTION));
            }
        });
    }

    private void purchaseProductNameBlankValidate(List<Product> purchaseProducts) {
        for (Product purchaseProduct : purchaseProducts) {
            if (purchaseProduct.getName().isBlank()) {
                throw new IllegalArgumentException(ExceptionMessage.PURCHASE_NAME_BLANK_EXCEPTION);
            }
        }
    }

    private void validatePositivePurchaseQuantity(List<Product> purchaseProducts) {
        for (Product purchaseProduct : purchaseProducts) {
            if (purchaseProduct.getQuantity() < 1) {
                throw new IllegalArgumentException(ExceptionMessage.PURCHASE_AMOUNT_INVALID_EXCEPTION);
            }
        }
    }

    private void validatePurchaseQuantityType(List<Product> purchaseProducts) {

    }
}
