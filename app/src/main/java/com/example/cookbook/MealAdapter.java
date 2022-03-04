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

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealHolder> {
    private Context context;
    private List<Meal> mealList;

    public MealAdapter(Context context, List<Meal> meals) {
        this.context = context;
        mealList = meals;
    }

    @NonNull
    @Override
    public MealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_list, parent, false);
        return new MealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealHolder holder, int position) {
        Meal meal = mealList.get(position);

        holder.name.setText(meal.getName());
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
        return mealList.size();
    }

    public class MealHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView picture;
        RelativeLayout relativeLayout;

        public MealHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            picture = itemView.findViewById(R.id.picture);
            relativeLayout = itemView.findViewById(R.id.mealList);
        }
    }
}
