package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class FilterActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    private ArrayList<String> selectedCategories, selectedCuisines;
    private String ingredientInput = "";

    private final String urlCategory = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";
    private final String urlCuisine = "https://www.themealdb.com/api/json/v1/1/filter.php?a=";
    private final String urlIngredient = "https://www.themealdb.com/api/json/v1/1/filter.php?i=";

    HashSet<Integer> filteredResults;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        android.widget.ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        selectedCategories = new ArrayList<>(Arrays.asList("Seafood", "Chicken", "Beef", "Lamb", "Goat", "Pork", "Vegan", "Vegetarian", "Starter", "Pasta", "Breakfast", "Miscellaneous", "Side", "Dessert"));
        selectedCuisines = new ArrayList<>();

        filteredResults = new HashSet<>();

        // TAKING INPUT - MAIN INGREDIENT - FROM USER

        EditText searchIngredient = findViewById(R.id.searchIngredient);
        searchIngredient.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);

        searchIngredient.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                ingredientInput = searchIngredient.getText().toString();
                return true;
            }

            return false;
        });

        // CATEGORY FILTER - RADIO

        RadioGroup radioGroup = findViewById(R.id.radioFilter);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCategories.clear();

            switch (checkedId) {
                case R.id.allRadio:
                    selectedCategories.add("Seafood");
                    selectedCategories.add("Chicken");
                    selectedCategories.add("Beef");
                    selectedCategories.add("Lamb");
                    selectedCategories.add("Goat");
                    selectedCategories.add("Pork");
                    selectedCategories.add("Vegan");
                    selectedCategories.add("Vegetarian");
                    selectedCategories.add("Starter");
                    selectedCategories.add("Pasta");
                    selectedCategories.add("Breakfast");
                    selectedCategories.add("Miscellaneous");
                    selectedCategories.add("Side");
                    selectedCategories.add("Dessert");
                    break;
                case R.id.seafoodRadio:
                    selectedCategories.add("Seafood");
                    break;
                case R.id.meatRadio:
                    selectedCategories.add("Chicken");
                    selectedCategories.add("Beef");
                    selectedCategories.add("Lamb");
                    selectedCategories.add("Goat");
                    selectedCategories.add("Pork");
                    break;
                case R.id.vegetarianRadio:
                    selectedCategories.add("Vegan");
                    selectedCategories.add("Vegetarian");
                    break;
                case R.id.lightRadio:
                    selectedCategories.add("Starter");
                    selectedCategories.add("Pasta");
                    selectedCategories.add("Breakfast");
                    break;
                case R.id.miscellaneousRadio:
                    selectedCategories.add("Miscellaneous");
                    selectedCategories.add("Side");
                    break;
                case R.id.sweetsRadio:
                    selectedCategories.add("Dessert");
            }
        });

        // APPLY BUTTON

        Button imageView = findViewById(R.id.applyButton);

        imageView.setOnClickListener(view -> {

            if (selectedCuisines.isEmpty()) {
                Toast.makeText(FilterActivity.this, "please select at least one cuisine", Toast.LENGTH_SHORT).show();
            } else if (ingredientInput.equals("")) {
                progressBar.setVisibility(View.VISIBLE);

                Thread thread = new Thread(() -> {
                    filteredResults = getIdList(selectedCategories, urlCategory);
                    HashSet<Integer> idCuisine = getIdList(selectedCuisines, urlCuisine);

                    filteredResults.retainAll(idCuisine);

                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                    startActivity(new Intent(getApplicationContext(), ResultsActivity.class).putExtra("filteredResults", new ArrayList<>(filteredResults)));
                    overridePendingTransition(0, 0);
                });
                thread.start();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                Thread thread = new Thread(() -> {
                    filteredResults = getIdList(selectedCategories, urlCategory);
                    HashSet<Integer> idCuisine = getIdList(selectedCuisines, urlCuisine);
                    HashSet<Integer> idIngredient = getIdList(new ArrayList<>(Collections.singletonList(ingredientInput)), urlIngredient);

                    filteredResults.retainAll(idCuisine);
                    filteredResults.retainAll(idIngredient);

                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                    startActivity(new Intent(getApplicationContext(), ResultsActivity.class).putExtra("filteredResults", new ArrayList<>(filteredResults)));
                    overridePendingTransition(0, 0);
                });
                thread.start();
            }
        });

        // NAVIGATION

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.filter);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.filter:
                    return true;
            }
            return false;
        });
    }

    // CUISINE FILTER - CHECKBOX

    @SuppressLint("NonConstantResourceId")
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.chineseCheckBox:
                if (checked) selectedCuisines.add("Chinese");
                else selectedCuisines.remove("Chinese");
                break;
            case R.id.frenchCheckBox:
                if (checked) selectedCuisines.add("French");
                else selectedCuisines.remove("French");
                break;
            case R.id.italianCheckBox:
                if (checked) selectedCuisines.add("Italian");
                else selectedCuisines.remove("Italian");
                break;
            case R.id.indianCheckBox:
                if (checked) selectedCuisines.add("Indian");
                else selectedCuisines.remove("Indian");
                break;
            case R.id.japaneseCheckBox:
                if (checked) selectedCuisines.add("Japanese");
                else selectedCuisines.remove("Japanese");
                break;
            case R.id.thaiCheckBox:
                if (checked) selectedCuisines.add("Thai");
                else selectedCuisines.remove("Thai");
                break;
            case R.id.turkishCheckBox:
                if (checked) selectedCuisines.add("Turkish");
                else selectedCuisines.remove("Turkish");
                break;
            case R.id.otherCheckBox:
                if (checked) {
                    selectedCuisines.add("American");
                    selectedCuisines.add("British");
                    selectedCuisines.add("Canadian");
                    selectedCuisines.add("Croatian");
                    selectedCuisines.add("Dutch");
                    selectedCuisines.add("Egyptian");
                    selectedCuisines.add("Greek");
                    selectedCuisines.add("Irish");
                    selectedCuisines.add("Jamaican");
                    selectedCuisines.add("Kenyan");
                    selectedCuisines.add("Malaysian");
                    selectedCuisines.add("Mexican");
                    selectedCuisines.add("Moroccan");
                    selectedCuisines.add("Polish");
                    selectedCuisines.add("Portuguese");
                    selectedCuisines.add("Russian");
                    selectedCuisines.add("Spanish");
                    selectedCuisines.add("Tunisian");
                    selectedCuisines.add("Unknown");
                    selectedCuisines.add("Vietnamese");
                } else {
                    selectedCuisines.remove("American");
                    selectedCuisines.remove("British");
                    selectedCuisines.remove("Canadian");
                    selectedCuisines.remove("Croatian");
                    selectedCuisines.remove("Dutch");
                    selectedCuisines.remove("Egyptian");
                    selectedCuisines.remove("Greek");
                    selectedCuisines.remove("Irish");
                    selectedCuisines.remove("Jamaican");
                    selectedCuisines.remove("Kenyan");
                    selectedCuisines.remove("Malaysian");
                    selectedCuisines.remove("Mexican");
                    selectedCuisines.remove("Moroccan");
                    selectedCuisines.remove("Polish");
                    selectedCuisines.remove("Portuguese");
                    selectedCuisines.remove("Russian");
                    selectedCuisines.remove("Spanish");
                    selectedCuisines.remove("Tunisian");
                    selectedCuisines.remove("Unknown");
                    selectedCuisines.remove("Vietnamese");
                }
        }
    }

    // FETCHING API DATA

    private HashSet<Integer> getIdList(ArrayList<String> filterList, String url) {
        HashSet<Integer> idSet = new HashSet<>();

        for (String filter : filterList) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + filter, new JSONObject(), future, future);
            requestQueue.add(jsonObjectRequest);

            try {
                JSONObject response = future.get();

                try {
                    JSONArray array = response.getJSONArray("meals");

                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jsonObject = array.getJSONObject(i);
                            idSet.add(Integer.parseInt(jsonObject.getString("idMeal")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return idSet;
    }
}