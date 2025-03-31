package com.example.zapisnik;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText etUsernameOrEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsernameOrEmail = view.findViewById(R.id.etUsernameOrEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvSignup = view.findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(usernameOrEmail, password);
            }
        });

        // Set up clickable "Sign up" text.
        SpannableString spannableString = new SpannableString(tvSignup.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Navigate to RegistrationFragment when "Sign up" is clicked.
                RegistrationFragment registrationFragment = new RegistrationFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, registrationFragment)
                        .commit();
            }
        };
        String text = tvSignup.getText().toString();
        int start = text.indexOf("Sign up");
        int end = start + "Sign up".length();
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.setText(spannableString);
        tvSignup.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private void loginUser(String usernameOrEmail, String password) {
        // Check if network is available
        if (!isNetworkAvailable()) {
            // Offline login: Compare with stored credentials (these should have been saved on a previous successful login)
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String storedUsername = prefs.getString("username", "");
            String storedEmail = prefs.getString("email", "");
            String storedPassword = prefs.getString("password", "");  // Ensure password was stored on successful online login.
            int storedUserId = prefs.getInt("userId", 0);

            if (storedUserId != 0 &&
                    ((storedUsername.equals(usernameOrEmail)) || (storedEmail.equals(usernameOrEmail))) &&
                    storedPassword.equals(password)) {
                Toast.makeText(getActivity(), "Offline login successful", Toast.LENGTH_SHORT).show();

                // Redirect to ProfileFragment after successful offline login
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToFragment(new ProfileFragment(), R.id.nav_profile);
                }
            } else {
                Toast.makeText(getActivity(), "Offline login failed. Please connect to the internet for first-time login.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Online login process
        UserApi api = RetrofitClient.getUserApi();
        Call<LoginResponse> call = api.loginUser(usernameOrEmail, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        // Store user data in SharedPreferences, including password for offline login.
                        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", loginResponse.getUser().getId());
                        editor.putString("username", loginResponse.getUser().getUsername());
                        editor.putString("email", loginResponse.getUser().getEmail());
                        editor.putString("password", password);
                        editor.apply();

                        Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();

                        // Corrected navigation logic
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).navigateToFragment(new ProfileFragment(), R.id.nav_profile);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Login failed: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Login failed: Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
