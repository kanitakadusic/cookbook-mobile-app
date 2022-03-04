package com.example.cookbook;

public class Meal {
    private String name, cuisine, picture, instructions, ingredients;

    public Meal(String name, String cuisine, String picture, String instructions, String ingredients) {
        this.name = name;
        this.cuisine = cuisine;
        this.picture = picture;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getPicture() {
        return picture;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getIngredients() {
        return ingredients;
    }
}
