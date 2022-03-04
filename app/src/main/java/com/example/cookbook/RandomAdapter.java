package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.RandomHolder> {
    private Context context;
    private List<Meal> randomList;

    public RandomAdapter(Context context, List<Meal> meals) {
        this.context = context;
        randomList = meals;
    }

    @NonNull
    @Override
    public RandomAdapter.RandomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.random_list, parent, false);
        return new RandomAdapter.RandomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RandomAdapter.RandomHolder holder, int position) {
        Meal meal = randomList.get(position);

        String cuisineSetText = meal.getCuisine() + " cuisine"; // due warning

        holder.name.setText(meal.getName());
        holder.cuisine.setText(cuisineSetText);
        Glide.with(context).load(meal.getPicture()).into(holder.picture);

        holder.relativeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("name", meal.getName());
            bundle.putString("cuisine", meal.getCuisine());
            bundle.putString("picture", meal.getPicture());
            bundle.putString("instructions", meal.getInstructions());
            bundle.putString("ingredients", meal.getIngredients());

            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return randomList.size();
    }

    public class RandomHolder extends RecyclerView.ViewHolder {
        TextView name, cuisine;
        ImageView picture;
        RelativeLayout relativeLayout;

        public RandomHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            cuisine = itemView.findViewById(R.id.cuisine);
            picture = itemView.findViewById(R.id.picture);
            relativeLayout = itemView.findViewById(R.id.randomList);
        }
    }
}
