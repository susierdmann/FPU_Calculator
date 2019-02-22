package info.rueth.fpucalculator;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NewFoodActivity extends AppCompatActivity {

    public static final String FOOD_POSITION = "foodposition";
    public static final String EXTRA_REPLY_NAME = "info.rueth.fpucalculator.foodlistsql.REPLY_NAME";
    public static final String EXTRA_REPLY_FAVORITE = "info.rueth.fpucalculator.foodlistsql.REPLY_FAVORITE";
    public static final String EXTRA_REPLY_CALORIES = "info.rueth.fpucalculator.foodlistsql.REPLY_CALORIES";
    public static final String EXTRA_REPLY_CARBS = "info.rueth.fpucalculator.foodlistsql.REPLY_CARBS";

    private EditText mFoodName;
    private Switch mFavorite;
    private EditText mCalories;
    private EditText mCarbs;

    private int foodPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food);

        mFoodName = findViewById(R.id.foodname);
        mFavorite = findViewById(R.id.favorite);
        mCarbs = findViewById(R.id.carbs);
        mCalories = findViewById(R.id.calories);

        // Fill data if food is edited
        String name = getIntent().getStringExtra(EXTRA_REPLY_NAME);
        if (name != null) {
            foodPosition = getIntent().getIntExtra(FOOD_POSITION, -1);
            boolean favorite = getIntent().getBooleanExtra(EXTRA_REPLY_FAVORITE, false);
            double carbs = getIntent().getDoubleExtra(EXTRA_REPLY_CARBS, 0f);
            double calories = getIntent().getDoubleExtra(EXTRA_REPLY_CALORIES, 0f);

            mFoodName.setText(name);
            mFavorite.setChecked(favorite);
            mCalories.setText(String.valueOf(calories), TextView.BufferType.EDITABLE);
            mCarbs.setText(String.valueOf(carbs));
        }
        
        // Save button
        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();

                if (
                        TextUtils.isEmpty(mFoodName.getText()) ||
                        TextUtils.isEmpty(mCalories.getText()) ||
                        TextUtils.isEmpty(mCarbs.getText())
                ) {
                    String errMsg = getResources().getString(R.string.newfood_error_datamissing);
                    Toast.makeText(
                            getApplicationContext(),
                            errMsg,
                            Toast.LENGTH_LONG).show();
                } else {
                    String foodName = mFoodName.getText().toString();
                    boolean favorite = mFavorite.isChecked();
                    double calories = Double.parseDouble(mCalories.getText().toString());
                    double carbs = Double.parseDouble(mCarbs.getText().toString());

                    // Check if all numbers are positive
                    if (calories < 0 || carbs < 0) {
                        String errMsg = getResources().getString(R.string.newfood_error_carbsorcalbelowzero) + foodName;
                        Toast.makeText(
                                getApplicationContext(),
                                errMsg,
                                Toast.LENGTH_LONG).show();
                    } else if (carbs * 4 > calories) {
                        // Do a consistency check: Calories from carbs cannot be more than
                        // total calories; 1g of carbs has 4 kcal
                        String errMsg =
                                getResources().getString(R.string.newfood_error_calslargercarbs1)
                                + foodName
                                + getResources().getString(R.string.newfood_error_calslargercarbs2)
                                + (carbs * 4) + getResources().getString(R.string.newfood_error_calslargercarbs3)
                                + calories;
                        Toast.makeText(
                                getApplicationContext(),
                                errMsg,
                                Toast.LENGTH_LONG).show();
                    } else {

                        replyIntent.putExtra(FOOD_POSITION, foodPosition);
                        replyIntent.putExtra(EXTRA_REPLY_NAME, foodName);
                        replyIntent.putExtra(EXTRA_REPLY_FAVORITE, favorite);
                        replyIntent.putExtra(EXTRA_REPLY_CALORIES, calories);
                        replyIntent.putExtra(EXTRA_REPLY_CARBS, carbs);

                        setResult(RESULT_OK, replyIntent);
                        finish();
                    }
                }
            }
        });
        
        // Cancel button
        final Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                setResult(RESULT_CANCELED, replyIntent);
                finish();
            }
        });
    }
}
