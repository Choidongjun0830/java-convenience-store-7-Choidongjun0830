package store.util;

public class MoneyFormatter {

    public static String formatMoney(int price) {
        if(price == 0) {
            return "-" + price;
        }
        return String.format("%,d", price);
    }
}
