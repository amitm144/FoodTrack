package com.example.foodtrack.Api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodtrack.Model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodApi {
    private static final String URL = "https://data.gov.il/api/3/action/datastore_search?resource_id=c3cb0630-0650-46c1-a068-82d575c094b2&limit=4624&q=";
    private RequestQueue mQueue;

    public FoodApi(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    public void init(FoodApiListener foodApiListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<Food> foodDB = new ArrayList<>();

                    // Extract the records array from the response
                    JSONArray jsonArray = response.getJSONObject("result").getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject foodFromApi = jsonArray.getJSONObject(i);

                        // Extract the necessary fields from the JSON object
                        String name = foodFromApi.getString("english_name");
                        int calories = foodFromApi.getInt("food_energy");
                        int protein = foodFromApi.getInt("protein");
                        double carbs = foodFromApi.isNull("carbohydrates") ? 0.0 : foodFromApi.getDouble("carbohydrates");
                        int fat = foodFromApi.getInt("total_fat");

                        // Create a new Food object and populate its fields
                        Food food = new Food();
                        food.setName(name).setEnergy(calories).setProtein(protein).setCarb(carbs).setFat(fat).setFrom("API");
                        foodDB.add(food);
                    }

                    // Call the listener with the populated foodDB list
                    foodApiListener.onFoodDBReceived(foodDB);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    public interface FoodApiListener {
        void onFoodDBReceived(ArrayList<Food> foodDB);
    }
}
