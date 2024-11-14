package store.domain;

import java.time.LocalDate;

public class Promotion {

    private String name;
    private int buyAmount;
    private int getAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String name, int buyAmount, int getAmount, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buyAmount = buyAmount;
        this.getAmount = getAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "name='" + name + '\'' +
                ", buyAmount=" + buyAmount +
                ", getAmount=" + getAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getBuyAmount() {
        return buyAmount;
    }

    public int getGetAmount() {
        return getAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
