package store.util;

public class MoneyFormatter {

    public String formatMoney(int price) {
        return String.format("%,d", price) + "원";
    }
}
