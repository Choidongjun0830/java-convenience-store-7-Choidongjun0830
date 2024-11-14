package store.dto;

public class TotalProductStock {

    String name;
    int stock;

    public TotalProductStock(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}
