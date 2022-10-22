package com.lilianortizcosta.virtwardrobe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lilianortizcosta.virtwardrobe.databinding.ActivityMainBinding;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    public NavController navController;
    private boolean isUserLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favourites, R.id.navigation_my_wardrobe)
                .build();
        navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(
                this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        LoggedUserButtons();

        BiometricAuthentication();

        NewClotheCameraAction();
    }

    protected void LoggedUserButtons() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        FloatingActionButton floatActionButton = findViewById(R.id.floatingActionButton);
        ScrollView authScreenView = findViewById(R.id.AuthScreenView);
        ScrollView customHomeView = findViewById(R.id.CustomHomeView);

        if(!isUserLogged) {
            navView.setVisibility(View.INVISIBLE);
            floatActionButton.setVisibility(View.INVISIBLE);
            customHomeView.setVisibility(View.INVISIBLE);

            authScreenView.setVisibility(View.VISIBLE);
        } else {
            authScreenView.setVisibility(View.INVISIBLE);

            navView.setVisibility(View.VISIBLE);
            floatActionButton.setVisibility(View.VISIBLE);
            customHomeView.setVisibility(View.VISIBLE);
        }
    }

    protected void BiometricAuthentication() {
        Button unlockActionButton = findViewById(R.id.unlock_button);

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.biometric_error, errString), Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        getText(R.string.biometric_succeeded), Toast.LENGTH_SHORT).show();
                isUserLogged = true;
                LoggedUserButtons();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), getText(R.string.biometric_failed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        unlockActionButton.setOnClickListener(v -> {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build();
            biometricPrompt.authenticate(promptInfo);
        });
    }

    protected void NewClotheCameraAction() {
        FloatingActionButton floatActionButton = findViewById(R.id.floatingActionButton);
        floatActionButton.setOnClickListener(v -> navController.navigate(R.id.newItemFragment));
    }

    @Override
    protected void onStop() {
        isUserLogged = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}