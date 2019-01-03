package com.l.main.board.domain;

public class Weight {

    private double weight;
    private String doubleARate;

    public String getDoubleARate() {
        return doubleARate;
    }

    public void setDoubleARate(String doubleARate) {
        this.doubleARate = doubleARate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    @Override
    public String toString() {
        return "Weight{" +
                "weight=" + weight +
                ", doubleARate='" + doubleARate + '\'' +
                '}';
    }
}
