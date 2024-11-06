package store.domain;

import java.time.LocalDate;

public class Promotion {

    private String name;
    private int buyAmount;
    private int getAmount;
    private LocalDate start_date;
    private LocalDate end_date;

    public Promotion(String name, int buyAmount, int getAmount, LocalDate start_date, LocalDate end_date) {
        this.name = name;
        this.buyAmount = buyAmount;
        this.getAmount = getAmount;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "name='" + name + '\'' +
                ", buyAmount=" + buyAmount +
                ", getAmount=" + getAmount +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
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

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }
}
