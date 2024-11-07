package store.util;

public class MoneyFormatter {

    public static String formatMoney(int price) {
        return String.format("%,d", price);
    }
}
