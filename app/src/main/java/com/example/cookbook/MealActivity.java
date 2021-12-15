package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MealActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.backButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MealActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        RadioButton ingredients_button = (RadioButton) findViewById(R.id.ingredientsRadio);
        RadioButton instructions_button = (RadioButton) findViewById(R.id.instructionsRadio);

        LinearLayout ingredients_layout = (LinearLayout) findViewById(R.id.ingredientsLayout);
        LinearLayout instructions_layout = (LinearLayout) findViewById(R.id.instructionsLayout);
        instructions_layout.setVisibility(View.INVISIBLE);

        ingredients_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredients_layout.setVisibility(View.VISIBLE);
                instructions_layout.setVisibility(View.INVISIBLE);
            }
        });

        instructions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredients_layout.setVisibility(View.INVISIBLE);
                instructions_layout.setVisibility(View.VISIBLE);
            }
        });
    }
}