package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MealActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        TextView name = findViewById(R.id.mealName);
        TextView cuisine = findViewById(R.id.mealCuisine);
        ImageView picture = findViewById(R.id.mealImage);
        TextView instructions = findViewById(R.id.mealInstructions);
        TextView ingredients = findViewById(R.id.mealIngredients);

        Bundle bundle = getIntent().getExtras();

        String mName = bundle.getString("name");
        String mCuisine = bundle.getString("cuisine") + " cuisine";
        String mPicture = bundle.getString("picture");
        String mInstructions = bundle.getString("instructions");
        String mIngredients = bundle.getString("ingredients");

        name.setText(mName);
        cuisine.setText(mCuisine);
        Glide.with(this).load(mPicture).into(picture);
        instructions.setText(mInstructions);
        ingredients.setText(mIngredients);

        // FLOATING BACK BUTTON

        FloatingActionButton fab = findViewById(R.id.backButton);

        fab.setOnClickListener(view -> {
            MealActivity.this.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // INGREDIENTS & INSTRUCTIONS SWITCH

        RadioButton ingredients_button = findViewById(R.id.ingredientsRadio);
        RadioButton instructions_button = findViewById(R.id.instructionsRadio);

        LinearLayout ingredients_layout = findViewById(R.id.ingredientsLayout);
        LinearLayout instructions_layout = findViewById(R.id.instructionsLayout);
        instructions_layout.setVisibility(View.GONE);

        ingredients_button.setOnClickListener(view -> {
            ingredients_layout.setVisibility(View.VISIBLE);
            instructions_layout.setVisibility(View.GONE);
        });

        instructions_button.setOnClickListener(view -> {
            ingredients_layout.setVisibility(View.GONE);
            instructions_layout.setVisibility(View.VISIBLE);
        });
    }
}