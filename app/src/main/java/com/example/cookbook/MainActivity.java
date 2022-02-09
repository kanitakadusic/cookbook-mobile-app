package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView randomView, mealView;
    private RequestQueue requestQueue;
    private List<Meal> randomList, mealList;

    private MealAdapter mealAdapter;
    private RandomAdapter randomAdapter;

    private int max = 0;

    private final ArrayList<Integer> idListMain = new ArrayList<>(Arrays.asList(52959, 52819, 52884, 52773, 52946, 52960, 52823, 53032, 52953, 52852, 52796, 52765, 52818, 52832, 52951, 52945, 53039, 52780, 52913, 52891, 52976, 52898, 52856, 52928, 52966, 52827, 52905, 52990, 52859, 52924, 52854, 52958, 52901, 52833, 53005, 52917, 52839, 52838, 53034, 52794, 52775, 52870, 52908, 52816, 52963));
    private ArrayList<Integer> idListEdit;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        idListEdit = idListMain;

        randomList = new ArrayList<>();
        randomAdapter = new RandomAdapter(MainActivity.this, randomList);

        randomView = findViewById(R.id.randomView);
        randomView.setHasFixedSize(true);
        randomView.setAdapter(randomAdapter);
        randomView.setLayoutManager(new LinearLayoutManager(this));

        fetchRandom();

        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(MainActivity.this, mealList);

        mealView = findViewById(R.id.mealView);
        mealView.setHasFixedSize(true);
        mealView.setAdapter(mealAdapter);
        mealView.setLayoutManager(new LinearLayoutManager(this));

        fetchMeals(new String[]{"Starter", "Side"});

        // RADIO BUTTONS FOR CATEGORIES

        RadioGroup radioGroup = findViewById(R.id.radioCategories);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mealList.clear();

            switch (checkedId) {
                case R.id.starterRadio:
                    fetchMeals(new String[]{"Starter", "Side"});
                    break;
                case R.id.mainRadio:
                    fetchMeals(new String[]{"Chicken", "Miscellaneous", "Beef", "Lamb", "Goat", "Pork"});
                    break;
                case R.id.dessertRadio:
                    fetchMeals(new String[]{"Dessert"});
                    break;
                case R.id.breakfastRadio:
                    fetchMeals(new String[]{"Breakfast"});
                    break;
                case R.id.pastaRadio:
                    fetchMeals(new String[]{"Pasta"});
                    break;
                case R.id.otherRadio:
                    fetchMeals(new String[]{"Seafood", "Vegan", "Vegetarian"});
            }
        });

        // NAVIGATION

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    return true;
                case R.id.search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.filter:
                    startActivity(new Intent(getApplicationContext(), FilterActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    // OUR SUGGESTION - RANDOM PART

    private String randomNumber() {
        Random rand = new Random();

        int index = rand.nextInt(idListEdit.size());
        String id = String.valueOf(idListEdit.get(index));
        idListEdit.remove(index);

        return id;
    }

    // FETCHING API DATA

    private void fetchRandom() {
        for (int i = 0; i < 3; i++) {
            String url = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + randomNumber();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    JSONArray array = response.getJSONArray("meals");

                    try {
                        JSONObject jsonObject = array.getJSONObject(0);

                        String name = jsonObject.getString("strMeal");
                        String cuisine = jsonObject.getString("strArea");
                        String picture = jsonObject.getString("strMealThumb");
                        String instructions = jsonObject.getString("strInstructions");
                        String ingredients = "";

                        int ingNum = 2;
                        String oneIng = jsonObject.getString("strIngredient1"), oneMea = jsonObject.getString("strMeasure1");

                        while (!oneIng.isEmpty() && !oneIng.equals("null") && ingNum <= 20) {
                            ingredients += "○   " + oneIng.toLowerCase() + "   ◦   " + oneMea.toLowerCase() + "\n";
                            oneIng = jsonObject.getString("strIngredient" + ingNum);
                            oneMea = jsonObject.getString("strMeasure" + ingNum);
                            ingNum++;
                        }

                        if (!oneIng.isEmpty() && !oneIng.equals("null") && ingNum == 21) {
                            ingredients += "○   " + oneIng.toLowerCase() + "   ◦   " + oneMea.toLowerCase() + "\n";
                        }

                        Meal meal = new Meal(name, cuisine, picture, instructions, ingredients);
                        randomList.add(meal);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    randomView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                    randomAdapter.notifyItemInserted(randomList.size() - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(jsonObjectRequest);
        }
    }

    // LISTING MEALS BY CATEGORY

    private void fetchMeals(String[] filter) {
        max = 0;

        for (String category : filter) {
            String url = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + category;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    JSONArray array = response.getJSONArray("meals");

                    for (int i = 0; i < array.length(); i++) {
                        max++;
                        if (max > 10) break;

                        try {
                            JSONObject jsonObject = array.getJSONObject(i);

                            String id = jsonObject.getString("idMeal");
                            String idUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + id;

                            // NEW REQUEST FOR SINGLE MEAL BASED ON ID

                            JsonObjectRequest idJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, idUrl, null, idResponse -> {
                                try {
                                    JSONArray idArray = idResponse.getJSONArray("meals");

                                    try {
                                        JSONObject idJsonObject = idArray.getJSONObject(0);

                                        String name = idJsonObject.getString("strMeal");
                                        String cuisine = idJsonObject.getString("strArea");
                                        String picture = idJsonObject.getString("strMealThumb");
                                        String instructions = idJsonObject.getString("strInstructions");
                                        String ingredients = "";

                                        int ingNum = 2;
                                        String oneIng = idJsonObject.getString("strIngredient1"), oneMea = idJsonObject.getString("strMeasure1");

                                        while (!oneIng.isEmpty() && !oneIng.equals("null") && ingNum <= 20) {
                                            ingredients += "○   " + oneIng.toLowerCase() + "   ◦   " + oneMea.toLowerCase() + "\n";
                                            oneIng = idJsonObject.getString("strIngredient" + ingNum);
                                            oneMea = idJsonObject.getString("strMeasure" + ingNum);
                                            ingNum++;
                                        }

                                        if (!oneIng.isEmpty() && !oneIng.equals("null") && ingNum == 21) {
                                            ingredients += "○   " + oneIng.toLowerCase() + "   ◦   " + oneMea.toLowerCase() + "\n";
                                        }

                                        Meal meal = new Meal(name, cuisine, picture, instructions, ingredients);
                                        mealList.add(meal);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    mealView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                                    mealAdapter.notifyItemInserted(mealList.size() - 1);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

                            requestQueue.add(idJsonObjectRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(jsonObjectRequest);
        }
    }
}