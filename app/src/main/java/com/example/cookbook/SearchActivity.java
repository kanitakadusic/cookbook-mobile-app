package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchView;
    private RequestQueue requestQueue;
    private List<Meal> searchList;

    private MealAdapter mealAdapter;
    private RelativeLayout noResultsFound;

    private int max = 0;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        searchList = new ArrayList<>();
        mealAdapter = new MealAdapter(SearchActivity.this, searchList);

        searchView = findViewById(R.id.searchView);
        searchView.setHasFixedSize(true);
        searchView.setAdapter(mealAdapter);
        searchView.setLayoutManager(new LinearLayoutManager(this));

        noResultsFound = findViewById(R.id.noResultsFound);
        fetchSearch("chocolate");

        // TAKING INPUT FROM USER

        EditText searchFood = findViewById(R.id.searchFood);
        searchFood.setText("chocolate");
        searchFood.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);

        searchFood.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchList.clear();
                fetchSearch(searchFood.getText().toString());
                return true;
            }

            return false;
        });

        // NAVIGATION

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.search);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.search:
                    return true;
                case R.id.filter:
                    startActivity(new Intent(getApplicationContext(), FilterActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    // FETCHING API DATA

    private void fetchSearch(String searchResult) {
        String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + searchResult;

        noResultsFound.setVisibility(View.INVISIBLE);
        max = 0;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray array = response.getJSONArray("meals");

                for (int i = 0; i < array.length(); i++) {
                    max++;
                    if (max > 10) break;

                    try {
                        JSONObject jsonObject = array.getJSONObject(i);

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
                        searchList.add(meal);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                searchView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                mealAdapter.notifyItemInserted(searchList.size() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
                noResultsFound.setVisibility(View.VISIBLE);
            }
        }, error -> Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }
}