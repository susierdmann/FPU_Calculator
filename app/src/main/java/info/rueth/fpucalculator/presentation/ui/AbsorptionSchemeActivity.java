package info.rueth.fpucalculator.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.rueth.fpucalculator.R;
import info.rueth.fpucalculator.domain.repository.AbsorptionSchemeRepository;
import info.rueth.fpucalculator.presentation.adapter.AbsorptionBlockAdapter;
import info.rueth.fpucalculator.presentation.ui.dialogs.AddAbsorptionBlockDialog;
import info.rueth.fpucalculator.presentation.ui.dialogs.IAddAbsorptionBlockListener;
import info.rueth.fpucalculator.presentation.viewmodels.AbsorptionBlockViewModel;
import info.rueth.fpucalculator.usecases.AbsorptionSchemeSave;

public class AbsorptionSchemeActivity extends AppCompatActivity implements IAddAbsorptionBlockListener {

    private AbsorptionBlockAdapter adapter;

    private RecyclerView recyclerView;
    private Button buttonAdd;
    private Button buttonReset;
    private Button buttonSave;
    private Button buttonCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editabsorptionscheme);

        // Create recycler view
        recyclerView = findViewById(R.id.recyclerview_absorptionblock);
        adapter = new AbsorptionBlockAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Attach data observer
        try {
            AbsorptionSchemeRepository.getInstance(getApplication()).getAbsorptionBlockViewModel().observe(this, adapter::setAbsorptionBlocks);
        } catch (IOException e) {
            Toast.makeText(this, R.string.err_absorptionschemefilenotfound, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Add and loadDefault buttons
        buttonAdd = findViewById(R.id.button_absorptionblock_add);
        buttonReset = findViewById(R.id.button_absorptionscheme_reset);

        // Save and cancel buttons
        buttonSave = findViewById(R.id.button_absorptionscheme_save);
        buttonCancel = findViewById(R.id.button_absorptionscheme_cancel);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set onClickListener to add button
        buttonAdd.setOnClickListener(v -> {
            AddAbsorptionBlockDialog dialog = new AddAbsorptionBlockDialog();
            Bundle args = new Bundle();
            args.putIntegerArrayList(AddAbsorptionBlockDialog.FPU_KEY, adapter.getFreeFPUValues());
            args.putIntegerArrayList(AddAbsorptionBlockDialog.ABSORPTIONTIME_KEY, adapter.getFreeAbsorptionTimeValues());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "AddAbsorptionBlockDialogFragment");
        });

        // Set onClickListener to reset button
        buttonReset.setOnClickListener(v -> {
            try {
                AbsorptionSchemeRepository.getInstance(getApplicationContext()).reset().observe(this, adapter::setAbsorptionBlocks);
                Toast.makeText(getApplicationContext(), R.string.hint_absorptionscheme_reset, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AbsorptionSchemeActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.err_absorptionschemefilenotfound, Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener to save button
        buttonSave.setOnClickListener(view -> {
            // Save latest absorption blocks in case the user made some changes
            try {
                new AbsorptionSchemeSave(getBaseContext()).execute(recyclerView);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.err_absorptionschemefilenotfound, Toast.LENGTH_SHORT).show();
            }

            // Go back to main activity
            Intent intent = new Intent(AbsorptionSchemeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Set onClickListener to cancel button
        buttonCancel.setOnClickListener(view -> {
            // Return to main activity without saving
            Intent intent = new Intent(AbsorptionSchemeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // De-register buttons
        buttonAdd.setOnClickListener(null);
        buttonReset.setOnClickListener(null);
        buttonSave.setOnClickListener(null);
        buttonCancel.setOnClickListener(null);
    }

    @Override
    public void addAbsorptionBlock(AbsorptionBlockViewModel absorptionBlock) {
        String errorMessage = adapter.addAbsorptionBlock(absorptionBlock);
        if (errorMessage != null) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
