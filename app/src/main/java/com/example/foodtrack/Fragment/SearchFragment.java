package com.example.foodtrack.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.foodtrack.Adapter.RecycleView_DailyFood_Adapter;
import com.example.foodtrack.Dialog.AddFoodDialog;
import com.example.foodtrack.Model.CurrentUserSingleton;
import com.example.foodtrack.Model.FireBaseAction;
import com.example.foodtrack.Model.Food;
import com.example.foodtrack.Model.User;
import com.example.foodtrack.databinding.FragmentSearchBinding;
import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecycleView_DailyFood_Adapter food_adapter;
    private SearchView searchView;
    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        searchView = binding.searchView;
        user = CurrentUserSingleton.getInstance().getCurrentUser();

        // Initialize the RecyclerView
        initRecycleView();

        // Handle search view interactions
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Hide the keyboard and search for food items
                hideKeyboard();
                getFoodFromServer(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Search for food items as the text changes
                getFoodFromServer(newText);
                return false;
            }
        });

        return binding.getRoot();
    }

    private void hideKeyboard() {
        // Hide the keyboard when called
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }

    private void initRecycleView() {
        // Initialize the RecyclerView and its adapter
        food_adapter = new RecycleView_DailyFood_Adapter(new ArrayList<>());
        food_adapter.setOnFoodClickListener(new RecycleView_DailyFood_Adapter.OnFoodClickListener() {
            @Override
            public void onClick(View view, Food food, int position) {
                // Show the add food dialog when a food item is clicked
                showAddFoodDialog(food);
            }

            @Override
            public void onLongClick(View view, Food food, int position) {
                // Long click action (if needed)
            }
        });

        // Set up the RecyclerView with a LinearLayoutManager and the adapter
        binding.SearchFragmentSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.SearchFragmentSearchList.setHasFixedSize(true);
        binding.SearchFragmentSearchList.setAdapter(food_adapter);
    }

    private void showAddFoodDialog(Food food) {
        // Show the add food dialog for the selected food item
        AddFoodDialog addFoodDialog = new AddFoodDialog(getContext(), food, user);
        addFoodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addFoodDialog.show();
    }

    public void getFoodFromServer(String newText) {
        // Retrieve food items from the server based on the search text
        FireBaseAction.getFoodByText(newText, user.getPhoneNumber(), new FireBaseAction.FoodSearchCallback() {
            @Override
            public void onFoodSearchComplete(ArrayList<Food> foods) {
                // Update the food items in the adapter
                food_adapter.updateList(foods);
            }
        });
    }
}
