package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView filterView;
    private RequestQueue requestQueue;
    private List<Meal> filterList;

    private MealAdapter filterAdapter;
    private RelativeLayout noResultsFound;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        filterList = new ArrayList<>();
        filterAdapter = new MealAdapter(ResultsActivity.this, filterList);

        filterView = findViewById(R.id.filterView);
        filterView.setHasFixedSize(true);
        filterView.setAdapter(filterAdapter);
        filterView.setLayoutManager(new LinearLayoutManager(this));

        noResultsFound = findViewById(R.id.noFilterResultsFound);

        ArrayList<Integer> filteredResults = getIntent().getIntegerArrayListExtra("filteredResults");
        fetchFilter(filteredResults);

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
                    ResultsActivity.this.finish();
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    // LISTING FILTERED MEAL RESULTS

    private void fetchFilter(ArrayList<Integer> filteredResults) {
        int max = 0;

        for (int i = 0; i < filteredResults.size(); i++) {
            max++;
            if (max > 10) break;

            String url = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + filteredResults.get(i);
            noResultsFound.setVisibility(View.INVISIBLE);

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
                        filterList.add(meal);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    filterView.setLayoutManager(new GridLayoutManager(ResultsActivity.this, 2));
                    filterAdapter.notifyItemInserted(filterList.size() - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                    noResultsFound.setVisibility(View.VISIBLE);
                }
            }, error -> Toast.makeText(ResultsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(jsonObjectRequest);
        }
    }
}