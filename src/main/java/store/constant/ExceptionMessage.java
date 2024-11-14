package store.constant;

public class ExceptionMessage {
    private static final String EXCEPTION_PREFIX = "[ERROR]";

    public static final String STOCK_OVER_EXCEPTION = EXCEPTION_PREFIX + " 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.";
    public static final String INVALID_FORMAT_EXCEPTION = EXCEPTION_PREFIX + " 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.";
    public static final String NOT_EXIST_PRODUCT_EXCEPTION = EXCEPTION_PREFIX + " 존재하지 않는 상품입니다. 다시 입력해 주세요.";
    public static final String INVALID_INPUT_EXCEPTION = EXCEPTION_PREFIX + " 잘못된 입력입니다. 다시 입력해 주세요.";
    public static final String PURCHASE_AMOUNT_INVALID_EXCEPTION = EXCEPTION_PREFIX + " 구매할 상품 수량은 1보다 작을 수 없습니다. 다시 입력해 주세요.";
}
