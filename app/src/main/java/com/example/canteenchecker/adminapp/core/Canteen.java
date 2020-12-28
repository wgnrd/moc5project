package com.example.canteenchecker.adminapp.core;

public class Canteen {
    private final String id;
    private final String name;
    private final String dish;
    private final float dishPrice;
    private final float averageRating;

    public Canteen(String id, String name, String setMeal, float setMealPrice, float averageRating) {
        this.id = id;
        this.name = name;
        this.dish = setMeal;
        this.dishPrice = setMealPrice;
        this.averageRating = averageRating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDish() {
        return dish;
    }

    public float getDishPrice() {
        return dishPrice;
    }

    public float getAverageRating() {
        return averageRating;
    }
}
